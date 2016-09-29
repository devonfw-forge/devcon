package com.devonfw.devcon.common.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.google.common.base.Optional;

/**
 * Implementation of {@link CommandModuleInfo}
 *
 * @author ivanderk
 */
public class CommandModuleInfoImpl implements CommandModuleInfo {

  private String name;

  private String description;

  private int sortValue;

  private boolean isVisible;

  private HashMap<String, Command> commands;

  public CommandModuleInfoImpl() {

    this.commands = new HashMap<>();
  }

  public CommandModuleInfoImpl(String name, String description, int sortValue, boolean isVisible,
      Class<?> moduleClass) {

    this();
    this.name = name;
    this.description = description;
    this.sortValue = sortValue;
    this.isVisible = isVisible;
    addCommands(name, moduleClass);
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
  public boolean isVisible() {

    return this.isVisible;
  }

  @Override
  public Collection<Command> getCommands() {

    return this.commands.values();
  }

  @Override
  public Optional<Command> getCommand(String name) {

    if (this.commands.containsKey(name)) {
      return Optional.of(this.commands.get(name));
    } else {
      return Optional.absent();
    }
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

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(CommandModuleInfo o) {

    return getName().compareTo(o.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getSortValue() {

    // TODO Auto-generated method stub
    return this.sortValue;
  }
}
