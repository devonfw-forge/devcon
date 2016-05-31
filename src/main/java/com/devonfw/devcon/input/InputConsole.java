package com.devonfw.devcon.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.devonfw.devcon.common.CmdManager;
import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.data.DevconOption;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.exception.NotRecognizedCommandException;
import com.devonfw.devcon.common.exception.NotRecognizedModuleException;
import com.devonfw.devcon.common.utils.DevconUtils;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class InputConsole {

  private String[] args = null;

  private Options options = new Options();

  DevconUtils dUtils = new DevconUtils();

  public InputConsole(String[] args) {

    this.args = args;
    this.options = setOptions();

  }

  public boolean parse() {

    Sentence sentence = new Sentence();
    sentence.params = new ArrayList<HashMap<String, String>>();

    try {

      CommandLineParser parser = new BasicParser();
      CommandLine cmd = null;
      cmd = parser.parse(this.options, this.args);

      if (cmd.hasOption("v")) {
        // TODO the version must be dynamic
        System.out.println("devcon v.0.1");
        System.exit(0);
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
        if (cmd.getOptionValue(parsedParam.getOpt()) != null)
          sentence.params.add(this.dUtils.createParameterItem(parsedParam.getOpt(),
              cmd.getOptionValue(parsedParam.getOpt())));
      }

      List<?> argsNotParsed = cmd.getArgList();

      if (argsNotParsed.size() == 0) {
        throw new Exception(
            "You must specify a valid module name. Try 'devon help guide' command to know more about DevCon usage.");
      } else if (argsNotParsed.size() == 1) {
        sentence.moduleName = argsNotParsed.get(0).toString();
      } else if (argsNotParsed.size() > 1) {
        sentence.moduleName = argsNotParsed.get(0).toString();
        sentence.commandName = argsNotParsed.get(1).toString();
      }

      new CmdManager(sentence).evaluate();

      return true;
    } catch (NotRecognizedModuleException e) {
      System.out.println("[ERROR] The module " + e.moduleName + " is not recognized as available module.");
      return false;
    } catch (NotRecognizedCommandException e) {
      System.out.println("The command " + e.commandName + " is not recognized as valid command of the " + e.moduleName
          + " module");
      return false;
    } catch (Exception e) {
      if (e.getMessage() != null) {
        System.out.println("[ERROR] An error occurred. Message: " + e.getMessage());
      } else {
        System.out.println("[ERROR] An error occurred.");
      }

      return false;
    }

  }

  private void help() {

    HelpFormatter formater = new HelpFormatter();
    formater.printHelp("devon [params(opt)] [command-module] <<command>> [command-params(opt)]", this.options);
    System.exit(0);
  }

  private Options getAvailableOptions() {

    try {
      List<CmdModuleRegistry> availableModules = this.dUtils.getAvailableModules();

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

    try {
      List<String> CommandParamsList = this.dUtils.getAvailableCommandParameters();

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

  private Options setOptions() {

    Options opts = new Options();
    List<DevconOption> globalOptions = new ArrayList<>();
    DevconUtils devconUtils = new DevconUtils();
    opts = getAvailableCommandParameters();

    globalOptions = devconUtils.getGlobalOptions();

    if (globalOptions != null) {
      for (DevconOption gOpt : globalOptions) {
        opts.addOption(new Option(gOpt.opt, gOpt.longOpt, false, gOpt.description));
      }
    }

    return opts;

  }

}
