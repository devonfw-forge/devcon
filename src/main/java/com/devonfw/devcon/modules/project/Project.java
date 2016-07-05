package com.devonfw.devcon.modules.project;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.modules.oasp4j.Oasp4j;
import com.devonfw.devcon.modules.oasp4js.Oasp4js;

/**
 * Module to automate tasks related to the devon projects (server + client)
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "project", description = "Module to automate tasks related to the devon projects (server + client)", deprecated = false)
public class Project extends AbstractCommandModule {

  @Command(name = "create", help = "This command is used to create new combined server & client project")
  @Parameters(values = {
  @Parameter(name = "serverpath", description = "path where the project server will be created (currentDir if not given)", optional = true),
  @Parameter(name = "servername", description = "name for the server project"),
  @Parameter(name = "packagename", description = "package name for the server project"),
  @Parameter(name = "groupid", description = "groupid for server project"),
  @Parameter(name = "version", description = "version of server project"),
  @Parameter(name = "clienttype", description = "type of the client project: 'devon4sencha' or 'oasp4js'"),
  @Parameter(name = "clientname", description = "name for the client project"),
  @Parameter(name = "clientpath", description = "path where the client project will be created. In case of sencha project this must point to a Sencha workspace.", optional = true) })
  public void create(String serverpath, String servername, String packagename, String groupid, String version,
      String clienttype, String clientname, String clientpath) {

    try {
      getOutput().showMessage("Creating server project...");
      Oasp4j serverManager = new Oasp4j();
      serverManager.create(serverpath, servername, packagename, groupid, version);
      getOutput().showMessage("Server project " + servername + " created successfully.");

      getOutput().showMessage("Creating client project...");
      if (clienttype.equals("devon4sencha")) {

      } else if (clienttype.equals("oasp4js")) {
        Oasp4js clientManager = new Oasp4js();
        clientManager.create(clientname, clientpath);

      } else {
        getOutput()
            .showError(
                "The parameter value for 'clienttype' is not valid. The options for this parameter are: 'devon4sencha' and 'oasp4js'.");
      }

      getOutput().showMessage("Client project " + clientname + " created successfully.");

    } catch (Exception e) {
      getOutput().showError("An error occurred during execution of project create command. " + e.getMessage());
    }

  }
}
