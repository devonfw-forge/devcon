package com.devonfw.devcon.common.api.data;

import java.io.File;

public class FileBucket {

  private File file;

  private String search;

  private String replace;

  /**
   * @return file
   */
  public File getFile() {

    return this.file;
  }

  /**
   * @param file new value of {@link #getfile}.
   */
  public void setFile(File file) {

    this.file = file;
  }

  /**
   * @return search
   */
  public String getSearch() {

    return this.search;
  }

  /**
   * @param search new value of {@link #getsearch}.
   */
  public void setSearch(String search) {

    this.search = search;
  }

  /**
   * @return replace
   */
  public String getReplace() {

    return this.replace;
  }

  /**
   * @param replace new value of {@link #getreplace}.
   */
  public void setReplace(String replace) {

    this.replace = replace;
  }

}
