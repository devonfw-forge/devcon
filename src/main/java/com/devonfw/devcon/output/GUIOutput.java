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

import java.io.PrintWriter;
import java.io.StringWriter;
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

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * This class implements Output interface.This is implementation for GUI.
 *
 * @author ssarmoka
 */
public class GUIOutput implements Output {

  private TextArea out_;

  private StringBuffer consoleOutput = new StringBuffer();

  /**
   * The constructor.
   */
  public GUIOutput() {

    this.out_ = new TextArea();
  }

  /**
   * The constructor.
   *
   * @param out - Textarea in which output should be set
   */
  public GUIOutput(TextArea out) {

    this();
    this.out_ = out;
  }

  @SuppressWarnings("javadoc")
  @Override
  public void showMessage(String message, String... args) {

    this.consoleOutput.append(String.format(message, args)).append("\n");
    Platform.runLater(new Runnable() {
      @Override
      public void run() {

        GUIOutput.this.out_.setText(GUIOutput.this.consoleOutput.toString());
        GUIOutput.this.out_.selectPositionCaret(GUIOutput.this.out_.getLength());
        GUIOutput.this.out_.deselect();
      }
    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void showCommandHelp(Command command) {

    StringWriter buffer = new StringWriter();

    Options options = new Options();
    for (CommandParameter commandParam : command.getDefinedParameters()) {
      options.addOption(commandParam.getName(), false, commandParam.getDescription());
    }

    HelpFormatter formatter = new HelpFormatter();

    String helpText = command.getHelpText();

    formatter.printHelp(new PrintWriter(buffer), 120, command.getModuleName() + " " + command.getName(),
        command.getDescription(), options, 1, 2, null, true);

    if (!helpText.isEmpty()) {
      buffer.append("\n");
      buffer.append(helpText);
    }
    this.out_.setText(buffer.toString());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void showModuleHelp(CommandModuleInfo module) {

    StringWriter buffer = new StringWriter();
    StringBuilder footer = new StringBuilder();

    footer.append("Available commands for module: " + module.getName() + "\n");

    // Obtain sorted command list
    for (Info command : sortCommands(module.getCommands())) {
      footer.append("> " + command.getName() + ": " + command.getDescription() + "\n");
    }

    Options options = new Options();
    String usage = "usage: " + module.getName() + " <<command>> [parameters...]";

    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(new PrintWriter(buffer), 120, usage, module.getDescription(), options, 1, 2, footer.toString(),
        true);

    this.out_.setText(buffer.toString());
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

  /**
   * {@inheritDoc}
   */
  @Override
  public void showGeneralHelp(String header, String usage, List<DevconOption> options,
      List<CommandModuleInfo> modules) {

    StringWriter buffer = new StringWriter();

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
    formatter.printHelp(new PrintWriter(buffer), 120, usage, header, options_, 1, 2, footer.toString(), true);
    this.out_.setText(buffer.toString());
  }

  /**
   * @param modules
   * @return
   */
  private List<CommandModuleInfo> sortModules(List<CommandModuleInfo> modules) {

    Collections.sort(modules);
    return modules;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void showError(String message, String... args) {

    this.consoleOutput.append("[ERROR] ");
    this.consoleOutput.append(String.format(message, args)).append("\n");
    this.out_.setText(this.consoleOutput.toString());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void status(String message, String... args) {

    this.consoleOutput.append("\n[INFO] ");
    this.consoleOutput.append(String.format(message, args));
    this.out_.setText(this.consoleOutput.toString());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void statusInNewLine(String message, String... args) {

    this.consoleOutput.append("\n[INFO] " + String.format(message, args));
    this.out_.setText(this.consoleOutput.toString());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void success(String command) {

    this.consoleOutput.append("\n[INFO] The command " + command.toUpperCase() + " has finished successfully");
    this.out_.setText(this.consoleOutput.toString());
  }

}
