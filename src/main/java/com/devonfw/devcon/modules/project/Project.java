package com.devonfw.devcon.modules.project;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Constants;
import com.google.common.base.Optional;

/**
 * This class contains command for project module
 *
 * @author ssarmoka
 */
@CmdModuleRegistry(name = "project", description = "Project(server & client project) related commands")
public class Project extends AbstractCommandModule {

  /**
   * @param serverpath path to server project
   * @param clienttype client type values can be oasp4js(for angular project) and sencha(Sencha client)
   * @param clientpath path to client project
   */
  @Command(name = "build", help = "This command will build the server & client project(unified server and client build)", context = ContextType.COMBINEDPROJECT)
  @Parameters(values = {
  @Parameter(name = "serverpath", description = "Path to Server project Workspace (currentDir if not given)", optional = true),
  @Parameter(name = "clienttype", description = "This parameter shows which type of client is integrated with server i.e oasp4js or sencha", optional = false),
  @Parameter(name = "clientpath", description = "path to client directory", optional = false) })
  public void build(String serverpath, String clienttype, String clientpath) {

    this.projectInfo = getContextPathInfo().getProjectRoot(serverpath);

    try {
      Optional<com.devonfw.devcon.common.api.Command> cmd = getCommand(Constants.OASP4J, Constants.BUILD);
      cmd.get().exec(serverpath);
      switch (clienttype == null ? "" : clienttype) {
      case Constants.OASP4JS:

        Optional<com.devonfw.devcon.common.api.Command> oasp4js_cmd = getCommand(Constants.OASP4JS, Constants.BUILD);
        oasp4js_cmd.get().exec(clientpath);
        break;
      case Constants.SENCHA:
        Optional<com.devonfw.devcon.common.api.Command> sencha_cmd = getCommand(Constants.SENCHA, Constants.BUILD);
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

  /**
   * @param clienttype Defines type of client either oasp4js or Sencha
   * @param clientport Defines client port for Sencha project not configurable for oasp4js project
   * @param clientpath Path for client directory
   * @param serverport Port to run server project
   * @param serverpath Path of server directory
   */
  @Command(name = "run", help = "This command will run the server & client project(unified server and client build) in debug mode (seperate cliet and spring boot server(not on tomcat))", context = ContextType.COMBINEDPROJECT)
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
}
