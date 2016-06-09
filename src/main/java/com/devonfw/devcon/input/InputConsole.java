package com.devonfw.devcon.input;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.CmdManager;
import com.devonfw.devcon.common.api.data.DevconOption;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.api.utils.Pair;
import com.devonfw.devcon.common.exception.NotRecognizedCommandException;
import com.devonfw.devcon.common.exception.NotRecognizedModuleException;
import com.devonfw.devcon.common.utils.BasicPair;
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
    sentence.setParams(new ArrayList<Pair<String, String>>());

    try {

      CommandLineParser parser = new BasicParser();
      CommandLine cmd = null;
      cmd = parser.parse(this.options, this.args);

      if (cmd.hasOption("v")) {
        System.out.println(Devcon.DEVCON_VERSION);
        System.exit(0);
      }

      if (cmd.hasOption("np")) {
        sentence.setNoPrompt(true);
      } else {
        sentence.setNoPrompt(false);
      }

      if (cmd.hasOption("h")) {
        sentence.setHelpRequested(true);
      } else {
        sentence.setHelpRequested(false);
      }

      Option[] parsedParams = cmd.getOptions();
      for (Option parsedParam : parsedParams) {
        if (cmd.getOptionValue(parsedParam.getOpt()) != null)
          sentence.getParams()
              .add(new BasicPair<String, String>(parsedParam.getOpt(), cmd.getOptionValue(parsedParam.getOpt())));
      }

      List<?> argsNotParsed = cmd.getArgList();

      if (argsNotParsed.size() == 0) {
        throw new Exception(
            "You must specify a valid module name. Try 'devon help guide' command to know more about DevCon usage.");
      } else if (argsNotParsed.size() == 1) {
        sentence.setModuleName(argsNotParsed.get(0).toString());
      } else if (argsNotParsed.size() > 1) {
        sentence.setModuleName(argsNotParsed.get(0).toString());
        sentence.setCommandName(argsNotParsed.get(1).toString());
      }

      new CmdManager(sentence).evaluate();

      return true;
    } catch (NotRecognizedModuleException e) {
      System.out.println("[ERROR] The module " + e.moduleName + " is not recognized as available module.");
      return false;
    } catch (NotRecognizedCommandException e) {
      System.out.println("[ERROR] The command " + e.commandName + " is not recognized as valid command of the "
          + e.moduleName + " module");
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
        opts.addOption(new Option(gOpt.getOpt(), gOpt.getLongOpt(), false, gOpt.getDescription()));
      }
    }

    return opts;
  }
}
