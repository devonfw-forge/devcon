package com.devonfw.devcon.modules.oasp4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.lang3.SystemUtils;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Constants;
import com.google.common.base.Optional;

/**
 * This class implements a Command Module with Oasp4j(server project) related commands
 *
 * @author ssarmoka
 */
@CmdModuleRegistry(name = "oasp4j", description = "Oasp4j(server project) related commands")
public class Oasp4j extends AbstractCommandModule {

  /**
   * The constructor.
   */
  public Oasp4j() {

    super();
  }

  /**
   * @param serverpath Path to Server Project
   * @param servername Name of Server Project
   * @param packagename Package Name of Server Project
   * @param groupid Group Id of the Server Project
   * @param version Version of the Server Project
   * @throws IOException
   */
  @Command(name = "create", description = "This command is used to create new server project")
  @Parameters(values = {
  @Parameter(name = "serverpath", description = "Path to create Server project (currentDir if not given)", optional = true),
  @Parameter(name = "servername", description = "Name of project"),
  @Parameter(name = "packagename", description = "package name in server project"),
  @Parameter(name = "groupid", description = "groupid for server project"),
  @Parameter(name = "version", description = "version of server project") })
  public void create(String serverpath, String servername, String packagename, String groupid, String version)
      throws IOException {

    String command =
        new StringBuffer("cmd /c start mvn -DarchetypeVersion=").append(Constants.OASP_TEMPLATE_VERSION)
            .append(" -DarchetypeGroupId=").append(Constants.OASP_TEMPLATE_GROUP_ID).append(" -DarchetypeArtifactId=")
            .append(Constants.OASP_TEMPLATE_GROUP_ID).append(" -DarchetypeArtifactId=")
            .append(Constants.OASP_ARTIFACT_ID).append(" archetype:generate -DgroupId=").append(groupid)
            .append(" -DartifactId=").append(servername).append(" -Dversion=").append(version).append(" -Dpackage=")
            .append(packagename).append(" -DinteractiveMode=false").toString();

    Optional<DistributionInfo> distInfo = getContextPathInfo().getDistributionRoot(serverpath);

    if (!SystemUtils.IS_OS_WINDOWS) {
      getOutput().showMessage("This task is currently only supported on Windows");
      return;
    }

    if (distInfo.isPresent()) {

      serverpath = serverpath.isEmpty() ? getContextPathInfo().getCurrentWorkingDirectory().toString() : serverpath;

      File projectDir = new File(serverpath);

      if (!projectDir.exists()) {
        projectDir.mkdirs();
      }
      File project = new File(serverpath + File.separator + servername);

      if (!project.exists()) {

        Runtime rt = Runtime.getRuntime();
        Process process = null;

        try {
          process = rt.exec(command, null, new File(serverpath));

          int result = process.waitFor();
          if (result == 0) {
            getOutput().showMessage("Project Creation complete");
          } else {
            throw new Exception("Project creation failed");
          }

        } catch (Exception e) {
          e.printStackTrace();
          getOutput().showError("Errr creating workspace: " + e.getMessage());
        }

      } else {
        getOutput().showError("The project " + project.toString() + " already exists!");
      }
    } else {
      getOutput().showError("Not a Devon Distribution Workspace");
    }

  }

  /**
   * @param port Server will be started at this port
   * @param path Path to server project
   */
  @Command(name = "run", description = "runs application from embedded tomcat", context = ContextType.PROJECT)
  @Parameters(values = {
  @Parameter(name = "port", description = "Port to start Spring boot app", optional = false),
  @Parameter(name = "path", description = "Path to Server project Workspace (currentDir if not given)", optional = true) })
  public void run(String port, String path) {

    Process p;
    try {
      String commandStr = "mvn spring-boot:run -Drun.arguments=\"server.port=" + port.trim() + "\" ";
      String cmd = "cmd /c start " + commandStr;//

      p = Runtime.getRuntime().exec(cmd, null, new File(path));
      int result = p.waitFor();
      if (result == 0) {
        getOutput().showMessage("Application started");
      } else {
        throw new Exception();
      }

    } catch (Exception e) {

      getOutput().showError("An error occured during executing oasp4j Cmd");
    }
  }

  /**
   * @param path path to server project
   */
  @Command(name = "build", description = "This command will build the server project", context = ContextType.PROJECT)
  @Parameters(values = { @Parameter(name = "path", description = "Path to Server project Workspace (currentDir if not given)", optional = true) })
  public void build(String path) {

    this.projectInfo = getContextPathInfo().getProjectRoot(path);
    System.out.println("projectInfo read...");
    System.out.println("path " + this.projectInfo.get().getPath() + "project type "
        + this.projectInfo.get().getProjecType());

    Process p;
    try {
      String cmd = "cmd /c start mvn clean install";

      p = Runtime.getRuntime().exec(cmd, null, this.projectInfo.get().getPath().toFile());
      p.waitFor();
      getOutput().showMessage("Completed");
    } catch (Exception e) {

      getOutput().showError("An error occured during executing oasp4j Cmd");
    }
  }

  /**
   * @param deploypath Path to tomcat
   * @param path server project path
   */
  @Command(name = "deploy", description = "This command will deploy the server project on tomcat", context = ContextType.PROJECT)
  @Parameters(values = {
  @Parameter(name = "deploypath", description = "Path to tomcat folder"),
  @Parameter(name = "path", description = "Path to Server project Workspace (currentDir if not given)", optional = true) })
  public void deploy(String deploypath, String path) {

    Optional<DistributionInfo> distInfo = getContextPathInfo().getDistributionRoot(path);
    Path distRootPath = distInfo.get().getPath();
    String distPath = distRootPath.toString();

    getOutput().showMessage("Distribution root path is " + distPath);

    getOutput().showMessage("Targetpath is " + path);

    Process p;
    try {
      String cmd = "cmd /c start src\\main\\resources\\deploy.bat " + distPath + " " + path;

      p = Runtime.getRuntime().exec(cmd, null, null);
      p.waitFor();
      System.out.println("Deploying and starting file...");
    } catch (Exception e) {

      getOutput().showError("An error occured during executing oasp4j Cmd");
    }
  }

}