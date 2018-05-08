/*******************************************************************************
 * Copyright 2015-2018 Capgemini SE.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package com.devonfw.devcon.common.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.json.simple.JSONObject;

import com.devonfw.devcon.common.CommandResult;
import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandManager;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.common.utils.Utils;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.output.Output;
import com.google.common.base.Optional;

/**
 * Implementation of {@link CommandManager}
 *
 * @author pparrado
 */
public class CommandManagerImpl implements CommandManager {

  private CommandRegistry registry;

  private Output output;

  private Input input;

  private ContextPathInfo contextPathInfo;

  public CommandManagerImpl() {

  }

  public CommandManagerImpl(CommandRegistry registry, Input input, Output output, ContextPathInfo contextPathInfo) {

    this();
    this.registry = registry;
    this.input = input;
    this.output = output;
    this.contextPathInfo = contextPathInfo;
  }

  public CommandManagerImpl(CommandRegistry registry, Input input, Output output) {

    this(registry, input, output, new ContextPathInfo());

  }

  @Override
  public void showMainHelp() throws Exception {

    execCommand("help", "overview");
  }

  /**
   * Execute command without parameters
   *
   * @param moduleName
   * @param commandName
   * @return Result of execution of command
   */

  @Override
  public Pair<CommandResult, Object> execCommand(String moduleName, String commandName, String... params) {

    Optional<CommandModuleInfo> module = this.registry.getCommandModule(moduleName);
    if (module.isPresent()) {
      Optional<Command> command = module.get().getCommand(commandName);
      if (command.isPresent()) {

        Command cmd = command.get();
        Optional<ProjectInfo> projectInfo = Optional.absent();
        cmd.injectEnvironment(this.registry, this.input, this.output, this.contextPathInfo, projectInfo);

        return execCmd(cmd, Arrays.asList(params));

      } else

        this.output.showError(
            "The command " + commandName + " is not recognized as valid command of the " + moduleName + " module");
      return Pair.of(CommandResult.UNKNOWN_COMMAND, (Object) (moduleName + " " + commandName));

    } else {
      this.output.showError("The module " + moduleName + " is not recognized as available module.");
      return Pair.of(CommandResult.UNKNOWN_MODULE, (Object) moduleName);
    }
  }

