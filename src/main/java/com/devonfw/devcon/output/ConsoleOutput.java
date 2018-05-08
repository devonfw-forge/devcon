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
package com.devonfw.devcon.output;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.DevconOption;
import com.devonfw.devcon.common.api.data.Info;

/**
 * Implementation of {@link Output} based on the Console
 *
 * @author pparrado
 */
public class ConsoleOutput implements Output {

  private PrintStream out_;

  public ConsoleOutput() {
    this.out_ = System.out;
  }

  public ConsoleOutput(PrintStream out) {
    this();
    this.out_ = out;
  }

  @Override
  public void showMessage(String message, String... args) {

    this.out_.println(String.format(message, args));
  }

  @Override
  public void showCommandHelp(Command command) {

    Options options = new Options();
    for (CommandParameter commandParam : command.getDefinedParameters()) {
      options.addOption(commandParam.getName(), false, commandParam.getDescription());
    }

    HelpFormatter formatter = new HelpFormatter();

    String helpText = command.getHelpText();

    formatter.printHelp(new PrintWriter(this.out_, true), 120, command.getModuleName() + " " + command.getName(),
        command.getDescription(), options, 1, 2, null, true);

    // Only print out help text when actually present
    if (!helpText.isEmpty()) {
      this.out_.println();
      this.out_.println(helpText);
    }
  }

  @Override
  public void showModuleHelp(CommandModuleInfo module) {

    StringBuilder footer = new StringBuilder();
    footer.append("Available commands for module: " + module.getName() + "\n");

    // Obtain sorted command list
    for (Info command : sortCommands(module.getCommands())) {
      footer.append("> " + command.getName() + ": " + command.getDescription() + "\n");
    }

    Options options = new Options();
    String usage = module.getName() + " <<command>> [parameters...]";
    HelpFormatter formatter = new HelpFormatter();

    formatter.printHelp(new PrintWriter(this.out_, true), 120, usage, module.getDescription(), options, 1, 2,
        footer.toString(), true);
  }

  /**
   * @param commands
   * @return
   */
  private Collection<Command> sortCommands(Collection<Command> commands) {

    List<Command> lst = new ArrayList<>(commands);
    Collections.sort(lst);
    return lst;
  }

  @Override
  public void showGeneralHelp(String header, String usage, List<DevconOption> options,
      List<CommandModuleInfo> modules) {

    Options options_ = new Options();

    for (DevconOption opt : options) {
      options_.addOption(opt.getOpt(), opt.getLongOpt(), false, opt.getDescription());
    }

    StringBuilder footer = new StringBuilder();
    footer.append("List of available modules: \n");

    // get sorted list of modules
    for (CommandModuleInfo moduleInfo : sortModules(modules)) {
      if (moduleInfo.isVisible())
        footer.append("> " + moduleInfo.getName() + ": " + moduleInfo.getDescription() + "\n");
    }

    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(new PrintWriter(this.out_, true), 120, usage, header, options_, 1, 2, footer.toString(), true);
  }

  /**
   * @param modules
   * @return
   */
  private List<CommandModuleInfo> sortModules(List<CommandModuleInfo> modules) {

    Collections.sort(modules);
    return modules;
  }

  @Override
  public void showError(String message, String... args) {

    this.out_.println("[ERROR] " + String.format(message, args));
  }

  @Override
  public void status(String message, String... args) {

    this.out_.println("\r[INFO] " + String.format(message, args));
  }

  @Override
  public void statusInNewLine(String message, String... args) {

    this.out_.println("\n[INFO] " + String.format(message, args));
  }

  @Override
  public void success(String command) {

    this.out_.println("[INFO] The command " + command.toUpperCase() + " has finished successfully");
  }

}
