package com.devonfw.devcon.modules.help;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Utils;

/**
 * Module to show general help info to the devcon user
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "help", description = "This module shows help info about devcon", deprecated = false)
public class Help extends AbstractCommandModule {

  @SuppressWarnings("javadoc")
  @Command(name = "guide", help = "This command is used to show a general vision about the basic usage of devcon.")
  public void guide() {

    StringBuilder body = new StringBuilder();

    this.output.showGeneralHelp("devon <<module>> <<command>> [parameters...]",
        "Devcon is a command line tool that provides many automated tasks around the full life-cycle of Devon applications.",
        Utils.getGlobalOptions(), this.registry.getCommandModules());

  }

}
