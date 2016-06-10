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

  DevconUtils dUtils = new DevconUtils();

  OutputConsole output = new OutputConsole();

  public CmdManager() {

  }

  public CmdManager(Sentence sentence) {

    this.sentence = sentence;
  }

  public void showMainHelp() throws Exception {

    this.dUtils.launchCommand("help", "guide");
  }

  public void evaluate() throws Exception {

    List<String> paramsValuesList = this.dUtils.getParamsValues(this.sentence.getParams());
    List<String> sentenceParams = this.dUtils.getParamsKeys(this.sentence.getParams());
    List<CommandParameter> commandNeededParams;

    Class<?> module = this.dUtils.getModule(this.sentence.getModuleName());

    if (module != null) {

      // If no command given OR helpRequested flag is 'true' the app shows the help info and ends
      if (this.sentence.getCommandName() == null || this.sentence.isHelpRequested()) {

        this.dUtils.showHelp(module, this.sentence);

      } else {

        Command command = this.dUtils.getCommand(module, this.sentence.getCommandName());
        if (command != null) {

          commandNeededParams = this.dUtils.getCommandParameters(module, this.sentence.getCommandName());
          if (commandNeededParams != null) {
            List<CommandParameter> missingParameters =
                this.dUtils.getMissingParameters(sentenceParams, commandNeededParams);

            if (missingParameters.size() > 0) {

              this.sentence =
                  this.dUtils.obtainValueForMissingParameters(missingParameters, this.sentence, this.output);

              // check again for missing parameters
              sentenceParams = this.dUtils.getParamsKeys(this.sentence.getParams());
              missingParameters = this.dUtils.getMissingParameters(sentenceParams, commandNeededParams);
              if (missingParameters.size() > 0) {
                this.dUtils.endAndShowMissingParameters(missingParameters);
              }

              paramsValuesList = this.dUtils.getParamsValues(this.sentence.getParams());

            }

            paramsValuesList = this.dUtils.orderParameters(this.sentence.getParams(), commandNeededParams);
          }

          this.dUtils.launchCommand(module, this.sentence.getCommandName(), paramsValuesList);

        } else {
          throw new NotRecognizedCommandException(this.sentence.getModuleName(), this.sentence.getCommandName());
        }
      }

    } else {
      throw new NotRecognizedModuleException(this.sentence.getModuleName());
    }

  }

}
