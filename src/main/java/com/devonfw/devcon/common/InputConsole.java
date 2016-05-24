package com.devonfw.devcon.common;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.entity.Sentence;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class InputConsole {

  private String[] args = null;

  private Options options = new Options();

  public InputConsole(String[] args) {

    // String[] argsMock = { "-np", "foo", "customFarewell", "-name", "Pablo" };
    // String[] argsMock = { "-np", "foo", "largeCustomFarewell", "-name", "Pablo", "-surname", "Parra" };
    String[] argsMock = { "foo", "largeCustomFarewell", "-name", "Pablo" };
    // String[] argsMock = { "foo", "customFarewell", "-help" };
    // String[] argsMock = { "foo", "-help" };
    this.args = argsMock;

    // this.args = args;

    this.options = getAvailableCommandParameters();

    // Global parameters
    this.options.addOption("h", "help", false, "show help");
    this.options.addOption("np", false, "no prompt");
    this.options.addOption("v", "version", false, "show the devcon version");

  }

  public void parse() {

    // System.out.println("----ARGS(" + this.args.length + ")----------------");
    // for (int i = 0; i < this.args.length; i++) {
    // System.out.println("args[" + i + "]: " + this.args[i]);
    // }
    // System.out.println("--------------------------");

    Sentence sentence = new Sentence();
    sentence.params = new HashMap<String, String>();

    try {

      CommandLineParser parser = new BasicParser();
      CommandLine cmd = null;
      cmd = parser.parse(this.options, this.args);

      if (cmd.hasOption("v")) {
        // TODO the version must be dynamic
        System.out.println("devcon v.0.1");
      }

      if (cmd.hasOption("np")) {
        sentence.noPrompt = true;
      } else {
        sentence.noPrompt = false;
      }

      if (cmd.hasOption("h")) {
        sentence.helpRequested = true;
      } else {
        sentence.helpRequested = false;
      }

      Option[] parsedParams = cmd.getOptions();
      for (Option parsedParam : parsedParams) {
        // System.out.println("parsedParam: " + parsedParam.getOpt() + " = " +
        // cmd.getOptionValue(parsedParam.getOpt()));
        if (cmd.getOptionValue(parsedParam.getOpt()) != null)
          sentence.params.put(parsedParam.getOpt(), cmd.getOptionValue(parsedParam.getOpt()));
      }

      List<?> argsNotParsed = cmd.getArgList();

      // for (Object argNotParsed : argsNotParsed) {
      // System.out.println("argNotParsed: " + argNotParsed);
      // }

      if (argsNotParsed.size() > 1) {
        sentence.cmdModuleName = argsNotParsed.get(0).toString();
        sentence.cmd = argsNotParsed.get(1).toString();
      } else if (argsNotParsed.size() == 1) {
        sentence.cmdModuleName = argsNotParsed.get(0).toString();
      }

      new CmdManager(sentence).evaluate();

    } catch (Exception e) {
      System.out.println("[ERROR] " + e.getMessage());
    }

  }

  private void help() {

    HelpFormatter formater = new HelpFormatter();
    formater.printHelp("devon [params(opt)] [command-module] <<command>> [command-params(opt)]", this.options);
    System.exit(0);
  }

  private Options getAvailableOptions() {

    CmdManager commandManager = new CmdManager();
    try {
      List<CmdModuleRegistry> availableModules = commandManager.getAvailableModules();

      Options availableOptions = new Options();
      for (CmdModuleRegistry module : availableModules) {
        availableOptions.addOption(module.name(), false, module.description());
      }

      return availableOptions;

    } catch (Exception e) {
      System.out.println("ERROR: " + e.getMessage());
      return new Options();
    }

  }

  private Options getAvailableCommandParameters() {

    CmdManager commandManager = new CmdManager();
    try {
      List<String> CommandParamsList = commandManager.getAvailableCommandParameters();

      Options availableCommandParameters = new Options();

      for (String commandParam : CommandParamsList) {
        availableCommandParameters.addOption(commandParam, true, null);
      }
      return availableCommandParameters;
    } catch (Exception e) {
      System.out.println("ERROR: " + e.getMessage());
      return new Options();
    }
  }

}
