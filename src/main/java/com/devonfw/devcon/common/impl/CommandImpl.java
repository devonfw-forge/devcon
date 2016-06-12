package com.devonfw.devcon.common.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.ParameterType;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.CommandParameter;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public class CommandImpl implements Command {

  private String name;

  private String description;

  private Method method;

  private List<CommandParameter> definedParameters;

  public CommandImpl() {
    this.definedParameters = new ArrayList<>();
  }

  public CommandImpl(String name, String description, Method method) {
    this();
    this.name = name;
    this.description = description;
    this.method = method;
    addParameters(method);
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
      for (Parameter param : paramsList) {
        CommandParameter cmdParam =
            new CommandParameter(param.name(), param.description(), param.type().equals(ParameterType.Optional));
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
  public void exec(HashMap<String, String> arguments) {

  }

  @Override
  public void exec() {

    HashMap<String, String> arguments = new HashMap<>();
    exec(arguments);

  }
}
