package com.devonfw.devcon.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.entity.Sentence;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class InputManager {

  private String[] args = null;

  private Options options = new Options();

  public InputManager(String[] args) {

    String[] argsMock = { "foo", "farewell1234"/* , "-name", "Pablo" */};
    this.args = argsMock;

    // this.args = args;
    // this.options.addOption("h", "help", false, "show help");
    // this.options.addOption("v", "version", false, "show the devcon version.");
    this.options = getAvailableOptions();

    // Global parameters
    this.options.addOption("h", "help", false, "show help");
    this.options.addOption("np", false, "no prompt");
    this.options.addOption("v", "version", false, "show the devcon version");

  }

  public void parse() {

    System.out.println("----ARGS(" + this.args.length + ")----------------");
    for (int i = 0; i < this.args.length; i++) {
      System.out.println("args[" + i + "]: " + this.args[i]);
    }
    System.out.println("--------------------------");

    Sentence sentence = new Sentence();
    sentence.params = new ArrayList<String>();

    for (int i = 0; i < this.args.length; i++) {
      if (i == 0) {
        sentence.cmdModuleName = this.args[i];
      } else if (i == 1) {
        sentence.cmd = this.args[i];
      } else {
        sentence.params.add(this.args[i]);
      }
    }
    try {
      new CmdManager(sentence).evaluate();
    } catch (Exception e) {
      System.out.println("[ERROR] " + e.getMessage());
    }

    // CommandLineParser parser = new BasicParser();
    // CommandLine cmd = null;
    // try {
    // cmd = parser.parse(this.options, this.args);
    //
    // if (cmd.hasOption("h"))
    // help();
    //
    // if (cmd.hasOption("v")) {
    // // TODO the version must be dynamic
    // System.out.println("devcon v.0.1");
    // } else {
    // new CmdManager(sentence).evaluate();
    // }
    //
    // } catch (Exception e) {
    // System.out.println("[ERROR] An error occurred while parsing the command. " + e.getMessage());
    // }
  }

  // public void parse() {
  //
  // System.out.println("----ARGS(" + this.args.length + ")----------------");
  // for (int i = 0; i < this.args.length; i++) {
  // System.out.println("args[" + i + "]: " + this.args[i]);
  // }
  // System.out.println("--------------------------");
  //
  // Sentence sentence = new Sentence();
  //
  // // if (!this.args[0].isEmpty()) {
  // // sentence.cmdModuleName = this.args[0];
  // //
  // // if (!this.args[1].isEmpty()) {
  // // sentence.cmd = this.args[1];
  // // }
  // // } else {
  // // help();
  // // }
  //
  // CommandLineParser parser = new BasicParser();
  // CommandLine cmd = null;
  // try {
  // cmd = parser.parse(this.options, this.args);
  //
  // List<?> argsNotParsed = cmd.getArgList();
  // for (Object argNotParsed : argsNotParsed) {
  // System.out.println("argNotParsed: " + argNotParsed);
  // }
  //
  // if (argsNotParsed.size() > 1) {
  // sentence.cmdModuleName = argsNotParsed.get(0).toString();
  // sentence.cmd = argsNotParsed.get(1).toString();
  //
  // // new CmdManager(sentence).evaluate();
  //
  // } else if (argsNotParsed.size() == 1) {
  //
  // }
  //
  // if (cmd.hasOption("h"))
  // help();
  //
  // if (cmd.hasOption("v")) {
  // // System.out.println("You passed option v --> " + cmd.getOptionValue("v"));
  //
  // // TODO the version must be dynamic
  // System.out.println("devcon v.0.1");
  // }
  //
  // if (cmd.hasOption("name")) {
  // System.out.println("You passed name --> " + cmd.getOptionValue("name"));
  // }
  //
  // } catch (ParseException e) {
  // System.out.println("ERROR: " + e.getMessage());
  // }
  // }

  private void help() {

    HelpFormatter formater = new HelpFormatter();
    formater.printHelp("devon [params(opt)] [command-module] <<command>> [command-params(opt)]", this.options);
    System.exit(0);
  }

  private Options getAvailableOptions() {

    try {
      List<CmdModuleRegistry> availableModules = CmdManager.getAvailableModules();

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

}
