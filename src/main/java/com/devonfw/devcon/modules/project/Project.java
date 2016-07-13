package com.devonfw.devcon.modules.project;

import java.io.File;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Constants;
import com.google.common.base.Optional;

/**
 * Module to automate tasks related to devonfw projects (server + client)
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "project", description = "Module to automate tasks related to the devon projects (server + client)")
public class Project extends AbstractCommandModule {

  private final String DEVON4SENCHA = "devon4sencha";

  private final String OASP4J = "oasp4j";

  private final String OASP4JS = "oasp4js";

  private final String SENCHA = "sencha";

  private final String CREATE = "create";

  private final String DEPLOY = "deploy";

  private final String WORKSPACE = "workspace";

  @Command(name = "build", description = "This command will build the server & client project(unified server and client build)", context = ContextType.COMBINEDPROJECT)
  @Parameters(values = {
  @Parameter(name = "serverpath", description = "Path to Server project Workspace (currentDir if not given)", optional = true),
  @Parameter(name = "clienttype", description = "This parameter shows which type of client is integrated with server i.e oasp4js or sencha", optional = false),
  @Parameter(name = "clientpath", description = "path to client directory", optional = false) })
  public void build(String serverpath, String clienttype, String clientpath) {

    Optional<ProjectInfo> projectInfo = getContextPathInfo().getProjectRoot(serverpath);

    try {
      Optional<com.devonfw.devcon.common.api.Command> oasp4j = getCommand("oasp4j", "build");
      oasp4j.get().exec(serverpath);
      switch (clienttype == null ? "" : clienttype) {
      case "oasp4js":
        Optional<com.devonfw.devcon.common.api.Command> oasp4js_cmd = getCommand("oasp4js", "build");
        oasp4js_cmd.get().exec(clientpath);
        break;
      case "sencha":
        Optional<com.devonfw.devcon.common.api.Command> sencha_cmd = getCommand("sencha", "build");
        sencha_cmd.get().exec(clientpath);

        break;
      case "":
        getOutput()
            .showError("Clienttype is not specified cannot build client. Please set client type to oasp4js or Sencha");
      }
    } catch (Exception e) {
      getOutput().showError("An error occured during executing Project Cmd");
    }

  }

  @Command(name = "create", description = "This command is used to create new combined server & client project")
  @Parameters(values = {
  @Parameter(name = "distributionpath", description = "path to the Devonfw distribution (currentDir if not given)", optional = true),
  @Parameter(name = "servername", description = "name for the server project"),
  @Parameter(name = "packagename", description = "package name for the server project"),
  @Parameter(name = "groupid", description = "groupid for server project"),
  @Parameter(name = "version", description = "version of server project"),
  @Parameter(name = "clienttype", description = "type of the client project: 'devon4sencha' or 'oasp4js'"),
  @Parameter(name = "clientname", description = "name for the client project"),
  @Parameter(name = "clientpath", description = "path where the client project will be created. In case of sencha project this must point to a Sencha workspace.", optional = true),
  @Parameter(name = "gituser", description = "Only for client type 'devon4sencha': a user with permissions to download the Devon distribution.", optional = true),
  @Parameter(name = "gitpassword", description = "Only for client type 'devon4sencha': the password related to the user with permissions to download the Devon distribution", optional = true),
  @Parameter(name = "gitfolder", description = "Only for client type 'devon4sencha': GIT BIN/CMD folder where git executable is present", optional = true) })
  public void create(String distributionpath, String servername, String packagename, String groupid, String version,
      String clienttype, String clientname, String clientpath, String gituser, String gitpassword, String gitfolder) {

    try {

      Optional<com.devonfw.devcon.common.api.Command> createServer = getCommand(this.OASP4J, this.CREATE);

      if (createServer.isPresent()) {
        createServer.get().exec(distributionpath, servername, packagename, groupid, version);
      } else {
        getOutput().showError("No command create found for oasp4j module.");
      }

      getOutput().showMessage("Creating client project...");
      if (clienttype.equals(this.DEVON4SENCHA)) {

        Optional<com.devonfw.devcon.common.api.Command> createSenchaWorkspace = getCommand(this.SENCHA, this.WORKSPACE);
        if (createSenchaWorkspace.isPresent()) {
          createSenchaWorkspace.get().exec(this.DEVON4SENCHA, clientpath, gituser, gitpassword, gitfolder);
        } else {
          getOutput().showError("No command workspace found for sencha module.");
        }

        Optional<com.devonfw.devcon.common.api.Command> createSenchaApp = getCommand(this.SENCHA, this.CREATE);
        if (createSenchaApp.isPresent()) {
          createSenchaApp.get().exec(clientname, clientpath + File.separator + this.DEVON4SENCHA);
        } else {
          getOutput().showError("No command create found for sencha module.");
        }

      } else if (clienttype.equals(this.OASP4JS)) {

        Optional<com.devonfw.devcon.common.api.Command> createOasp4js = getCommand(this.OASP4JS, this.CREATE);

        if (createOasp4js.isPresent()) {
          createOasp4js.get().exec(clientname, clientpath);
        } else {
          getOutput().showError("No command create found for oasp4js module.");
        }

      } else {
        getOutput().showError(
            "The parameter value for 'clienttype' is not valid. The options for this parameter are: 'devon4sencha' and 'oasp4js'.");
      }

    } catch (Exception e) {
      getOutput().showError("An error occurred during execution of project create command. " + e.getMessage());
    }

  }

  /**
   * @param clienttype Defines type of client either oasp4js or Sencha
   * @param clientport Defines client port for Sencha project not configurable for oasp4js project
   * @param clientpath Path for client directory
   * @param serverport Port to run server project
   * @param serverpath Path of server directory
   */
  @Command(name = "run", description = "This command will run the server & client project(unified server and client build) in debug mode (seperate cliet and spring boot server(not on tomcat))", context = ContextType.COMBINEDPROJECT)
  @Parameters(values = {

  @Parameter(name = "clienttype", description = "This parameter shows which type of client is integrated with server i.e oasp4js or sencha", optional = false),
  @Parameter(name = "clientport", description = "User can configured port if client type is Sencha", optional = true),
  @Parameter(name = "clientpath", description = "Port to start spring boot server", optional = true),
  @Parameter(name = "serverport", description = "Port to start client", optional = true),
  @Parameter(name = "serverpath", description = "Path to Server project Workspace (currentDir if not given)", optional = true) })
  public void run(String clienttype, String clientport, String clientpath, String serverport, String serverpath) {

    this.projectInfo = getContextPathInfo().getProjectRoot(serverpath);
    try {
      Optional<com.devonfw.devcon.common.api.Command> cmd = getCommand(Constants.OASP4J, Constants.RUN);
      cmd.get().exec(serverport, serverpath);

      switch (clienttype == null ? "" : clienttype) {
      case "oasp4js":
        Optional<com.devonfw.devcon.common.api.Command> oasp4js_cmd = getCommand(Constants.OASP4JS, Constants.RUN);
        oasp4js_cmd.get().exec(clientpath);
        break;
      case "sencha":
        Optional<com.devonfw.devcon.common.api.Command> sencha_cmd = getCommand(Constants.SENCHA, Constants.RUN);
        sencha_cmd.get().exec(clientport, clientpath);
        break;
      case "":
        getOutput()
            .showError("Clienttype is not specified cannot build client. Please set client type to oasp4js or Sencha");
      }
    } catch (Exception e) {
      getOutput().showError("An error occured during executing Project Cmd");
    }
  }

  @Command(name = "deploy", description = "This command is to automate the deploy process of a combined server & client project")
  @Parameters(values = { @Parameter(name = "tomcatpath", description = "Path to tomcat folder"),
  @Parameter(name = "distributionpath", description = "path to the Devonfw distribution (currentDir if not given)") })
  public void deploy(String tomcatpath, String distributionpath) {

    try {

      Optional<com.devonfw.devcon.common.api.Command> deploy = getCommand(this.OASP4J, this.DEPLOY);
      if (deploy.isPresent()) {
        deploy.get().exec(tomcatpath, distributionpath);
      } else {
        getOutput().showError("No command deploy found for oasp4j module.");
      }

    } catch (Exception e) {
      getOutput().showError("An error occurred during the execution of project deploy command. " + e.getMessage());

    }

  }
}
