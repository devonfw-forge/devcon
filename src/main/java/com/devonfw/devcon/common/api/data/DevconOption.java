package com.devonfw.devcon.common.api.data;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class DevconOption {

  public String opt;

  public String longOpt;

  public String description;

  public DevconOption(String opt, String longOpt, String description) {

    this.opt = opt;
    this.longOpt = longOpt;
    this.description = description;
  }
}
