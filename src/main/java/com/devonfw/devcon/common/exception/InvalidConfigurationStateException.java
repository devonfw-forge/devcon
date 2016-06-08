package com.devonfw.devcon.common.exception;

public class InvalidConfigurationStateException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Exception thrown when a particular config or settings file contains illegal keys and/or values or encounters an
   * unexpected exception
   *
   * @param msg Text to denote exception
   */
  public InvalidConfigurationStateException(String msg) {
    super(msg);
  }

  /**
   * Exception thrown when a particular config or settings file contains illegal keys and/orvalues or encounters an
   * enexpected exception
   *
   * @param msg original Exception
   */
  public InvalidConfigurationStateException(Exception err) {
    super(err);
  }

}
