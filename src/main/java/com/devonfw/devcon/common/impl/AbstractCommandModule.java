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

import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModule;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.output.ConsoleOutput;
import com.devonfw.devcon.output.Output;
import com.google.common.base.Optional;

/**
 * Abstract base class for user defined Commands, facilitating the creation of commands
 *
 * @author pparrado
 */
public abstract class AbstractCommandModule implements CommandModule {

  /**
   * {@link ConsoleOutput} instance
   */
  protected Output output;

  /**
   * {@link ConsoleOutput} instance
   */
  protected Input input;

  /**
   * {@link ContextPathInfo} instance
   */
  protected Optional<ProjectInfo> projectInfo;

  protected ContextPathInfo contextPathInfo;

  /**
   * {@link CommandRegistry} instance
   */
  protected CommandRegistry registry;

  /**
   * The constructor.
   */
  public AbstractCommandModule() {

  }

  @Override
  public Optional<Command> getCommand(String module, String command) {

    Optional<Command> cmd = this.registry.getCommand(module, command);
    if (cmd.isPresent()) {
      cmd.get().injectEnvironment(this.registry, this.input, this.output, this.contextPathInfo, this.projectInfo);
    }
    return cmd;
  }

  /**
   * @return output
   */
  @Override
  public Output getOutput() {

    return this.output;
  }

  /**
   * @return contextPathInfo
   */
  @Override
  public Optional<ProjectInfo> getProjectInfo() {

    return this.projectInfo;
  }

  /**
   * @return input
   */
  @Override
  public Input getInput() {

    return this.input;
  }

  /**
   * @param input new value of {@link #getinput}.
   */
  @Override
  public void setInput(Input input) {

    this.input = input;
  }

  /**
   * @param output new value of {@link #getoutput}.
   */
  @Override
  public void setOutput(Output output) {

    this.output = output;
  }

  /**
   * @param contextPathInfo new value of {@link #getcontextPathInfo}.
   */
  @Override
  public void setProjectInfo(Optional<ProjectInfo> projectInfo) {

    this.projectInfo = projectInfo;
  }

  @Override
  public CommandRegistry getRegistry() {

    return this.registry;
  }

  @Override
  public void setRegistry(CommandRegistry registry) {

    this.registry = registry;

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

  @Override
  public Path getPath(String path) {

    return FileSystems.getDefault().getPath(path);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<Command> getCommand(String module, String command, ProjectInfo projectInfo) {

    Optional<Command> cmd = this.registry.getCommand(module, command);
    if (cmd.isPresent()) {

      cmd.get().injectEnvironment(this.registry, this.input, this.output, this.contextPathInfo,
          Optional.of(projectInfo));
    }
    return cmd;
  }

}
