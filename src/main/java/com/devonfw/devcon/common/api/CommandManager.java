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
package com.devonfw.devcon.common.api;

import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.devonfw.devcon.common.CommandResult;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.output.Output;

/**
 * The Command Manager is the central orchestrating unit within Devcon. It is responsible for loading and executing
 * {@link CommandModule}s and {@link Command}s
 *
 * @author ivanderk
 */
public interface CommandManager {

  void showMainHelp() throws Exception;

  /**
   * Execute command without parameters
   *
   * @param moduleName
   * @param commandName
   * @return Result of execution of command
   */

  Pair<CommandResult, Object> execCommand(String moduleName, String commandName, String... params);

  /**
   * Execute command with command line input
   *
   * @param sentence
   * @return
   * @throws Exception
   */
  Pair<CommandResult, Object> execCmdLine(Sentence sentence);

  /**
   * @return output
   */
  Output getOutput();

  /**
   * @param output new value of {@link #getoutput}.
   */
  void setOutput(Output output);

  /**
   * @return registry
   */
  CommandRegistry getRegistry();

  /**
   * @param registry new value of {@link #getregistry}.
   */
  void setRegistry(CommandRegistry registry);

  /**
   * @return
   */
  Set<String> getParameterNames();

  /**
   * @return contextPathInfo
   */
  ContextPathInfo getContextPathInfo();

  /**
   * @param contextPathInfo new value of {@link #getcontextPathInfo}.
   */
  void setContextPathInfo(ContextPathInfo contextPathInfo);

}
