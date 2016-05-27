package com.devonfw.devcon.common.exception;

/**
 * Exception to throw if a command is not recognized
 *
 * @author pparrado
 */
public class NotRecognizedCommandException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * The name of the module
   */
  public String moduleName;

  /**
   * The name of the command
   */
  public String commandName;

  /**
   * The constructor.
   *
   * @param moduleName name of the module associated to the exception
   * @param commandName name of the command associated to the exception
   */
  public NotRecognizedCommandException(String moduleName, String commandName) {

    super();
    this.moduleName = moduleName;
    this.commandName = commandName;
  }

}
