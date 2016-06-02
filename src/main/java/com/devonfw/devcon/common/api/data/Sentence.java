package com.devonfw.devcon.common.api.data;

import java.util.HashMap;
import java.util.List;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class Sentence {

  private String gParam;

  private String moduleName;

  private String commandName;

  private List<HashMap<String, String>> params;

  private String context;

  private boolean noPrompt;

  private boolean helpRequested;

  /**
   * @return gParam
   */
  public String getgParam() {

    return this.gParam;
  }

  /**
   * @param gParam new value of {@link #getgParam}.
   */
  public void setgParam(String gParam) {

    this.gParam = gParam;
  }

  /**
   * @return moduleName
   */
  public String getModuleName() {

    return this.moduleName;
  }

  /**
   * @param moduleName new value of {@link #getmoduleName}.
   */
  public void setModuleName(String moduleName) {

    this.moduleName = moduleName;
  }

  /**
   * @return commandName
   */
  public String getCommandName() {

    return this.commandName;
  }

  /**
   * @param commandName new value of {@link #getcommandName}.
   */
  public void setCommandName(String commandName) {

    this.commandName = commandName;
  }

  /**
   * @return params
   */
  public List<HashMap<String, String>> getParams() {

    return this.params;
  }

  /**
   * @param params new value of {@link #getparams}.
   */
  public void setParams(List<HashMap<String, String>> params) {

    this.params = params;
  }

  /**
   * @return context
   */
  public String getContext() {

    return this.context;
  }

  /**
   * @param context new value of {@link #getcontext}.
   */
  public void setContext(String context) {

    this.context = context;
  }

  /**
   * @return noPrompt
   */
  public boolean isNoPrompt() {

    return this.noPrompt;
  }

  /**
   * @param noPrompt new value of {@link #getnoPrompt}.
   */
  public void setNoPrompt(boolean noPrompt) {

    this.noPrompt = noPrompt;
  }

  /**
   * @return helpRequested
   */
  public boolean isHelpRequested() {

    return this.helpRequested;
  }

  /**
   * @param helpRequested new value of {@link #gethelpRequested}.
   */
  public void setHelpRequested(boolean helpRequested) {

    this.helpRequested = helpRequested;
  }

}
