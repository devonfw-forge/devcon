package com.devonfw.devcon.common;

import java.util.List;

import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.exception.NotRecognizedCommandException;
import com.devonfw.devcon.common.exception.NotRecognizedModuleException;
import com.devonfw.devcon.common.utils.DevconUtils;
import com.devonfw.devcon.output.OutputConsole;

/**
 * Implementation of the Command Manager
 *
 * @author pparrado
 */
public class CmdManager {

  public Sentence sentence;

  public CmdManager() {

  }

  public CmdManager(Sentence sentence) {

    this.sentence = sentence;
  }

  public void evaluate() throws Exception {

    DevconUtils dUtils = new DevconUtils();
    OutputConsole output = new OutputConsole();
    List<String> paramsValuesList = dUtils.getParamsValues(this.sentence.getParams());
    List<String> paramsNamesList = dUtils.getParamsKeys(this.sentence.getParams());
    List<String> commandNeededParams;

    Class<?> module = dUtils.getModule(this.sentence.getModuleName());

    if (module != null) {

      Command command = dUtils.getCommand(module, this.sentence.getCommandName());

      // If helpRequested flag is 'true' the app shows the help info and ends
      if (this.sentence.isHelpRequested()) {

        dUtils.showHelp(module, this.sentence);

      } else {

        if (command != null) {

          commandNeededParams = dUtils.getCommandParameters(module, this.sentence.getCommandName());
          if (commandNeededParams != null) {
            List<String> missingParameters = dUtils.getMissingParameters(paramsNamesList, commandNeededParams);

            if (missingParameters.size() > 0 && !this.sentence.isNoPrompt()) {
              this.sentence = dUtils.promptForMissingArguments(missingParameters, this.sentence, output);
              paramsValuesList = dUtils.getParamsValues(this.sentence.getParams());
            } else if (missingParameters.size() > 0) {
              throw new Exception("You need to specify the following parameter/s: " + missingParameters.toString());
            }

            paramsValuesList = dUtils.orderParameters(this.sentence.getParams(), commandNeededParams);
          }

          dUtils.LaunchCommand(module, this.sentence.getCommandName(), paramsValuesList);

        } else {
          throw new NotRecognizedCommandException(this.sentence.getModuleName(), this.sentence.getCommandName());
        }
      }

    } else {
      throw new NotRecognizedModuleException(this.sentence.getModuleName());
    }

  }

}
