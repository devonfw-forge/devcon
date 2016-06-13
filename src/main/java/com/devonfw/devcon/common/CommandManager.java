package com.devonfw.devcon.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModule;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.exception.NotRecognizedCommandException;
import com.devonfw.devcon.common.exception.NotRecognizedModuleException;
import com.devonfw.devcon.common.utils.DevconUtils;
import com.devonfw.devcon.output.Output;
import com.google.common.base.Optional;

/**
 * Implementation of the Command Manager
 *
 * @author pparrado
 */
public class CommandManager {

  private CommandRegistry registry;

  private Output output;

  private DevconUtils dUtils = new DevconUtils();

  public CommandManager() {

  }

  public CommandManager(CommandRegistry registry, Output output) {
    this();
    this.registry = registry;
    this.output = output;
  }

  public void showMainHelp() throws Exception {

    execCommand("help", "guide");
  }

  public void execCommand(String moduleName, String commandName) {

    Optional<CommandModule> module = this.registry.getCommandModule(moduleName);
    if (module.isPresent()) {
      Optional<Command> command = module.get().getCommand(commandName);
      if (command.isPresent()) {

        command.get().exec();

      } else
        throw new NotRecognizedCommandException(moduleName, commandName);
    } else {
      throw new NotRecognizedModuleException(moduleName);
    }

  }

  public void evaluate(Sentence sentence) throws Exception {

    List<String> paramsValuesList = this.dUtils.getParamsValues(sentence.getParams());
    List<String> sentenceParams = this.dUtils.getParamsKeys(sentence.getParams());
    Collection<CommandParameter> commandNeededParams;

    Optional<CommandModule> module = this.registry.getCommandModule(sentence.getModuleName());

    if (module.isPresent()) {

      // If no command given OR helpRequested flag is 'true' the app shows the help info and ends
      if (sentence.getCommandName() == null || sentence.isHelpRequested()) {

        // this.dUtils.showHelp(module, this.sentence);

      } else {

        Optional<Command> command = module.get().getCommand(sentence.getCommandName());
        if (command.isPresent()) {
          Command cmd = command.get();
          commandNeededParams = cmd.getDefinedParameters();

          Collection<CommandParameter> missingParameters = cmd.getParametersDiff(sentenceParams);

          if (missingParameters.size() > 0) {

            // this.sentence = this.dUtils.obtainValueForMissingParameters(missingParameters, this.sentence,
            // this.output);

            // check again for missing parameters
            // sentenceParams = this.dUtils.getParamsKeys(this.sentence.getParams());
            // missingParameters = this.dUtils.getMissingParameters(sentenceParams, commandNeededParams);
            // if (missingParameters.size() > 0) {
            // this.dUtils.endAndShowMissingParameters(missingParameters);
            // }

            // paramsValuesList = this.dUtils.getParamsValues(this.sentence.getParams());

          }

          // paramsValuesList = this.dUtils.orderParameters(this.sentence.getParams(), commandNeededParams);

          // this.dUtils.launchCommand(module, this.sentence.getCommandName(), paramsValuesList);

          HashMap<String, String> arguments = new HashMap<>();
          cmd.exec(arguments);

        } else {
          throw new NotRecognizedCommandException(sentence.getModuleName(), sentence.getCommandName());
        }
      }

    } else {
      throw new NotRecognizedModuleException(sentence.getModuleName());
    }

  }

  /**
   * @return output
   */
  public Output getOutput() {

    return this.output;
  }

  /**
   * @param output new value of {@link #getoutput}.
   */
  public void setOutput(Output output) {

    this.output = output;
  }

  /**
   * @return registry
   */
  public CommandRegistry getRegistry() {

    return this.registry;
  }

  /**
   * @param registry new value of {@link #getregistry}.
   */
  public void setRegistry(CommandRegistry registry) {

    this.registry = registry;
  }

}
