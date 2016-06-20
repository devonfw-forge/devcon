package com.devonfw.devcon.common;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.json.simple.JSONObject;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.ProjectInfo;
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

  /**
   * Execute command without parameters
   *
   * @param moduleName
   * @param commandName
   * @return Result of execution of command
   */

  public Pair<CommandResult, String> execCommand(String moduleName, String commandName)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    Optional<CommandModuleInfo> module = this.registry.getCommandModule(moduleName);
    if (module.isPresent()) {
      Optional<Command> command = module.get().getCommand(commandName);
      if (command.isPresent()) {

        Command cmd = command.get();
        Optional<ProjectInfo> projectInfo = Optional.absent();
        cmd.injectEnvironment(this.registry, this.input, this.output, this.contextPathInfo, projectInfo);

        Object result = cmd.exec();
        if (result == null) {
          return Pair.of(CommandResult.OK, CommandResult.OK_MSG);
        } else {
          return Pair.of(CommandResult.OK, result.toString());
        }

      } else

        this.output.showError("[ERROR] The command " + commandName + " is not recognized as valid command of the "
            + moduleName + " module");
      return Pair.of(CommandResult.UNKNOWN_COMMAND, moduleName + " " + commandName);

    } else {
      this.output.showError("[ERROR] The module " + moduleName + " is not recognized as available module.");
      return Pair.of(CommandResult.UNKNOWN_MODULE, moduleName);
    }
  }

  /**
   * Execute command with command line input
   *
   * @param sentence
   * @return
   * @throws Exception
   */
  public Pair<CommandResult, String> execCmdLine(Sentence sentence) throws Exception {

    Optional<CommandModuleInfo> module = this.registry.getCommandModule(sentence.getModuleName());

    if (module.isPresent()) {

      CommandModuleInfo mod = module.get();
      // If no command given and helpRequested flag is 'true' the app shows the help info and ends
      if (sentence.getCommandName() == null && sentence.isHelpRequested()) {

        this.output.showModuleHelp(mod);
        return Pair.of(CommandResult.HELP_SHOWN, "module: " + mod);

      } else {

        Optional<Command> command = module.get().getCommand(sentence.getCommandName());
        if (command.isPresent()) {

          Command cmd = command.get();
          return execCommand(cmd, sentence);

        } else {
          this.output.showError("[ERROR] The command " + sentence.getCommandName()
              + " is not recognized as valid command of the " + sentence.getModuleName() + " module");
          return Pair.of(CommandResult.UNKNOWN_COMMAND, sentence.getModuleName() + " " + sentence.getCommandName());
        }
      }
    } else {

      this.output
          .showError("[ERROR] The module " + sentence.getModuleName() + " is not recognized as available module.");
      return Pair.of(CommandResult.UNKNOWN_MODULE, sentence.getModuleName());
    }

  }

  /**
   * Execute command
   *
   * @param sentence
   * @param cmd
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  private Pair<CommandResult, String> execCommand(Command cmd, Sentence sentence)
      throws InstantiationException, IllegalAccessException, InvocationTargetException {

    if (sentence.isHelpRequested()) {
      this.output.showCommandHelp(cmd);
      return Pair.of(CommandResult.HELP_SHOWN, "command: " + cmd.getName());
    }

    Triple<CommandResult, String, List<CommandParameter>> completedResult =
        cmd.getParametersWithInput(sentence.getParams());

    // in case of missing mandatory or not existing parameters
    CommandResult cmdRes = completedResult.getLeft();
    String msg = completedResult.getMiddle();
    if (cmdRes == CommandResult.MANDATORY_PARAMS_MISSING) {
      this.output.showError("Mandatory parameter missing: " + msg);
      return Pair.of(cmdRes, msg);
    } else if (cmdRes == CommandResult.UNKNOWN_PARAMS) {
      this.output.showError("Invalid parameter(s): " + msg);
      return Pair.of(cmdRes, msg);
    }

    List<CommandParameter> givenParameters = completedResult.getRight();

    // if context needs to be given; add projectinfo from last parameter --path (optional)
    // then remove it from the parameters to be passed to the command
    Optional<ProjectInfo> projectInfo;
    if (cmd.getContext() == ContextType.NONE) {
      projectInfo = Optional.absent();
    } else if (cmd.getContext() == ContextType.COMBINEDPROJECT) {

      CommandParameter pathParam = givenParameters.get(givenParameters.size() - 1);
      String path = (pathParam.getValue().isPresent()) ? pathParam.getValue().get() : "";
      projectInfo = getContextPathInfo().getCombinedProjectRoot(path);
      givenParameters.remove(givenParameters.size() - 1);

    } else {
      CommandParameter pathParam = givenParameters.get(givenParameters.size() - 1);
      String path = (pathParam.getValue().isPresent()) ? pathParam.getValue().get() : "";
      projectInfo = getContextPathInfo().getProjectRoot(path);
      givenParameters.remove(givenParameters.size() - 1);
    }

    // optionally load missing values from config files
    List<String> completedparameters = completeParameters(projectInfo, givenParameters);

    // load environment api for command
    cmd.injectEnvironment(this.registry, this.input, this.output, new ContextPathInfo(), projectInfo);
    Object result = cmd.exec(completedparameters);
    if (result == null) {
      return Pair.of(CommandResult.OK, CommandResult.OK_MSG);
    } else {
      return Pair.of(CommandResult.OK, result.toString());
    }

  }

  /**
   * Get Possible missing parameter values from Project file (if Context != None), otherwise set missing values to empty
   * string (not null)
   *
   * @param projectInfo
   * @param commandNeededParams
   * @return
   */
  private List<String> completeParameters(Optional<ProjectInfo> projectInfo, List<CommandParameter> parameters) {

    if (projectInfo.isPresent()) {
      return completeParametersfrom(projectInfo.get(), parameters);
    } else {
      return completeParameters(parameters);
    }
  }

  /**
   * Get Possible missing parameter values from Project file
   *
   * @param projectInfo
   * @param parameters
   * @return
   */
  private List<String> completeParametersfrom(ProjectInfo projectInfo, List<CommandParameter> parameters) {

    List<String> values = new ArrayList<>();
    JSONObject config = projectInfo.getConfig();

    for (CommandParameter param : parameters) {

      String key = param.getName();
      if (param.getValue().isPresent()) {
        values.add(param.getValue().get());
      } else {

        if (config.containsKey(key)) {
          values.add(config.get(key).toString());
        } else {
          values.add("");
        }
      }
    }
    return values;
  }

  /**
   * @param parameters
   * @return
   */
  private List<String> completeParameters(List<CommandParameter> parameters) {

    List<String> values = new ArrayList<>();
    for (CommandParameter param : parameters) {

      if (param.getValue().isPresent()) {
        values.add(param.getValue().get());
      } else {
        values.add("");
      }
    }
    return values;
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
