package com.devonfw.devcon.common.api;

import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.output.Output;
import com.google.common.base.Optional;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public interface CommandModule {

  /**
   *
   * @return registry
   */
  CommandRegistry getRegistry();

  /**
   * @return output
   */
  Output getOutput();

  /**
   * @return contextPathInfo
   */
  Optional<ProjectInfo> getProjectInfo();

  /**
   * @return input
   */
  Input getInput();

  /**
   * @param registry new value of {@link #getRegistry}
   */
  void setRegistry(CommandRegistry registry);

  /**
   * @param input new value of {@link #getinput}.
   */
  void setInput(Input input);

  /**
   * @param output new value of {@link #getoutput}.
   */
  void setOutput(Output output);

  /**
   * @param projectInfo
   */
  void setProjectInfo(Optional<ProjectInfo> projectInfo);

}