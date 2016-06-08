package com.devonfw.devcon.common;

import java.util.List;

import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.data.CommandParameter;
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
    List<String> sentenceParams = dUtils.getParamsKeys(this.sentence.getParams());
    List<CommandParameter> commandNeededParams;

    Class<?> module = dUtils.getModule(this.sentence.getModuleName());

    if (module != null) {

      // If no command given OR helpRequested flag is 'true' the app shows the help info and ends
      if (this.sentence.getCommandName() == null || this.sentence.isHelpRequested()) {

        dUtils.showHelp(module, this.sentence);

      } else {

        Command command = dUtils.getCommand(module, this.sentence.getCommandName());
        if (command != null) {

          commandNeededParams = dUtils.getCommandParameters(module, this.sentence.getCommandName());
          if (commandNeededParams != null) {
            List<CommandParameter> missingParameters = dUtils.getMissingParameters(sentenceParams, commandNeededParams);

            if (missingParameters.size() > 0) {

              this.sentence = dUtils.obtainValueForMissingParameters(missingParameters, this.sentence, output);

              // check again for missing parameters
              sentenceParams = dUtils.getParamsKeys(this.sentence.getParams());
              missingParameters = dUtils.getMissingParameters(sentenceParams, commandNeededParams);
              if (missingParameters.size() > 0) {
                dUtils.endAndShowMissingParameters(missingParameters);
              }

              paramsValuesList = dUtils.getParamsValues(this.sentence.getParams());

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
