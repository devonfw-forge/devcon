package com.devonfw.devcon.common.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.devonfw.devcon.common.CommandResult;
import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModule;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.output.Output;
import com.google.common.base.Optional;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public class CommandImpl implements Command {

  private String name;

  private String description;

  private Class<?> module;

  private Method method;

  private List<CommandParameter> definedParameters;

  private CommandRegistry registry;

  private Optional<ProjectInfo> projectInfo;

  private Input input;

  private Output output;

  private ContextType context;

  private ContextPathInfo contextPathInfo;

  public CommandImpl() {
    this.definedParameters = new ArrayList<>();
  }

  public CommandImpl(String name, String description, ContextType context, Method method, Class<?> module) {
    this();
    this.name = name;
    this.description = description;
    this.context = context;
    this.method = method;
    this.module = module;
    addParameters(method);
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

  /**
   * @param module
   */
  private void injectEnvIfCommandModule(Object module) {

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

  void addParameters(Method method) {

    int pos = 0;

    Annotation annotation = method.getAnnotation(Parameters.class);
    if (annotation != null) {
      Parameters params = (Parameters) annotation;

      List<Parameter> paramsList = Arrays.asList(params.values());

      for (Parameter param : paramsList) {
        CommandParameter cmdParam = new CommandParameter(param.name(), param.description(), pos++, param.optional());
        this.definedParameters.add(cmdParam);
      }
    }

    // When a context is given, a default --path parameter is added to the end
    if (this.context != ContextType.NONE) {

      this.definedParameters.add(
          new CommandParameter("path", "Give path to project (current directory used when not given)", pos++, true));
    }
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
      return Triple.of(CommandResult.UNKOWN_PARAMS, msg, None);
    }
    return Triple.of(CommandResult.OK, CommandResult.OK_MSG, parameters);

  }

  @Override
  public Object exec(List<String> arguments)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    Object module = this.module.newInstance();

    injectEnvIfCommandModule(module);

    return this.method.invoke(module, arguments.toArray());
  }

  @Override
  public Object exec()
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    Object module = this.module.newInstance();

    injectEnvIfCommandModule(module);

    return this.method.invoke(module);

  }

  /**
   * @return module
   */
  public Class<?> getModule() {

    return this.module;
  }

  /**
   * @return method
   */
  public Method getMethod() {

    return this.method;
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

  /**
   * @return contextPathInfo
   */
  public ContextPathInfo getContextPathInfo() {

    return this.contextPathInfo;
  }
}
