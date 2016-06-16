package com.devonfw.devcon.common;

import static com.devonfw.devcon.common.utils.Utils.unzipList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.annotations.ParameterType;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.input.Input;
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

  private Input input;

  private ContextPathInfo contextPathInfo;

  public CommandManager() {

  }

  public CommandManager(CommandRegistry registry, Input input, Output output, ContextPathInfo contextPathInfo) {
    this();
    this.registry = registry;
    this.input = input;
    this.output = output;
    this.contextPathInfo = contextPathInfo;
  }

  public CommandManager(CommandRegistry registry, Input input, Output output) {

    this(registry, input, output, new ContextPathInfo());

  }

  public void showMainHelp() throws Exception {

    execCommand("help", "guide");
  }

  public Pair<CommandResult, String> execCommand(String moduleName, String commandName)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    Optional<CommandModuleInfo> module = this.registry.getCommandModule(moduleName);
    if (module.isPresent()) {
      Optional<Command> command = module.get().getCommand(commandName);
      if (command.isPresent()) {

        Command cmd = command.get();

        cmd.injectEnvironment(this.registry, this.input, this.output, this.contextPathInfo);

        cmd.exec();
        return Pair.of(CommandResult.OK, CommandResult.OK_MSG);

      } else

        this.output.showError("[ERROR] The command " + commandName + " is not recognized as valid command of the "
            + moduleName + " module");
      return Pair.of(CommandResult.CommandNotRecognized, moduleName + " " + commandName);

    } else {
      this.output.showError("[ERROR] The module " + moduleName + " is not recognized as available module.");
      return Pair.of(CommandResult.ModuleNotRecognized, moduleName);
    }
  }

  public Pair<CommandResult, String> execCmdLine(Sentence sentence) throws Exception {

    Optional<CommandModuleInfo> module = this.registry.getCommandModule(sentence.getModuleName());

    if (module.isPresent()) {

      CommandModuleInfo mod = module.get();
      // If no command given and helpRequested flag is 'true' the app shows the help info and ends
      if (sentence.getCommandName() == null && sentence.isHelpRequested()) {

        this.output.showModuleHelp(mod);
        return Pair.of(CommandResult.HelpShown, "module: " + mod);

      } else {

        Optional<Command> command = module.get().getCommand(sentence.getCommandName());
        if (command.isPresent()) {

          Command cmd = command.get();
          if (sentence.isHelpRequested()) {
            this.output.showCommandHelp(cmd);
            return Pair.of(CommandResult.HelpShown, "command: " + cmd.getName());
          }

          cmd.injectEnvironment(this.registry, this.input, this.output, this.contextPathInfo);
          Collection<CommandParameter> commandNeededParams = cmd.getDefinedParameters();

          Collection<CommandParameter> missingParameters =
              cmd.getParametersDiff(unzipList(sentence.getParams()).getLeft());

          List<String> givenParameters;
          if (missingParameters.size() > 0) {

            Pair<Boolean, String> mandatoryMissing = mandatoryParamsMissing(missingParameters);
            if (mandatoryMissing.getLeft()) {
              this.output.showError("Missing mandatory parameter(s): " + mandatoryMissing.getRight());
              return Pair.of(CommandResult.MandatoryParameterMissing, mandatoryMissing.getRight());
            }

            Triple<Boolean, String, List<String>> withAllParams =
                completeWithMissingParameters(sentence, commandNeededParams, missingParameters);
            if (!withAllParams.getLeft()) {

              this.output.showError("Missing  parameter(s): " + withAllParams.getMiddle());
              return Pair.of(CommandResult.OptionalParameterMissing, withAllParams.getMiddle());
            }

            givenParameters = withAllParams.getRight();
          } else {
            givenParameters = unzipList(sentence.getParams()).getRight();
          }

          cmd.exec(givenParameters);

        } else {
          this.output.showError("[ERROR] The command " + sentence.getCommandName()
              + " is not recognized as valid command of the " + sentence.getModuleName() + " module");
          return Pair.of(CommandResult.CommandNotRecognized,
              sentence.getModuleName() + " " + sentence.getCommandName());
        }
      }
    } else {

      this.output
          .showError("[ERROR] The module " + sentence.getModuleName() + " is not recognized as available module.");
      return Pair.of(CommandResult.ModuleNotRecognized, sentence.getModuleName());
    }
    return Pair.of(CommandResult.OK, CommandResult.OK_MSG);

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

  /**
   * @return
   */
  public Set<String> getParameterNames() {

    Set<String> options = new HashSet<>();
    for (CommandModuleInfo module : this.registry.getCommandModules()) {
      for (Command command : module.getCommands()) {
        for (CommandParameter param : command.getDefinedParameters()) {
          String name = param.getName();
          if (!options.contains(name)) {
            options.add(name);
          }
        }
      }
    }
    return options;
  }

  /**
   * @param missingParameters
   * @return
   */
  private Pair<Boolean, String> mandatoryParamsMissing(Collection<CommandParameter> missingParameters) {

    boolean mandatoryMissing = false;
    StringBuilder sb = new StringBuilder();
    for (CommandParameter param : missingParameters) {
      if (param.getParameterType() == ParameterType.Mandatory) {
        sb.append((mandatoryMissing) ? ", " : "" + param.getName());
        mandatoryMissing = true;
      }
    }

    return Pair.of(mandatoryMissing, sb.toString());
  }

  /**
   * @param sentence
   * @param commandNeededParams
   * @param missingParameters
   * @return
   */
  private Triple<Boolean, String, List<String>> completeWithMissingParameters(Sentence sentence,
      Collection<CommandParameter> definedParams, Collection<CommandParameter> missingParameters) {

    List<CommandParameter> allParams = new ArrayList<>();
    for (CommandParameter definedParam : definedParams) {

    }

    unzipList(sentence.getParams()).getRight();

    return null;
  }

  /**
   * @return contextPathInfo
   */
  public ContextPathInfo getContextPathInfo() {

    return this.contextPathInfo;
  }

  /**
   * @param contextPathInfo new value of {@link #getcontextPathInfo}.
   */
  public void setContextPathInfo(ContextPathInfo contextPathInfo) {

    this.contextPathInfo = contextPathInfo;
  }

}
