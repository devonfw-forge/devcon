package com.devonfw.devcon.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.DistributionType;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.common.exception.InvalidConfigurationStateException;
import com.devonfw.devcon.common.impl.DistributionInfoImpl;
import com.devonfw.devcon.common.impl.ProjectInfoImpl;
import com.devonfw.devcon.common.impl.utils.DistributionFolderProcessor;
import com.devonfw.devcon.common.impl.utils.ProjectFolderProcessor;
import com.github.zafarkhaja.semver.Version;
import com.google.common.base.Optional;

/**
 * This class is to be used as a service/singleton, to obtain the Distribution and ProjectInfo types from paths on the
 * file system
 *
 * @author ivanderk
 */
public class ContextPathInfo {

  private static final String OASP_IDE = "oasp-ide";

  private static final String DEVON_DIST = "devon-dist";

  private static final String TYPE = "type";

  private static final String VERSION = "version";

  private static final String CONF_SETTINGS_JSON = "conf/settings.json";

  private static final String DEVON_JSON = "devon.json";

  private static final String COMBINED = "combined";

  private static final String DEVON4SENCHA = "devon4sencha";

  private static final String OASP4JS = "oasp4js";

  private static final String OASP4J = "oasp4j";

  /**
   *
   * @param path get {@link Path} from Strng
   * @return
   */
  private Path getPath(String path) {

    return FileSystems.getDefault().getPath(path);
  }

  /**
   * @TODO to see whether Commons has a better implementation?
   * @return CWD - Current working directory as a Path instance
   */
  private Path getCurrentWorkingDirectory() {

    File file = new File(".");
    return getPath(file.getAbsolutePath());
  }

  /**
   *
   * @return Distribution Info if CWD within a Devon Distrubution or OASP IDE
   */
  public Optional<DistributionInfo> getDistributionRoot() {

    return getDistributionRoot(getCurrentWorkingDirectory());
  }

  /**
   *
   * @param currentDir pass directory as String
   * @return Distribution Info if currentDir within a Devon Distrubution or OASP IDE
   */
  public Optional<DistributionInfo> getDistributionRoot(String currentDir) {

    return getDistributionRoot(getPath(currentDir));
  }

  /**
   *
   * @param currentDir pass directory as Path instance
   * @return Distribution Info if currentDir within a Devon Distrubution or OASP IDE
   */
  public Optional<DistributionInfo> getDistributionRoot(Path currentDir) {

    DistributionFolderProcessor interceptor = new DistributionFolderProcessor();

    try {
      TreeClimber.climb(currentDir, interceptor);
      if (interceptor.isFound()) {

        DistributionInfo info = getDistributionInfo(interceptor.getFoundPath());
        return Optional.of(info);
      } else {
        return Optional.absent();
      }
    } catch (InvalidConfigurationStateException err) {
      throw err;
    } catch (Exception err) {
      throw new InvalidConfigurationStateException(err);
    }
  }

  public DistributionInfo getDistributionInfo(Path distPath) throws FileNotFoundException, IOException, ParseException {

    Path settingsPath = distPath.resolve(CONF_SETTINGS_JSON);
    JSONParser parser = new JSONParser();
    Object obj = parser.parse(new FileReader(settingsPath.toFile()));

    JSONObject json = (JSONObject) obj;
    Version version = Version.valueOf(json.get(VERSION).toString());
    DistributionType distType;
    String disttype = json.get(TYPE).toString();
    if (disttype.toLowerCase().equals(DEVON_DIST)) {
      distType = DistributionType.DevonDist;
    } else if (disttype.toLowerCase().equals(OASP_IDE)) {
      distType = DistributionType.OASPIDE;
    } else {
      throw new InvalidConfigurationStateException("type property does not contain either 'devon-dist' nor 'oasp-ide'");
    }

    /**
     * HERE WE COULD ADD DYNAMIC PROPS
     */

    return new DistributionInfoImpl(distPath, distType, version);
  }

  public Optional<ProjectInfo> getProjectRoot() {

    return getProjectRoot(getCurrentWorkingDirectory());
  }

  public Optional<ProjectInfo> getProjectRoot(String currentDir) {

    return getProjectRoot(getPath(currentDir));
  }

  public Optional<ProjectInfo> getProjectRoot(Path currentDir) {

    ProjectFolderProcessor interceptor = new ProjectFolderProcessor();

    try {
      TreeClimber.climb(currentDir, interceptor);
      if (interceptor.isFound()) {

        ProjectInfo info = getProjectInfo(interceptor.getFoundPath());
        return Optional.of(info);
      } else {
        return Optional.absent();
      }
    } catch (InvalidConfigurationStateException err) {
      throw err;
    } catch (Exception err) {
      throw new InvalidConfigurationStateException(err);
    }

  }

  public ProjectInfo getProjectInfo(Path projectPath) throws FileNotFoundException, IOException, ParseException {

    Path settingsPath = projectPath.resolve(DEVON_JSON);
    JSONParser parser = new JSONParser();
    Object obj = parser.parse(new FileReader(settingsPath.toFile()));

    JSONObject json = (JSONObject) obj;
    Version version = Version.valueOf(json.get(VERSION).toString());
    List<ProjectInfo> projects = new ArrayList<>();
    ProjectType projectType;

    String projtype = json.get(TYPE).toString();
    if (projtype.toLowerCase().equals(COMBINED)) {
      projectType = ProjectType.Combined;

      JSONArray subJson = (JSONArray) json.get("projects");
      for (Object e : subJson) {
        Path resolved = projectPath.resolve(e.toString());
        projects.add(getProjectInfo(resolved));
      }

    } else if (projtype.toLowerCase().equals(OASP4J)) {
      projectType = ProjectType.OASP4J;
    } else if (projtype.toLowerCase().equals(OASP4JS)) {
      projectType = ProjectType.OASP4JS;
    } else if (projtype.toLowerCase().equals(DEVON4SENCHA)) {
      projectType = ProjectType.Devon4Sencha;
    } else {
      throw new InvalidConfigurationStateException(
          "type property does not contain valid ProjectInfoType: 'combined', 'oasp4j', 'oasp4js' or 'devon4sencha' ");
    }

    /**
     * HERE WE COULD ADD DYNAMIC PROPS
     */

    return new ProjectInfoImpl(projectPath, projectType, version, projects);
  }

  /**
   * @param projectPath
   * @return
   */
  public Optional<ProjectInfo> getCombinedProjectRoot(Path projectPath) {

    Optional<ProjectInfo> projectInfo = getProjectRoot(projectPath);
    if (!projectInfo.isPresent() || projectInfo.get().getProjecType().equals(ProjectType.Combined)) {
      return projectInfo;
    } else {
      return getProjectRoot(projectPath.getParent());
    }
  }

  public Optional<ProjectInfo> getCombinedProjectRoot() {

    return this.getCombinedProjectRoot(getCurrentWorkingDirectory());
  }

}
