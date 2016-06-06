package com.devonfw.devcon.common.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.devonfw.devcon.common.api.CommandHolder;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.data.Response;
import com.devonfw.devcon.common.utils.DevconUtils;
import com.devonfw.devcon.output.OutputConsole;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class AbstractCommandHolder implements CommandHolder {

  /**
   * {@link DevconUtils} instance
   */
  protected DevconUtils dUtils = new DevconUtils();

  /**
   * {@link Response} instance
   */
  protected Response response = new Response();

  /**
   * {@link OutputConsole} instance
   */
  protected OutputConsole output = new OutputConsole();

  /**
   * The constructor.
   */
  public AbstractCommandHolder() {

  }

  // @Override
  public List<Command> getCommands() {

    List<Command> commandList = new ArrayList();

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

  // @Override
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
