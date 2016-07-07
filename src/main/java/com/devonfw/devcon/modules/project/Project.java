package com.devonfw.devcon.modules.project;

import java.io.File;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.modules.oasp4j.Oasp4j;
import com.devonfw.devcon.modules.oasp4js.Oasp4js;
import com.devonfw.devcon.modules.sencha.Sencha;

/**
 * Module to automate tasks related to the devon projects (server + client)
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "project", description = "Module to automate tasks related to the devon projects (server + client)", deprecated = false)
public class Project extends AbstractCommandModule {

  private final String DEVON4SENCHA = "devon4sencha";

  private final String OASP4JS = "oasp4js";

  @Command(name = "create", help = "This command is used to create new combined server & client project")
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
  @Parameter(name = "gitdir", description = "Only for client type 'devon4sencha': GIT BIN/CMD directory where git executable is present", optional = true) })
  public void create(String distributionpath, String servername, String packagename, String groupid, String version,
      String clienttype, String clientname, String clientpath, String gituser, String gitpassword, String gitdir) {

    try {

      getOutput().showMessage("Creating server project...");
      Oasp4j serverManager = new Oasp4j();
      serverManager.setContextPathInfo(getContextPathInfo());
      serverManager.setOutput(getOutput());
      serverManager.create(distributionpath, servername, packagename, groupid, version);

      getOutput().showMessage("Creating client project...");
      if (clienttype.equals(this.DEVON4SENCHA)) {
        Sencha senchaManager = new Sencha();
        senchaManager.setContextPathInfo(getContextPathInfo());
        senchaManager.setOutput(getOutput());
        senchaManager.workspace(this.DEVON4SENCHA, clientpath, gituser, gitpassword, gitdir);
        senchaManager.create(clientname, clientpath + File.separator + this.DEVON4SENCHA);
      } else if (clienttype.equals(this.OASP4JS)) {
        Oasp4js clientManager = new Oasp4js();
        clientManager.setContextPathInfo(getContextPathInfo());
        clientManager.setOutput(getOutput());
        clientManager.create(clientname, clientpath);

      } else {
        getOutput()
            .showError(
                "The parameter value for 'clienttype' is not valid. The options for this parameter are: 'devon4sencha' and 'oasp4js'.");
      }

    } catch (Exception e) {
      getOutput().showError("An error occurred during execution of project create command. " + e.getMessage());
    }

  }
}
