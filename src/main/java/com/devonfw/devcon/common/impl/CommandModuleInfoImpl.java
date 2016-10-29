package com.devonfw.devcon.common.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;

/**
 * Implementation of {@link CommandModuleInfo}
 *
 * @author ivanderk
 */
public class CommandModuleInfoImpl extends BaseCmdModuleInfoImpl {

  public CommandModuleInfoImpl(String name, String description, int sortValue, boolean isVisible,
      Class<?> moduleClass) {

    super();
    this.name = name;
    this.description = description;
    this.sortValue = sortValue;
    this.isVisible = isVisible;
    addCommands(name, moduleClass);
  }

  void addCommands(String moduleName, Class<?> moduleClass) {

    try {
      for (Method method : moduleClass.getMethods()) {

        Class<? extends Annotation> klass = com.devonfw.devcon.common.api.annotations.Command.class;
        if (method.isAnnotationPresent(klass)) {

          Annotation annotation = method.getAnnotation(klass);
          com.devonfw.devcon.common.api.annotations.Command cmd =
              (com.devonfw.devcon.common.api.annotations.Command) annotation;
          Command cmdImpl = new CommandImpl(cmd.name(), cmd.description(), cmd.sort(), cmd.context(), cmd.proxyParams(),
              method, moduleName, moduleClass);
          this.commands.put(cmd.name(), cmdImpl);
        }
      }
    } catch (Exception e) {
      // TODO implement logs
      System.out.println("[ERROR] at CommandModuleImpl#addCommands: " + e.getMessage());
    }
  }

}
