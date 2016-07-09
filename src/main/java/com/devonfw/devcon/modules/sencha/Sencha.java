package com.devonfw.devcon.modules.sencha;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.SystemUtils;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.common.utils.Utils;
import com.google.common.base.Optional;

/**
 *
 * @author ivanderk
 */
@CmdModuleRegistry(name = "sencha", description = "Sencha related commands")
public class Sencha extends AbstractCommandModule {

  @SuppressWarnings("javadoc")
  @Command(name = "run", help = "compiles in DEBUG mode and then runs the internal Sencha web server (\"app watch\")", context = ContextType.PROJECT)
  @Parameters(values = { @Parameter(name = "port", description = "", optional = true),
  @Parameter(name = "appDir", description = "app Directory required to run Sencha Commands", optional = false) })
  public void run(String port, String appDir) throws Exception {

    // TODO ivanderk Implementatin for MacOSX & Unix
    if (!SystemUtils.IS_OS_WINDOWS) {
      getOutput().showMessage("This task is currently only supported on Windows");
      return;
    }

    Optional<ProjectInfo> project = getProjectInfo();
    if (project.isPresent() && project.get().getProjecType().equals(ProjectType.DEVON4SENCHA)) {
      try {

        if (appDir == null || appDir.isEmpty()) {
          appDir = getContextPathInfo().getCurrentWorkingDirectory().toString();
        }
        ProcessBuilder processBuilder = new ProcessBuilder("sencha", "app", "watch");
        processBuilder.directory(new File(appDir));

        processBuilder.start();
        this.output.status("[LOG]" + " Sencha App Watch Started");

      } catch (Exception e) {
        getOutput().showError("An error occured during executing Sencha Cmd");
        throw e;
      }

    } else {
      getOutput().showMessage("Not a Sencha project (or does not have a corresponding devon.json file)");
    }
  }

  @SuppressWarnings("javadoc")
  @Command(name = "workspace", help = "Creates a new Sencha Ext JS6 project in a workspace")
  @Parameters(values = { @Parameter(name = "projectname", description = "Name of project"),
  @Parameter(name = "workspacepath", description = "Path to Sencha Workspace (currentDir if not given)", optional = true),
  @Parameter(name = "username", description = "a user with permissions to download the Devon distribution"),
  @Parameter(name = "password", description = "the password related to the user with permissions to download the Devon distribution"),
  @Parameter(name = "gitDir", description = "GIT BIN/CMD directory where git executable is present", optional = true) })
  public void workspace(String projectname, String workspace, String username, String password, String gitDir)
      throws Exception {

    try {
      final String REMOTE_URL = new StringBuffer(Constants.HTTPS).append(username).append(Constants.COLON)
          .append(password).append(Constants.AT_THE_RATE).append(Constants.SENCHA_REPO_URL).toString();

      Path wsPath = null;
      Path projectPath = null;
      final Path currentDir = getContextPathInfo().getCurrentWorkingDirectory();
      if (workspace == null || workspace.isEmpty()) {
        projectPath = currentDir.resolve(projectname);
      } else {
        wsPath = currentDir.resolve(workspace);
        projectPath = wsPath.resolve(projectname);
      }
      if (!projectPath.toFile().exists()) {

        projectPath.toFile().mkdirs();

        // create workspace here
        Utils.cloneRepository(REMOTE_URL, projectPath.toString(), gitDir);
        getOutput().showMessage("Having repository: " + projectPath.toString() + Constants.DOT_GIT);
      } else {
        getOutput().showError("Project exists!");
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * @param appDir Location of Sencha Ext JS6 Application
   * @throws Exception Exception thrown by the Sencha build command
   */
  @Command(name = "build", help = "Builds a Sencha Ext JS6 project in a workspace", context = ContextType.PROJECT)
  @Parameters(values = {
  @Parameter(name = "appDir", description = "Path to Sencha Ext JS6 Application (currentDir if not given)", optional = true), })
  public void build(String appDir) throws Exception {

    try {

      ProcessBuilder processBuilder = new ProcessBuilder("sencha", "app", "build");
      processBuilder.directory(new File(appDir));

      Process process = processBuilder.start();

      final InputStream isError = process.getErrorStream();
      final InputStream isOutput = process.getInputStream();

      Utils.processErrorAndOutPut(isError, isOutput);

      // Wait to get exit value
      int pStatus = 0;
      try {
        pStatus = process.waitFor();
      } catch (InterruptedException e) {
        e.printStackTrace();
        throw e;
      }

      if (pStatus == 0) {
        run(Constants.SENCHA_CMD_WS_PORT, appDir);
        this.output.status("[LOG]" + "Sencha Build Successful");
      } else {
        this.output.status("[LOG]" + "Sencha Build Failed");
      }
    } catch (Exception e) {
      e.printStackTrace();
      this.output.status("[LOG]" + e.getMessage());
      throw e;
    }
  }

  /**
   * @param appname Name of Sencha Ext JS6 app
   * @param workspacepath Path to Sencha Workspace (currentDir if not given)
   * @throws Exception Exception thrown by Sencha generate app Command
   */
  @Command(name = "create", help = "Creates a new Sencha Ext JS6 app", context = ContextType.PROJECT)
  @Parameters(values = { @Parameter(name = "appname", description = "Name of Sencha Ext JS6 app"),
  @Parameter(name = "workspacepath", description = "Path to Sencha Workspace (currentDir if not given)", optional = true), })
  public void create(String appname, String workspacepath) throws Exception {

    try {

      Path currentDir = getContextPathInfo().getCurrentWorkingDirectory();

      if (workspacepath == null || workspacepath.isEmpty()) {
        workspacepath = currentDir.toString();
      }

      Path senchaWSPath = Paths.get(workspacepath);
      String starterTemplatePath = senchaWSPath.resolve(Constants.SENCHA_APP_STARTER_TEMPLATE).toString();
      Path senchaAppPath = senchaWSPath.resolve(appname);

      if (!Files.exists(senchaAppPath)) {
        Files.createDirectories(senchaAppPath);
      }

      ProcessBuilder processBuilder = new ProcessBuilder("sencha", "generate", "app", "-ext", "--starter",
          starterTemplatePath, appname, senchaAppPath.toString());

      processBuilder.directory(senchaWSPath.toFile());

      Process process = processBuilder.start();

      final InputStream isError = process.getErrorStream();
      final InputStream isOutput = process.getInputStream();

      Utils.processErrorAndOutPut(isError, isOutput);

      // Wait to get exit value
      int pStatus = 0;
      try {
        pStatus = process.waitFor();
      } catch (InterruptedException e) {
        e.printStackTrace();
        throw e;
      }

      if (pStatus == 0) {
        this.output.status("[LOG]" + "Sencha Ext JS6 app Created");
      } else {
        this.output.status("[LOG]"
            + "Sencha Ext JS6 app Creation Failed . Please make sure the workspace where app is created is a valid Sencha Workspace");
      }
    } catch (Exception e) {
      e.printStackTrace();
      this.output.status("[LOG]" + e.getMessage());
      throw e;
    }
  }
}