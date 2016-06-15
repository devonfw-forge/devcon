package com.devonfw.devcon.common.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModule;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.Response;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.output.Output;

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

  private ContextPathInfo contextPathInfo;

  private Input input;

  private Output output;

  private Response response;

  public CommandImpl() {
    this.definedParameters = new ArrayList<>();
  }

  public CommandImpl(String name, String description, Method method, Class<?> module) {
    this();
    this.name = name;
    this.description = description;
    this.method = method;
    this.module = module;
    addParameters(method);
  }

  @Override
  public void injectEnvironment(CommandRegistry registry, Input input, Output output, Response response,
      ContextPathInfo contextPathInfo) {

    this.registry = registry;
    this.contextPathInfo = contextPathInfo;
    this.input = input;
    this.output = output;
    this.response = response;
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
  public Collection<CommandParameter> getDefinedParameters() {

    return this.definedParameters;
  }

  void addParameters(Method method) {

    Annotation annotation = method.getAnnotation(Parameters.class);
    if (annotation != null) {
      Parameters params = (Parameters) annotation;

      List<Parameter> paramsList = Arrays.asList(params.values());
      int pos = 0;
      for (Parameter param : paramsList) {
        CommandParameter cmdParam =
            new CommandParameter(param.name(), param.description(), pos++, param.parametertype());
        this.definedParameters.add(cmdParam);
      }
    }
  }

  @Override
  public Collection<CommandParameter> getParametersDiff(List<String> sentenceParams) {

    List<CommandParameter> missingParam = new ArrayList<>();

    for (CommandParameter commandArg : getDefinedParameters()) {
      if (!sentenceParams.contains(commandArg.getName()))
        missingParam.add(commandArg);
    }
    return missingParam;
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
   * @param module
   */
  private void injectEnvIfCommandModule(Object module) {

    // When Command Module, inject environment
    if (module instanceof CommandModule) {
      CommandModule module_ = (CommandModule) module;
      module_.setRegistry(this.registry);
      module_.setContextPathInfo(this.contextPathInfo);
      module_.setInput(this.input);
      module_.setOutput(this.output);
      module_.setResponse(this.response);
    }
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
   * @return contextPathInfo
   */
  public ContextPathInfo getContextPathInfo() {

    return this.contextPathInfo;
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
   * @return response
   */
  public Response getResponse() {

    return this.response;
  }

  /**
   * @return registry
   */
  public CommandRegistry getRegistry() {

    return this.registry;
  }
}
