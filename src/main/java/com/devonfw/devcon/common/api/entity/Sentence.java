package com.devonfw.devcon.common.api.entity;

import java.util.HashMap;
import java.util.List;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class Sentence {

  public String gParam;

  public String moduleName;

  public String commandName;

  public List<HashMap<String, String>> params;

  public String context;

  public boolean noPrompt;

  public boolean helpRequested;
}
