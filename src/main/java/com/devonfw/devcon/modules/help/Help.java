package com.devonfw.devcon.modules.help;

import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Utils;
import com.google.common.base.Optional;

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

    // StringBuilder body = new StringBuilder();

    this.output.showGeneralHelp(
        "Devcon is a command line tool that provides many automated tasks around the full life-cycle of Devon applications.",
        "devon <<module>> <<command>> [parameters...]", Utils.getGlobalOptions(), this.registry.getCommandModules());

  }

  @SuppressWarnings("javadoc")
  @Command(name = "module", help = "Shows commands available in a module")
  @Parameters(values = { @Parameter(name = "name", description = "the module name to get help for") })
  public void module(String name) {

    Optional<CommandModuleInfo> module = this.registry.getCommandModule(name);
    if (module.isPresent()) {

      this.output.showModuleHelp(module.get());

    } else {
      this.output.showError("Unkown module");
    }
  }

  @SuppressWarnings("javadoc")
  @Command(name = "command", help = "Shows commands available in a module")
  @Parameters(values = { @Parameter(name = "module", description = "the module to look up the coomand"),
  @Parameter(name = "command", description = "the comand name to get help for") })
  public void module(String moduleName, String commandName) {

    Optional<CommandModuleInfo> module = this.registry.getCommandModule(moduleName);
    if (module.isPresent()) {
      Optional<com.devonfw.devcon.common.api.Command> command = module.get().getCommand(commandName);
      if (command.isPresent()) {
        this.output.showCommandHelp(command.get());
      } else {

        this.output.showError("Unkown command");
      }
    } else {

      this.output.showError("Unkown module");
    }
  }

}
