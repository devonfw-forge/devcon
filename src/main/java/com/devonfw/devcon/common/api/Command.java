package com.devonfw.devcon.common.api;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.Info;
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
public interface Command extends Info {

  ContextType getContext();

  List<CommandParameter> getDefinedParameters();

  List<CommandParameter> getParametersWithInput(List<Pair<String, String>> list);

  Object exec(List<String> arguments)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

  Object exec()
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

  void injectEnvironment(CommandRegistry registry, Input input, Output output, Optional<ProjectInfo> projectInfo);

}