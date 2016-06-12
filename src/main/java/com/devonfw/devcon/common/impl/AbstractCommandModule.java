package com.devonfw.devcon.common.impl;

import com.devonfw.devcon.common.api.data.Response;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.common.utils.DevconUtils;
import com.devonfw.devcon.output.OutputConsole;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class AbstractCommandModule {

  /**
   * {@link DevconUtils} instance
   */
  protected DevconUtils dUtils;

  /**
   * {@link Response} instance
   */
  protected Response response;

  /**
   * {@link OutputConsole} instance
   */
  protected OutputConsole output;

  /**
   * {@link ContextPathInfo} instance
   */
  protected ContextPathInfo contextPathInfo;

  /**
   * The constructor.
   */
  public AbstractCommandModule() {

  }

  // @Override
  /*
   * @Override public List<Command> getCommands() {
   *
   * List<Command> commandList = new ArrayList<>();
   *
   * Class<?> obj = this.getClass();
   *
   * for (Method m : obj.getMethods()) { if (m.isAnnotationPresent(Command.class)) { Annotation methodAnnotation =
   * m.getAnnotation(Command.class); Command com = (Command) methodAnnotation; commandList.add(com); } }
   *
   * return commandList; }
   */

  // @Override

  /*
   * public Optional<Command> getCommand(String name) {
   *
   * Command com = null; Class<?> obj = this.getClass(); for (Method m : obj.getMethods()) { if
   * (m.isAnnotationPresent(Command.class)) { if (m.getName().equals(name)) { Annotation methodAnnotation =
   * m.getAnnotation(Command.class); com = (Command) methodAnnotation;
   *
   * } } } return Optional.of(com); }
   */
  /**
   * @return dUtils
   */
  public DevconUtils getUtils() {

    return this.dUtils;
  }

  /**
   * @return response
   */
  public Response getResponse() {

    return this.response;
  }

  /**
   * @return output
   */
  public OutputConsole getOutput() {

    return this.output;
  }

  /**
   * @return contextPathInfo
   */
  public ContextPathInfo getContextPathInfo() {

    return this.contextPathInfo;
  }

}
