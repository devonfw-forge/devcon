package com.devonfw.devcon.modules.sencha;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

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
 * Module to automate tasks related to devon4sencha projects (Ext JS)
 *
 * @author ivanderk
 */
@CmdModuleRegistry(name = "sencha", description = "Commands related with Ext JS6/Devon4Sencha projects")
public class Sencha extends AbstractCommandModule {

  @SuppressWarnings("javadoc")
  @Command(name = "run", description = "compiles in DEBUG mode and then runs the internal Sencha web server (\"app watch\")", context = ContextType.PROJECT)
  @Parameters(values = { @Parameter(name = "port", description = "", optional = true),
  @Parameter(name = "appfolder", description = "app folder required to run Sencha Commands", optional = true) })
  public void run(String port, String appFolder) throws Exception {

    // TODO ivanderk Implementatin for MacOSX & Unix
    if (!SystemUtils.IS_OS_WINDOWS) {
      getOutput().showMessage("This task is currently only supported on Windows");
      return;
    }

    Optional<ProjectInfo> project = getContextPathInfo().getProjectRoot(appFolder);
    if (project.isPresent() && project.get().getProjecType().equals(ProjectType.DEVON4SENCHA)) {
      try {

        if (appFolder == null || appFolder.isEmpty()) {
          appFolder = getContextPathInfo().getCurrentWorkingDirectory().toString();
        }

        ProcessBuilder processBuilder = new ProcessBuilder("sencha", "app", "watch");
        processBuilder.directory(new File(appFolder));

        Process process = processBuilder.start();

        final InputStream isError = process.getErrorStream();
        final InputStream isOutput = process.getInputStream();

        Utils.processErrorAndOutPut(isError, isOutput);

        getOutput().showMessage(" Sencha App Watch Started");

      } catch (Exception e) {
        getOutput().showError("An error occured during executing Sencha Cmd");
        throw e;
      }

    } else {
      getOutput().showMessage("Not a Sencha project (or does not have a corresponding devon.json file)");
    }
  }

  @SuppressWarnings("javadoc")
  @Command(name = "workspace", description = "Creates a new Sencha Ext JS6 project in a workspace")
  @Parameters(values = {
  @Parameter(name = "workspacename", description = "Name for the workspace"),
  @Parameter(name = "workspacepath", description = "Path to Sencha Workspace (currentDir if not given)", optional = true),
  @Parameter(name = "username", description = "a user with permissions to download the Devon distribution"),
  @Parameter(name = "password", description = "the password related to the user with permissions to download the Devon distribution"),
  @Parameter(name = "gitFolder", description = "GIT BIN/CMD folder where git executable is present", optional = true) })
  public void workspace(String projectname, String workspace, String username, String password, String gitFolder)
      throws Exception {

    try {
      String pass = Utils.encode(password);
      String user = Utils.encode(username);
      final String REMOTE_URL =
          new StringBuffer(Constants.HTTPS).append(user).append(Constants.COLON).append(pass)
              .append(Constants.AT_THE_RATE).append(Constants.SENCHA_REPO_URL).toString();

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

        String remoteUrl = Utils.decode(REMOTE_URL);
        getOutput().showMessage("Cloning from " + remoteUrl);

        // create workspace here
        Utils.cloneRepository(REMOTE_URL, projectPath.toString(), gitFolder);
        getOutput().showMessage("Having repository: " + projectPath.toString() + Constants.DOT_GIT);
      } else {
        getOutput().showError("Project exists!");
      }
    } catch (Exception e) {
      getOutput().showError("An error occured while executing workspace command", e.getMessage());
      throw e;
    }
  }

  /**
   * @param appDir Location of Sencha Ext JS6 Application
   * @throws Exception Exception thrown by the Sencha build command
   */
  @Command(name = "build", description = "Builds a Sencha Ext JS6 project in a workspace", context = ContextType.PROJECT)
  @Parameters(values = { @Parameter(name = "appDir", description = "Path to Sencha Ext JS6 Application (currentDir if not given)", optional = true), })
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
        getOutput().showError("An error occured while executing build command", e.getMessage());
        throw e;
      }

      if (pStatus == 0) {
        run(Constants.SENCHA_CMD_WS_PORT, appDir);
        getOutput().showMessage(" Sencha Build Successful");
      } else {
        getOutput().showError(" Sencha Build Failed");
      }
    } catch (Exception e) {
      getOutput().showError("An error occured while executing build command", e.getMessage());
      throw e;
    }
  }

  /**
   * @param appname Name of Sencha Ext JS6 app
   * @param workspacepath Path to Sencha Workspace (currentDir if not given)
   * @throws Exception Exception thrown by Sencha generate app Command
   */
  @Command(name = "create", description = "Creates a new Sencha Ext JS6 app", context = ContextType.NONE)
  @Parameters(values = {
  @Parameter(name = "appname", description = "Name of Sencha Ext JS6 app"),
  @Parameter(name = "workspacepath", description = "Path to Sencha Workspace (currentDir if not given)", optional = true), })
  public void create(String appname, String workspacepath) throws Exception {

    try {

      workspacepath =
          workspacepath.isEmpty() ? getContextPathInfo().getCurrentWorkingDirectory().toString() : workspacepath;

      File starterTemplate = new File(workspacepath + File.separator + Constants.SENCHA_APP_STARTER_TEMPLATE);

      if (!starterTemplate.exists()) {
        getOutput().showError(
            starterTemplate.toString()
                + " not found. Please verify that you are creating the app in a Sencha workspace.");
        return;
      }

      File senchaApp = new File(workspacepath + File.separator + appname);

      if (!senchaApp.exists()) {

        ProcessBuilder processBuilder =
            new ProcessBuilder("sencha", "generate", "app", "-ext", "--starter", Constants.SENCHA_APP_STARTER_TEMPLATE,
                appname, senchaApp.toString());

        processBuilder.directory(new File(workspacepath));

        Process process = processBuilder.start();

        final InputStream isError = process.getErrorStream();
        final InputStream isOutput = process.getInputStream();

        Utils.processErrorAndOutPut(isError, isOutput);

        // Wait to get exit value
        int pStatus = 0;
        try {
          pStatus = process.waitFor();
        } catch (InterruptedException e) {
          getOutput().showError("An error occured while executing create command", e.getMessage());
          throw e;
        }

        if (pStatus == 0) {
        getOutput().showMessage("Adding devon.json file...");
        Utils.addDevonJsonFile(senchaAppPath, ProjectType.DEVON4SENCHA);
        getOutput().showMessage("Sencha Ext JS6 app Created");
      } else {
        getOutput().showError("The app " + senchaApp.toString() + " already exists.");
      }

    } catch (Exception e) {
      getOutput().showError("An error occured while executing create command", e.getMessage());
      throw e;
    }
  }

}