package com.devonfw.devcon.input;

import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.tuple.Pair;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.CommandManager;
import com.devonfw.devcon.common.CommandResult;
import com.devonfw.devcon.common.api.data.DevconOption;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.utils.DevconUtils;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class ConsoleInputManager {

  private CommandManager commandManager;

  private DevconUtils dUtils = new DevconUtils();

  public ConsoleInputManager(CommandManager commandManager) {

    this.commandManager = commandManager;
  }

  public boolean parse(String[] args) {

    Sentence sentence = new Sentence();

    try {

      CommandLineParser parser = new BasicParser();
      CommandLine cmd = parser.parse(getOptions(), args);

      if (cmd.hasOption("v")) {
        System.out.println(Devcon.DEVCON_VERSION);
        System.exit(0);
      }

      sentence.setNoPrompt(cmd.hasOption("np"));
      sentence.setHelpRequested(cmd.hasOption("h"));

      for (Option parsedParam : cmd.getOptions()) {
        if (cmd.getOptionValue(parsedParam.getOpt()) != null)
          // sentence.getParams().add(Pair.of(parsedParam.getOpt(), cmd.getOptionValue(parsedParam.getOpt())));
          sentence.addParam(parsedParam.getOpt(), parsedParam.getOpt());
      }

      List<?> argList = cmd.getArgList();

      if (argList.size() == 0) {
        // If not command line parameters given, show main help ("usage")
        this.commandManager.showMainHelp();

        return false;

      } else if (argList.size() == 1) {
        sentence.setModuleName(argList.get(0).toString());
      } else if (argList.size() > 1) {
        sentence.setModuleName(argList.get(0).toString());
        sentence.setCommandName(argList.get(1).toString());
      }

      Pair<CommandResult, String> result = this.commandManager.execCmdLine(sentence);

      return (result.getLeft() == CommandResult.OK);

    } catch (Exception e) {
      if (e.getMessage() != null) {
        System.out.println("[ERROR] An error occurred. Message: " + e.getMessage());
      } else {
        System.out.println("[ERROR] An error occurred.");
      }
      return false;
    }
  }

  // private Options getAvailableCommandParameters() {
  //
  // try {
  // List<String> CommandParamsList = null; // this.dUtils.getAvailableCommandParameters();
  //
  // Options availableCommandParameters = new Options();
  //
  // for (String commandParam : CommandParamsList) {
  // availableCommandParameters.addOption(commandParam, true, null);
  // }
  // return availableCommandParameters;
  // } catch (Exception e) {
  // System.out.println("ERROR: " + e.getMessage());
  // return new Options();
  // }
  // }

  private Options getOptions() {

    List<DevconOption> devconOptions = this.dUtils.getGlobalOptions();
    Options options = new Options();

    for (DevconOption gOpt : devconOptions) {
      options.addOption(new Option(gOpt.getOpt(), gOpt.getLongOpt(), false, gOpt.getDescription()));
    }

    for (String name : this.commandManager.getParameterNames()) {
      options.addOption(name, true, null);
    }

    return options;
  }
}
