package com.devonfw.devcon.common.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.devonfw.devcon.common.api.CommandHolder;
import com.devonfw.devcon.common.api.annotations.Command;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class AbstractCommandHolder implements CommandHolder {

  /**
   * The constructor.
   */
  public AbstractCommandHolder() {

  }

  public List<Command> getCommands() {

    List<Command> commandList = new ArrayList<Command>();

    Class<?> obj = this.getClass();

    for (Method m : obj.getMethods()) {
      if (m.isAnnotationPresent(Command.class)) {
        Annotation methodAnnotation = m.getAnnotation(Command.class);
        Command com = (Command) methodAnnotation;
        commandList.add(com);
      }
    }

    return commandList;
  }

  public Command getCommand(String name) {

    Command com = null;
    Class<?> obj = this.getClass();
    for (Method m : obj.getMethods()) {
      if (m.isAnnotationPresent(Command.class)) {
        if (m.getName().equals(name)) {
          Annotation methodAnnotation = m.getAnnotation(Command.class);
          com = (Command) methodAnnotation;
        }
      }
    }
    return com;
  }

}
