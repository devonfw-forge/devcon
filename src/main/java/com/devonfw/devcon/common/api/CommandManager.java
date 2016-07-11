package com.devonfw.devcon.common.api;

import java.lang.reflect.InvocationTargetException;
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

  Pair<CommandResult, Object> execCommand(String moduleName, String commandName, String... params)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

  /**
   * Execute command with command line input
   *
   * @param sentence
   * @return
   * @throws Exception
   */
  Pair<CommandResult, Object> execCmdLine(Sentence sentence) throws Exception;

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