package com.devonfw.devcon.modules.help;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.impl.AbstractCommandHolder;

/**
 * Module to show general help info to the devcon user
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "help", description = "This module shows help info about devcon", context = "global", deprecated = false)
public class Help extends AbstractCommandHolder {

  @SuppressWarnings("javadoc")
  @Command(name = "guide", help = "This command is used to show a general vision about the basic usage of devcon.")
  public void guide() {

    this.response.setUsage("devon <<module>> <<command>> [parameters...]");
    this.response
        .setHeader("Devcon is a command line tool that provides many automated tasks around the full life-cycle of Devon applications.");
    this.response.setGlobalParameters(this.dUtils.getGlobalOptions());
    this.response.setModulesList(this.dUtils.getListOfAvailableModules());
    this.output.showGeneralHelp(this.response);
  }

}
