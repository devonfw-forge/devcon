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
import com.devonfw.devcon.common.exception.InvalidSettingsException;
import com.devonfw.devcon.common.impl.DistributionInfoImpl;
import com.devonfw.devcon.common.impl.ProjectInfoImpl;
import com.devonfw.devcon.common.impl.utils.DistributionFolderInterceptor;
import com.devonfw.devcon.common.impl.utils.ProjectFolderInterceptor;
import com.github.zafarkhaja.semver.Version;
import com.google.common.base.Optional;

/**
 * This class is to be used as a service/singleton
 *
 * @author ivanderk
 * @since 0.0.1
 */
public class ContextPathInfo {

  /**
   *
   */
  public static final String OASP_IDE = "oasp-ide";

  /**
   *
   */
  public static final String DEVON_DIST = "devon-dist";

  /**
   *
   */
  public static final String TYPE = "type";

  /**
   *
   */
  public static final String VERSION = "version";

  /**
   *
   */
  public static final String CONF_SETTINGS_JSON = "conf/settings.json";

  /**
   *
   */
  public static final String DEVON_JSON = "devon.json";

  /**
   *
   */
  public static final String COMBINED = "combined";

  /**
   *
   */
  public static final String DEVON4SENCHA = "devon4sencha";

  /**
   *
   */
  public static final String OASP4JS = "oasp4js";

  /**
   *
   */
  public static final String OASP4J = "oasp4j";

  private Path getPath(String path) {

    return FileSystems.getDefault().getPath(path);
  }

  private Path getCurrentWorkingDirectory() {

    File file = new File(".");
    return getPath(file.getAbsolutePath());
  }

  public Optional<DistributionInfo> getDistributionRoot() {

    return getDistributionRoot(getCurrentWorkingDirectory());
  }

  public Optional<DistributionInfo> getDistributionRoot(String currentDir) {

    return getDistributionRoot(getPath(currentDir));
  }

  public Optional<DistributionInfo> getDistributionRoot(Path currentDir) {

    DistributionFolderInterceptor interceptor = new DistributionFolderInterceptor();

    try {
      TreeClimber.climb(currentDir, interceptor);
      if (interceptor.isFound()) {

        DistributionInfo info = getDistributionInfo(interceptor.getFoundPath());
        return Optional.of(info);
      } else {
        return Optional.absent();
      }
    } catch (Exception err) {
      System.err.println(err);
      return Optional.absent();
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
      throw new InvalidSettingsException("type property does not contain either 'devon-dist' nor 'oasp-ide'");
    }

    return new DistributionInfoImpl(distPath, distType, version);
  }

  public Optional<ProjectInfo> getProjectRoot() {

    return getProjectRoot(getCurrentWorkingDirectory());
  }

  public Optional<ProjectInfo> getProjectRoot(String currentDir) {

    return getProjectRoot(getPath(currentDir));
  }

  public Optional<ProjectInfo> getProjectRoot(Path currentDir) {

    ProjectFolderInterceptor interceptor = new ProjectFolderInterceptor();

    try {
      TreeClimber.climb(currentDir, interceptor);
      if (interceptor.isFound()) {

        ProjectInfo info = getProjectInfo(interceptor.getFoundPath());
        return Optional.of(info);
      } else {
        return Optional.absent();
      }
    } catch (Exception err) {
      System.err.println(err);
      return Optional.absent();
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
      throw new InvalidSettingsException(
          "type property does not contain valid ProjectInfoType: 'combined', 'oasp4j', 'oasp4js' or 'devon4sencha' ");
    }

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

}
