package com.devonfw.devcon.common.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.ContextType;

/**
 * Implementation of {@link Command}
 *
 * @author ivanderk
 */
public class CommandImpl extends BaseCmdImpl {

  private Method method;

  private Class<?> module;

  public CommandImpl(String name, String description, int sortValue, ContextType context, boolean proxyParams,
      Method method, String moduleName, Class<?> module) {

    super();
    this.name = name;
    this.description = description;
    this.sortValue = sortValue;
    this.context = context;
    this.proxyParams = proxyParams;
    this.method = method;
    this.moduleName = moduleName;
    this.module = module;

    addParameters(method);
    patchParameters();
  }

  void addParameters(Method method) {

    int pos = 0;

    Annotation annotation = method.getAnnotation(Parameters.class);
    if (annotation != null) {
      Parameters params = (Parameters) annotation;

      List<Parameter> paramsList = Arrays.asList(params.values());

      for (Parameter param : paramsList) {
        CommandParameter cmdParam =
            new CommandParameter(param.name(), param.description(), pos++, param.optional(), param.inputType());
        this.definedParameters.add(cmdParam);
      }
    }
  }

  @Override
  public Object exec(String... arguments)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    return this.exec(Arrays.asList(arguments));
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
   * @return method
   */
  public Method getMethod() {

    return this.method;
  }

  /**
   * @return module
   */
  protected Class<?> getModule() {

    return this.module;
  }

}
