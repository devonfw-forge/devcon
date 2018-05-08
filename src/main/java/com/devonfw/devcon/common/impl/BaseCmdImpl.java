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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.CommandResult;
import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModule;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.InputTypeNames;
import com.devonfw.devcon.common.api.data.ParameterInputType;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.output.Output;
import com.google.common.base.Optional;

/**
 * Implementation of {@link Command}
 *
 * @author ivanderk
 */
public abstract class BaseCmdImpl implements Command {

  protected String name;

  protected String description;

  protected int sortValue;

  protected String moduleName;

  protected List<CommandParameter> definedParameters;

  protected CommandRegistry registry;

  protected Optional<ProjectInfo> projectInfo;

  protected Input input;

  protected Output output;

  protected ContextType context;

  protected boolean proxyParams;

  protected ContextPathInfo contextPathInfo;

  public BaseCmdImpl() {

    this.definedParameters = new ArrayList<>();
  }

  @Override
  public void injectEnvironment(CommandRegistry registry, Input input, Output output, ContextPathInfo contextPathInfo,
      Optional<ProjectInfo> projectInfo) {

    this.registry = registry;
    this.projectInfo = projectInfo;
    this.input = input;
    this.output = output;
    this.contextPathInfo = contextPathInfo;

  }

  protected void patchParameters() {

    int length = this.definedParameters.size();
    // When a context is given, a default --path parameter is added to the end
    if (this.context != ContextType.NONE) {

      this.definedParameters
          .add(new CommandParameter("path", "Give path to project (current folder used when not given)", length++, true,
              new ParameterInputType(InputTypeNames.PATH)));
    }

    if (this.proxyParams) {
      this.definedParameters
          .add(new CommandParameter("proxyHost", "Host parameter for optional Proxy configuration", length++, true));
      this.definedParameters
          .add(new CommandParameter("proxyPort", "Port parameter for optional Proxy configuration", length++, true));
    }
  }

  /**
   * @param module
   */
  protected void injectEnvIfCommandModule(Object module) {

    // When Command Module, inject environment
    if (module instanceof CommandModule) {
      CommandModule module_ = (CommandModule) module;
      module_.setRegistry(this.registry);
      module_.setProjectInfo(this.projectInfo);
      module_.setInput(this.input);
      module_.setOutput(this.output);
      module_.setContextPathInfo(this.contextPathInfo);
    }
  }

  @Override
  public String getName() {

    return this.name;
  }

  @Override
  public String getDescription() {

    return this.description;
  }

  @Override
  public List<CommandParameter> getDefinedParameters() {

    return this.definedParameters;
  }

  @Override
  public Triple<CommandResult, String, List<CommandParameter>> getParametersWithInput(
      List<Pair<String, String>> givenParameters) {

    Map<String, String> given = new HashMap<>();
    for (Pair<String, String> param : givenParameters) {
      given.put(param.getLeft().toLowerCase(), param.getRight());
    }

    List<CommandParameter> parameters = new ArrayList<>();
    List<CommandParameter> None = new ArrayList<>();

    for (CommandParameter defined : getDefinedParameters()) {
      CommandParameter val = new CommandParameter(defined.getName().toLowerCase(), defined.getDescription(),
          defined.getPosition(), defined.isOptional());

      // take value when given
      if (given.containsKey(val.getName())) {
        val.setValue(given.get(val.getName()));
        given.remove(val.getName());
      }

      // error when mandatory and no value
      if (!val.isOptional() && !val.getValue().isPresent()) {
        return Triple.of(CommandResult.MANDATORY_PARAMS_MISSING, val.getName(), None);
      }
      parameters.add(val);
    }

    // not existing parameters given
    if (given.size() > 0) {
      String msg = "Unknown parameters : " + given.keySet().toString();
      return Triple.of(CommandResult.UNKNOWN_PARAMS, msg, None);
    }
    return Triple.of(CommandResult.OK, CommandResult.OK_MSG, parameters);

  }

  /**
   * @return input
   */
  public Input getInput() {

    return this.input;
  }

  /**
   * @return output
   */
  public Output getOutput() {

    return this.output;
  }

  /**
   * @return registry
   */
  public CommandRegistry getRegistry() {

    return this.registry;
  }

  @Override
  public ContextType getContext() {

    return this.context;
  }

  @Override
  public boolean getProxyParams() {

    return this.proxyParams;
  }

  /**
   * @return contextPathInfo
   */
  public ContextPathInfo getContextPathInfo() {

    return this.contextPathInfo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(Command o) {

    return this.name.compareTo(o.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getHelpText() {

    String root = (Devcon.IN_EXEC_JAR) ? "resources/" : "";
    String resource = root + "help/" + this.moduleName + "/" + this.name + ".txt";
    URL txt = ClassLoader.getSystemClassLoader().getResource(resource);

    if (txt == null) {
      return "";
    } else {
      try {
        return IOUtils.toString(txt, "utf-8");
      } catch (IOException e) {
        e.printStackTrace();
        return "";
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModuleName() {

    return this.moduleName;
  }

  /**
   * @return sortValue
   */
  public int getSortValue() {

    return this.sortValue;
  }

  /**
   * @param sortValue the sortValue to set
   */
  public void setSortValue(int sortValue) {

    this.sortValue = sortValue;
  }
}
