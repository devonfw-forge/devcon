package com.devonfw.devcon.modules.oasp4j;

import java.io.File;
import java.nio.file.Path;

import org.apache.commons.lang3.SystemUtils;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.google.common.base.Optional;

/**
 * TODO ssarmoka This type ...
 *
 * @author ssarmoka
 */
@CmdModuleRegistry(name = "oasp4j", description = "Oasp4j(server project) related commands")
public class Oasp4j extends AbstractCommandModule {

  private final String OASP_TEMPLATE_VERSION = "2.0.0";

  private final String OASP_TEMPLATE_GROUP_ID = "io.oasp.java.templates";

  private final String OASP_ARTIFACT_ID = "oasp4j-template-server";

  /**
   * The constructor.
   */
  public Oasp4j() {
    super();
  }

  @Command(name = "create", help = "This command is used to create new server project")
  @Parameters(values = {
  @Parameter(name = "serverpath", description = "Path to Server project Workspace (currentDir if not given)", optional = true),
  @Parameter(name = "servername", description = "Name of project"),
  @Parameter(name = "packagename", description = "package name in server project"),
  @Parameter(name = "groupid", description = "groupid for server project"),
  @Parameter(name = "version", description = "version of server project") })
  public void create(String serverpath, String servername, String packagename, String groupid, String version) {

    String command = "cmd /c start mvn -DarchetypeVersion=" + this.OASP_TEMPLATE_VERSION + " -DarchetypeGroupId="
        + this.OASP_TEMPLATE_GROUP_ID + " -DarchetypeArtifactId=" + this.OASP_ARTIFACT_ID
        + " archetype:generate -DgroupId=" + groupid + " -DartifactId=" + servername + " -Dversion=" + version
        + " -Dpackage=" + packagename + " -DinteractiveMode=false";

    Optional<DistributionInfo> distInfo = getContextPathInfo().getDistributionRoot(serverpath);

    if (!SystemUtils.IS_OS_WINDOWS) {
      getOutput().showMessage("This task is currently only supported on Windows");
      return;
    }

    if (distInfo.isPresent()) {
      Path distPath = distInfo.get().getPath();

      if (!new File(distPath + "\\workspaces\\" + servername).exists()) {

        Runtime rt = Runtime.getRuntime();
        Process process = null;
        String targetPath = distPath + "\\workspaces";
        try {

          System.out.println("command " + command);
          process = rt.exec(command, null, new File(targetPath));

          /*
           * new File("D:\\temp29Jun").mkdir(); process = rt.exec(
           * "cmd /c start mvn -DarchetypeVersion=2.0.0 -DarchetypeGroupId=io.oasp.java.templates " +
           * "-DarchetypeArtifactId=oasp4j-template-server archetype:generate -DgroupId=io.oasp.application " +
           * "-DartifactId=sampleapp -Dversion=0.1-SNAPSHOT -Dpackage=io.oasp.application.sampleapp -X", null, new
           * File("D:\\temp29Jun"));
           */
          process.waitFor();
          getOutput().showMessage("Project Creation complete");
        } catch (

        Exception e) {
          e.printStackTrace();
          getOutput().showError("Errr creating workspace: " + e.getMessage());
        }
        // create workspace here
      } else {
        getOutput().showError("Project exists!");
      }
    } else {
      getOutput().showError("Not a Devon Distribution Workspace");
    }

  }

  @Command(name = "run", help = "runs application from embedded tomcat", context = ContextType.PROJECT)
  @Parameters(values = { @Parameter(name = "port", description = "Port to start Spring boot app", optional = false),
  @Parameter(name = "path", description = "Path to Server project Workspace (currentDir if not given)", optional = true) })
  public void run(String port, String path) {

    // Optional<ProjectInfo> projectInfo = getContextPathInfo().getProjectRoot(path);

    Process p;
    try {
      String commandStr = "mvn spring-boot:run -Drun.arguments=\"server.port=" + port.trim() + "\" ";
      String cmd = "cmd /c start " + commandStr;//

      p = Runtime.getRuntime().exec(cmd, null, new File(path));
      p.waitFor();
      getOutput().showMessage("Starting application");
    } catch (Exception e) {

      getOutput().showError("An error occured during executing oasp4j Cmd");
    }
  }

  @Command(name = "build", help = "This command will build the server project", context = ContextType.PROJECT)
  @Parameters(values = {
  @Parameter(name = "path", description = "Path to Server project Workspace (currentDir if not given)", optional = true) })
  public void build(String path) {

    Optional<ProjectInfo> projectInfo = getContextPathInfo().getProjectRoot(path);
    System.out.println("projectInfo read...");
    System.out.println("path " + projectInfo.get().getPath() + "project type " + projectInfo.get().getProjecType());

    Process p;
    try {
      String cmd = "cmd /c start mvn clean install"; // Q: where to redirect output? --log-file D:\\log1.txt

      p = Runtime.getRuntime().exec(cmd, null, projectInfo.get().getPath().toFile());
      p.waitFor();
      getOutput().showMessage("Completed");
    } catch (Exception e) {

      getOutput().showError("An error occured during executing oasp4j Cmd");
    }
  }

  @Command(name = "deploy", help = "This command will deploy the server project on tomcat", context = ContextType.PROJECT)
  @Parameters(values = { @Parameter(name = "deploypath", description = "Path to tomcat folder"),
  @Parameter(name = "path", description = "Path to Server project Workspace (currentDir if not given)", optional = true) })
  public void deploy(String deploypath, String path) {

    Optional<DistributionInfo> distInfo = getContextPathInfo().getDistributionRoot(path);
    Path distRootPath = distInfo.get().getPath(); // Root path of distribution
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