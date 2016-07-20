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

  private boolean isVisible;

  private HashMap<String, Command> commands;

  public CommandModuleInfoImpl() {
    this.commands = new HashMap<>();
  }

  public CommandModuleInfoImpl(String name, String description, boolean isVisible, Class<?> moduleClass) {
    this();
    this.name = name;
    this.description = description;
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
          Command cmdImpl =
              new CommandImpl(cmd.name(), cmd.description(), cmd.context(), method, moduleName, moduleClass);
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
}
