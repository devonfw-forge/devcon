package com.devonfw.devcon.common.api.data;

public class FileMessage {

  private String filePath;

  private String fileMessage;

  /**
   * @return filePath
   */
  public String getFilePath() {

    return this.filePath;
  }

  /**
   * @param filePath new value of {@link #getfilePath}.
   */
  public void setFilePath(String filePath) {

    this.filePath = filePath;
  }

  /**
   * @return fileMessage
   */
  public String getFileMessage() {

    return this.fileMessage;
  }

  /**
   * @param fileMessage new value of {@link #getfileMessage}.
   * @param j
   * @param i
   * @param string
   */
  public void setFileMessage(String fileMessage) {

    this.fileMessage = fileMessage;
  }

}
