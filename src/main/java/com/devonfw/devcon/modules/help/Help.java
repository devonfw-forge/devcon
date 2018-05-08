/*******************************************************************************
 * Copyright 2015-2018 Capgemini SE.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package com.devonfw.devcon.modules.help;

import java.lang.reflect.InvocationTargetException;

import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.google.common.base.Optional;

/**
 * Module to show general help info to the devcon user
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "help", description = "This module shows help info about devcon")
public class Help extends AbstractCommandModule {

  @SuppressWarnings("javadoc")
  @Command(name = "overview", description = "This command provides a quick overview the basic usage of devcon")
  public void overview() {

    this.output
        .showGeneralHelp(
            "Devcon is a command line tool that provides many automated tasks around the full life-cycle of Devon applications.",
            "devon <<module>> <<command>> [parameters...]", this.contextPathInfo.getGlobalOptions(),
            this.registry.getCommandModules());

  }

  @SuppressWarnings("javadoc")
  @Command(name = "userguide", description = "Shows the Devcon user guide")
  public void guide() {

    try {
      getCommand("doc", "userguide").get().exec();
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {

      this.output.showError("calling doc devcon: %s", e.getMessage());
    }
  }

  @SuppressWarnings("javadoc")
  @Command(name = "module", description = "Shows commands available in a module")
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
  @Command(name = "command", description = "Shows commands available in a module")
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
