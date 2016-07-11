package com.devonfw.devcon.common.api.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Represents the command line parameters as provided by the user when executing a command
 *
 * @author pparrado
 */
public class Sentence {

  private String gParam;

  private String moduleName;

  private String commandName;

  private List<Pair<String, String>> params;

  private String context;

  private boolean helpRequested;

  public Sentence() {

    this.params = new ArrayList<Pair<String, String>>();
  }

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
  public List<Pair<String, String>> getParams() {

    return this.params;
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

  /**
   * @param optName parameter name given
   * @param optValue parameter value
   */
  public void addParam(String optName, String optValue) {

    this.params.add(Pair.of(optName, optValue));
  }

}
