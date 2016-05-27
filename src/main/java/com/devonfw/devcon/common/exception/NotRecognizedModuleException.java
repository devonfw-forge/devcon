package com.devonfw.devcon.common.exception;

/**
 * Exception to throw if a module is not recognized
 *
 * @author pparrado
 */
public class NotRecognizedModuleException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Name for the module
   */
  public String moduleName;

  /**
   * The constructor.
   * 
   * @param moduleName the module associated with the exception
   */
  public NotRecognizedModuleException(String moduleName) {

    super();
    this.moduleName = moduleName;
  }

}
