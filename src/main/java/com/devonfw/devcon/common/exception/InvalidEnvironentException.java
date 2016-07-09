package com.devonfw.devcon.common.exception;

/**
 * Exception thrown when the environment is in an unknown or invalid state. For example: a missing global environment
 * variable.
 *
 * @author ivanderk
 * @since 0.0.1
 */
public class InvalidEnvironentException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Exception thrown when the environment is in an unknown or invalid state. For example: a missing global environment
   * variable
   *
   * @param msg Text to denote exception
   */
  public InvalidEnvironentException(String msg) {
    super(msg);
  }

  /**
   * Exception thrown when the environment is in an unknown or invalid state. For example: a missing global environment
   * variable
   *
   * @param msg original Exception
   */
  public InvalidEnvironentException(Exception err) {
    super(err);
  }

}
