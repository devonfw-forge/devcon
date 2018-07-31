package com.devonfw.devcon.common.utils;

/**
 * @author adubey4
 *
 */

public class DownloadFileException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * The constructor.
   *
   * @param message is for showing the massage while exception comaing
   */
  public DownloadFileException(String message) {

    super(message);
  }
}
