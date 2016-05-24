package com.devonfw.devcon.common.api.entity;

import java.util.HashMap;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class Sentence {

  public String gParam;

  public String cmdModuleName;

  public String cmd;

  public HashMap<String, String> params;

  public String context;

  public boolean noPrompt;

  public boolean helpRequested;
}
