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
package com.devonfw.devcon.mocks;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.devonfw.devcon.common.CommandResult;
import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandManager;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.output.Output;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public class MockCommandManager implements CommandManager {

  private Sentence sentence;

  private CommandRegistry registry;

  private Output output;

  private Input input;

  private ContextPathInfo contextPathInfo;

  public MockCommandManager() {

  }

  public MockCommandManager(CommandRegistry registry, Output output) {
    this();
    this.registry = registry;
    this.output = output;
  }

  @Override
  public void showMainHelp() throws Exception {

    // ignore

  }

  @Override
  public Pair<CommandResult, Object> execCommand(String moduleName, String commandName, String... params) {

    return Pair.of(CommandResult.OK, (Object) "");
  }

  @Override
  public Pair<CommandResult, Object> execCmdLine(Sentence sentence) {

    this.sentence = sentence;
    return Pair.of(CommandResult.OK, (Object) "");
  }

  @Override
  public Output getOutput() {

    // ignore
    return null;
  }

  @Override
  public void setOutput(Output output) {

    // ignore

  }

  @Override
  public CommandRegistry getRegistry() {

    // ignore
    return null;
  }

  @Override
  public void setRegistry(CommandRegistry registry) {

    // ignore

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

  @Override
  public ContextPathInfo getContextPathInfo() {

    // ignore
    return null;
  }

  @Override
  public void setContextPathInfo(ContextPathInfo contextPathInfo) {

    // ignore

  }

  /**
   * @return sentence
   */
  public Sentence getSentence() {

    return this.sentence;
  }

  /**
   * @param sentence new value of {@link #getsentence}.
   */
  public void setSentence(Sentence sentence) {

    this.sentence = sentence;
  }

}
