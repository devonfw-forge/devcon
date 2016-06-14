package com.devonfw.devcon.common.api;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.Info;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public interface Command extends Info {

  Collection<CommandParameter> getDefinedParameters();

  Collection<CommandParameter> getParametersDiff(List<String> sentenceParams);

  Object exec(List<String> arguments)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

  Object exec()
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

  /**
   * @param sentenceParams
   * @return
   */

}
