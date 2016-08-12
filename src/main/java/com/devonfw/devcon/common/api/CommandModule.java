package com.devonfw.devcon.common.api;

import java.nio.file.Path;

import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.modules.oasp4j.Oasp4j;
import com.devonfw.devcon.output.Output;
import com.google.common.base.Optional;

/**
 * The container of a series of related {@link Command}s
 *
 * @author ivanderk
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

  /**
   *
   * @return ContextPathInfo
   */
  ContextPathInfo getContextPathInfo();

  /**
   *
   * @param contextPathInfo
   */
  void setContextPathInfo(ContextPathInfo contextPathInfo);

  /**
   * get {@link Command} instance
   *
   * @param module
   * @param command
   * @return
   */
  Optional<Command> getCommand(String module, String command);

  /**
   * This command can be use to call Devcon command from other module. Parameter module refers to modulename , command
   * refers to command name and projectInfo is passed from existing method. ProjectInfo contains all information related
   * to respective devcon project. Usecase: We can use this method from project module to get the commands of other
   * modules such as oasp4j, oasp4js or sencha etc. This method provide correct information for respective projects such
   * as path etc. As an example,consider we need to build combinedproject created by devcon. From project module build
   * method, we internally call 'oasp4j build()' and 'client(oasp4js or sencha) build()' method. But as path parameter
   * is optional we are not including it in command method signature e.g {@link Oasp4j#build()}. As this method do not
   * have any input parameter we cannot pass any value directly from project method to this called method. So we will
   * use getCommand(module,command, projectinfo) method which will pass all information required to called method.
   */

  Optional<Command> getCommand(String module, String command, ProjectInfo projectInfo);

  /**
   * @param path
   * @return
   */
  Path getPath(String path);

}