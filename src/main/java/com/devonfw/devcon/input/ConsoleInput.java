package com.devonfw.devcon.input;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.tuple.Pair;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.CommandManager;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.exception.NotRecognizedCommandException;
import com.devonfw.devcon.common.exception.NotRecognizedModuleException;
import com.devonfw.devcon.common.utils.DevconUtils;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class ConsoleInput {

  private CommandManager commandManager;

  // private String[] args = null;

  DevconUtils dUtils = new DevconUtils();

  public ConsoleInput(CommandManager commandManager) {

    // this.args = args;
    this.commandManager = commandManager;

  }

  public boolean parse(String[] args) {

    Sentence sentence = new Sentence();
    sentence.setParams(new ArrayList<Pair<String, String>>());

    try {

      CommandLineParser parser = new BasicParser();
      CommandLine cmd = null;
      cmd = parser.parse(new Options(), args);

      if (cmd.hasOption("v")) {
        System.out.println(Devcon.DEVCON_VERSION);
        System.exit(0);
      }

      sentence.setNoPrompt(cmd.hasOption("np"));
      sentence.setHelpRequested(cmd.hasOption("h"));

      for (Option parsedParam : cmd.getOptions()) {
        if (cmd.getOptionValue(parsedParam.getOpt()) != null)
          sentence.getParams().add(Pair.of(parsedParam.getOpt(), cmd.getOptionValue(parsedParam.getOpt())));
      }

      List<?> argsNotParsed = cmd.getArgList();

      if (argsNotParsed.size() == 0) {
        // If not command line parameters given, show main help ("usage")
        this.commandManager.showMainHelp();
        return false;

      } else if (argsNotParsed.size() == 1) {
        sentence.setModuleName(argsNotParsed.get(0).toString());
      } else if (argsNotParsed.size() > 1) {
        sentence.setModuleName(argsNotParsed.get(0).toString());
        sentence.setCommandName(argsNotParsed.get(1).toString());
      }

      this.commandManager.evaluate(sentence);

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
      List<String> CommandParamsList = null; // this.dUtils.getAvailableCommandParameters();

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

  // private Options setOptions() {
  //
  // Options opts = new Options();
  // List<DevconOption> globalOptions = new ArrayList<>();
  // DevconUtils devconUtils = new DevconUtils();
  // opts = getAvailableCommandParameters();
  //
  // globalOptions = devconUtils.getGlobalOptions();
  //
  // if (globalOptions != null) {
  // for (DevconOption gOpt : globalOptions) {
  // opts.addOption(new Option(gOpt.getOpt(), gOpt.getLongOpt(), false, gOpt.getDescription()));
  // }
  // }
  //
  // return opts;
  // }
}
