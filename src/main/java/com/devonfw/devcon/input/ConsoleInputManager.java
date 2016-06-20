package com.devonfw.devcon.input;

import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.tuple.Pair;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.CommandResult;
import com.devonfw.devcon.common.api.CommandManager;
import com.devonfw.devcon.common.api.data.DevconOption;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.utils.Utils;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class ConsoleInputManager {

  private CommandManager commandManager;

  private Utils dUtils = new Utils();

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

      sentence.setHelpRequested(cmd.hasOption("h"));

      for (Option parsedParam : cmd.getOptions()) {
        if (cmd.getOptionValue(parsedParam.getOpt()) != null)
          sentence.addParam(parsedParam.getOpt(), parsedParam.getValue());
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

      Pair<CommandResult, Object> result = this.commandManager.execCmdLine(sentence);

      return ((result.getLeft() == CommandResult.OK) || (result.getLeft() == CommandResult.HELP_SHOWN));

    } catch (Exception e) {
      if (e.getMessage() != null) {
        System.out.println("[ERROR] An error occurred. Message: " + e.getMessage());
      } else {
        System.out.println("[ERROR] An error occurred.");
      }
      return false;
    }
  }

  private Options getOptions() {

    List<DevconOption> devconOptions = Utils.getGlobalOptions();
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
