package com.devonfw.devcon.common.exception;

public class InvalidSettingsException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public InvalidSettingsException(String msg) {
    super(msg);
  }

}
