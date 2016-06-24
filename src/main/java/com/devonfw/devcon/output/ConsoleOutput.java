package com.devonfw.devcon.output;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.DevconOption;
import com.devonfw.devcon.common.api.data.Info;

/**
 * TODO pparrado This type ...
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
  public void showMessage(String message) {

    this.out_.println(message);
  }

  @Override
  public void showCommandHelp(Command command) {

    Options options = new Options();
    for (CommandParameter commandParam : command.getDefinedParameters()) {
      options.addOption(commandParam.getName(), false, commandParam.getDescription());
    }

    HelpFormatter formatter = new HelpFormatter();

    formatter.printHelp(new PrintWriter(this.out_, true), 120, command.getName(), command.getDescription(), options, 0,
        0, null, true);
  }

  @Override
  public void showModuleHelp(CommandModuleInfo module) {

    StringBuilder footer = new StringBuilder();
    footer.append("Available commands for module: " + module.getName() + "\n");
    for (Info command : module.getCommands()) {
      footer.append("> " + command.getName() + ": " + command.getDescription() + "\n");
    }

    Options options = new Options();
    String usage = module.getName() + " <<command>> [parameters...]";
    HelpFormatter formatter = new HelpFormatter();

    formatter.printHelp(new PrintWriter(this.out_, true), 120, usage, module.getDescription(), options, 0, 0,
        footer.toString(), true);
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
    for (CommandModuleInfo moduleInfo : modules) {
      if (moduleInfo.isVisible())
        footer.append("> " + moduleInfo.getName() + ": " + moduleInfo.getDescription() + "\n");
    }

    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(new PrintWriter(this.out_, true), 120, usage, header, options_, 0, 0, footer.toString(), true);
  }

  @Override
  public void showError(String message) {

    this.out_.println("[ERROR] " + message);
  }

  @Override
  public void status(String message) {

    this.out_.println("\r[INFO] " + message);
  }

  @Override
  public void statusInNewLine(String message) {

    this.out_.println("\n[INFO] " + message);
  }

  @Override
  public void success(String command) {

    this.out_.println("[INFO] The command " + command.toUpperCase() + " has finished successfully");
  }

}
