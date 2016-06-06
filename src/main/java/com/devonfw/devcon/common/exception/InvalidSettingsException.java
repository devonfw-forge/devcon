package com.devonfw.devcon.common.exception;

public class InvalidSettingsException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Exception thrown when a particular config or settings file contains illegal keys and/orvalues
   *
   * @param msg Text to denote exception
   */
  public InvalidSettingsException(String msg) {
    super(msg);
  }

}
