package com.devonfw.devcon.common.impl;

import com.devonfw.devcon.common.api.CommandModule;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.output.ConsoleOutput;
import com.devonfw.devcon.output.Output;
import com.google.common.base.Optional;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class AbstractCommandModule implements CommandModule {

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

}