  /**
   * Execute command with command line input
   *
   * @param sentence
   * @return
   * @throws Exception
   */
  @Override
  public Pair<CommandResult, Object> execCmdLine(Sentence sentence) {

    Optional<CommandModuleInfo> module = this.registry.getCommandModule(sentence.getModuleName());

    if (module.isPresent()) {

      CommandModuleInfo mod = module.get();
      // If no command given or helpRequested flag is 'true' the app shows the help info and ends
      if (sentence.getCommandName() == null) {

        if (!sentence.isHelpRequested()) {
          this.output.showError("No command given");
        }
        this.output.showModuleHelp(mod);
        return Pair.of(CommandResult.HELP_SHOWN, (Object) ("module: " + mod));

      } else {

        Optional<Command> command = module.get().getCommand(sentence.getCommandName());
        if (command.isPresent()) {

          Command cmd = command.get();
          return execCommand(cmd, sentence);

        } else {
          this.output.showError("The command " + sentence.getCommandName()
              + " is not recognized as valid command of the " + sentence.getModuleName() + " module");
          return Pair.of(CommandResult.UNKNOWN_COMMAND,
              (Object) (sentence.getModuleName() + " " + sentence.getCommandName()));
        }
      }
    } else {

      this.output.showError("The module " + sentence.getModuleName() + " is not recognized as available module.");
      return Pair.of(CommandResult.UNKNOWN_MODULE, (Object) sentence.getModuleName());
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
  private Pair<CommandResult, Object> execCommand(Command cmd, Sentence sentence) {

    if (sentence.isHelpRequested()) {
      this.output.showCommandHelp(cmd);
      return Pair.of(CommandResult.HELP_SHOWN, (Object) ("command: " + cmd.getName()));
    }

    Triple<CommandResult, String, List<CommandParameter>> completedResult =
        cmd.getParametersWithInput(sentence.getParams());

    // in case of missing mandatory or not existing parameters
    CommandResult cmdRes = completedResult.getLeft();
    String msg = completedResult.getMiddle();
    if (cmdRes == CommandResult.MANDATORY_PARAMS_MISSING) {
      this.output.showError("Mandatory parameter missing: " + msg);
      return Pair.of(cmdRes, (Object) msg);
    } else if (cmdRes == CommandResult.UNKNOWN_PARAMS) {
      this.output.showError("Invalid parameter(s): " + msg);
      return Pair.of(cmdRes, (Object) msg);
    }

    List<CommandParameter> givenParameters = completedResult.getRight();

    if (cmd.getProxyParams()) {

      int proxyHostIndex = 0;
      int proxyPortIndex = 0;

      for (CommandParameter commandParameter : givenParameters) {
        if (commandParameter.getName().toLowerCase().equals("proxyhost")) {
          proxyHostIndex = commandParameter.getPosition();
        }
        if (commandParameter.getName().toLowerCase().equals("proxyport")) {
          proxyPortIndex = commandParameter.getPosition();
        }
      }

      // proxyHost
      CommandParameter proxyHostParam = givenParameters.get(proxyHostIndex);
      String proxyHost = (proxyHostParam.getValue().isPresent()) ? proxyHostParam.getValue().get() : "";

      // proxyPort
      CommandParameter proxyPortParam = givenParameters.get(proxyPortIndex);
      String proxyPort = (proxyPortParam.getValue().isPresent()) ? proxyPortParam.getValue().get() : "";

      if (!proxyHost.isEmpty() && !proxyPort.isEmpty()) {
        Utils.setProxy(proxyHost, proxyPort);
      }

      givenParameters.remove(proxyHostParam);
      givenParameters.remove(proxyPortParam);

    }

    // if context needs to be given; add projectinfo from last parameter --path (optional)
    // then remove it from the parameters to be passed to the command
    Optional<ProjectInfo> projectInfo;
    if (cmd.getContext() == ContextType.NONE) {
      projectInfo = Optional.absent();
    } else if (cmd.getContext() == ContextType.COMBINEDPROJECT) {

      int pathIndex = 0;
      for (CommandParameter commandParameter : givenParameters) {
        if (commandParameter.getName().toLowerCase().equals("path")) {
          pathIndex = commandParameter.getPosition();
        }
      }

      CommandParameter pathParam = givenParameters.get(pathIndex);
      String path = (pathParam.getValue().isPresent()) ? pathParam.getValue().get() : "";
      projectInfo = getContextPathInfo().getCombinedProjectRoot(path);
      givenParameters.remove(pathParam);

    } else {

      int pathIndex = 0;
      for (CommandParameter commandParameter : givenParameters) {
        if (commandParameter.getName().toLowerCase().equals("path")) {
          pathIndex = commandParameter.getPosition();
        }
      }

      CommandParameter pathParam = givenParameters.get(pathIndex);
      String path = (pathParam.getValue().isPresent()) ? pathParam.getValue().get() : "";
      projectInfo = getContextPathInfo().getProjectRoot(path);
      givenParameters.remove(pathParam);
    }

    // optionally load missing values from config files
    List<String> completedparameters = completeParameters(projectInfo, givenParameters);

    // load environment api for command
    cmd.injectEnvironment(this.registry, this.input, this.output, new ContextPathInfo(), projectInfo);
    return execCmd(cmd, completedparameters);
  }

  /**
   * @param cmd
   * @param parameters
   * @return
   */
  private Pair<CommandResult, Object> execCmd(Command cmd, List<String> parameters) {

    try {
      Object result = cmd.exec(parameters);
      return Pair.of(CommandResult.OK, result);
    } catch (Throwable ex) {
      // get original exception from java.lang.reflect.InvocationTargetException
      Throwable cause = ex.getCause();
      if (cause != null) {
        return Pair.of(CommandResult.FAILURE, (Object) cause);
      } else {
        return Pair.of(CommandResult.FAILURE, (Object) ex);
      }
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
  @Override
  public Output getOutput() {

    return this.output;
  }

  /**
   * @param output new value of {@link #getoutput}.
   */
  @Override
  public void setOutput(Output output) {

    this.output = output;
  }

  /**
   * @return registry
   */
  @Override
  public CommandRegistry getRegistry() {

    return this.registry;
  }

  /**
   * @param registry new value of {@link #getregistry}.
   */
  @Override
  public void setRegistry(CommandRegistry registry) {

    this.registry = registry;
  }

  /**
   * @return
   */
  @Override
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
  @Override
  public ContextPathInfo getContextPathInfo() {

    return this.contextPathInfo;
  }

  /**
   * @param contextPathInfo new value of {@link #getcontextPathInfo}.
   */
  @Override
  public void setContextPathInfo(ContextPathInfo contextPathInfo) {

    this.contextPathInfo = contextPathInfo;
  }

}
