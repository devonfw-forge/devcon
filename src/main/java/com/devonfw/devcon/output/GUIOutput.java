package com.devonfw.devcon.output;

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

import javafx.scene.control.TextArea;

/**
 * This class implements Output interface.This is implementation for GUI.
 *
 * @author ssarmoka
 */
public class GUIOutput implements Output {

  private TextArea out_;

  private StringBuffer consoleOutput = new StringBuffer();

  public GUIOutput() {
    this.out_ = new TextArea();
  }

  public GUIOutput(TextArea out) {
    this();
    this.out_ = out;
  }

  @Override
  public void showMessage(String message, String... args) {

    this.consoleOutput.append(message).append("\n");
    this.out_.setText(this.consoleOutput.toString());
  }

  @Override
  public void showCommandHelp(Command command) {

    StringBuffer consoleOutput = new StringBuffer();
    Options options = new Options();
    for (CommandParameter commandParam : command.getDefinedParameters()) {
      options.addOption(commandParam.getName(), false, commandParam.getDescription());
    }

    HelpFormatter formatter = new HelpFormatter();

    String helpText = command.getHelpText();

    formatter.printHelp(120, command.getModuleName() + " " + command.getName(), command.getDescription(), options, null,
        true);

    consoleOutput.append(command.getModuleName()).append(" ").append(command.getName()).append("\n")
        .append(command.getDescription()).append("\n").append(options.toString());
    // Only print out help text when actually present
    if (!helpText.isEmpty()) {
      // this.out_.setText("");
      consoleOutput.append("\n").append(helpText);
      this.out_.setText(consoleOutput.toString());
    }
    this.out_.setText(consoleOutput.toString());
  }

  @Override
  public void showModuleHelp(CommandModuleInfo module) {

    StringBuilder footer = new StringBuilder();
    StringBuilder consoleOutput = new StringBuilder();
    footer.append("Available commands for module: " + module.getName() + "\n");

    // Obtain sorted command list
    for (Info command : sortCommands(module.getCommands())) {
      footer.append("> " + command.getName() + ": " + command.getDescription() + "\n");
    }

    Options options = new Options();
    String usage = "usage: " + module.getName() + " <<command>> [parameters...]";
    // HelpFormatter formatter = new HelpFormatter();
    //
    // formatter.printHelp(120, usage, module.getDescription(), options, footer.toString(), true);
    consoleOutput.append(usage).append("\n").append(module.getDescription()).append("\n").append(options).append("\n")
        .append(footer.toString());
    this.out_.setText(consoleOutput.toString());
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

    StringBuffer consoleOutput = new StringBuffer();

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
    consoleOutput.append("usage: " + usage).append("\n").append(header).append("\n").append(options_).append("\n")
        .append(footer.toString());
    this.out_.setText(consoleOutput.toString());
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

    this.consoleOutput.append("[ERROR] " + message);
    this.out_.setText(this.consoleOutput.toString());
  }

  @Override
  public void status(String message, String... args) {

    this.consoleOutput.append("\r[INFO] " + message);
    this.out_.setText(this.consoleOutput.toString());
  }

  @Override
  public void statusInNewLine(String message, String... args) {

    this.consoleOutput.append("\n[INFO] " + message);
    this.out_.setText(this.consoleOutput.toString());
  }

  @Override
  public void success(String command) {

    this.consoleOutput.append("[INFO] The command " + command.toUpperCase() + " has finished successfully");
    this.out_.setText(this.consoleOutput.toString());
  }

}
