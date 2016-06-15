package com.devonfw.devcon.output;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.data.DevconOption;
import com.devonfw.devcon.common.api.data.Info;
import com.devonfw.devcon.common.api.data.Response;

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
  public void showCommandHelp(Response response) {

    Options options = new Options();
    for (Parameter commandParam : response.getCommandParamsList()) {
      options.addOption(commandParam.name(), false, commandParam.description());
    }

    HelpFormatter formatter = new HelpFormatter();

    formatter.printHelp(new PrintWriter(this.out_), 80, response.getName(), response.getDescription(), options, 0, 0,
        null, true);
  }

  @Override
  public void showModuleHelp(Response response) {

    StringBuilder footerContent = new StringBuilder();
    footerContent.append("Available commands for module: " + response.getName() + "\n");
    for (Info command : response.getCommandsList()) {
      footerContent.append("> " + command.getName() + ": " + command.getDescription() + "\n");
    }

    Options options = new Options();
    String header = response.getName() + " <<command>> [parameters...]";
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(new PrintWriter(this.out_), 80, header, response.getDescription(), options, 0, 0, null, true);
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
    formatter.printHelp(new PrintWriter(this.out_), 80, usage, header, options_, 0, 0, null, true);
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
