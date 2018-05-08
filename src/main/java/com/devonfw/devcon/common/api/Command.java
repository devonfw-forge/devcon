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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.devonfw.devcon.common.CommandResult;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.Info;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.output.Output;
import com.google.common.base.Optional;

/**
 * Represents a Devcon Command as contained and defined in a {@link CommandModule}. A Command is the actual work unit
 *
 * @author ivanderk
 */
public interface Command extends Info, Comparable<Command> {

  String getModuleName();

  ContextType getContext();

  boolean getProxyParams();

  String getHelpText();

  List<CommandParameter> getDefinedParameters();

  Triple<CommandResult, String, List<CommandParameter>> getParametersWithInput(List<Pair<String, String>> list);

  Object exec(String... arguments) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException;

  Object exec(List<String> arguments) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException;

  Object exec() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException;

  void injectEnvironment(CommandRegistry registry, Input input, Output output, ContextPathInfo contextPathInfo,
      Optional<ProjectInfo> projectInfo);

}
