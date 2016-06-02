package com.devonfw.devcon.common.api.data;

import java.util.List;

import com.devonfw.devcon.common.api.annotations.Parameter;

/**
 * This entity encapsulates the info to be shown in the output
 *
 */
public class Response {

  /**
   * the name to be shown
   */
  private String name;

  /**
   * the description to be shown
   */
  private String description;

  /**
   * the statusMessage to be shown
   */
  private String statusMessage;

  /**
   * a list of modules based on {@link Info} class
   */
  private List<Info> modulesList;

  /**
   * a list of commands based on {@link Info} class
   */
  private List<Info> commandsList;

  /**
   * a list of parameters based on {@link Parameter} class
   */
  private List<Parameter> commandParamsList;

  /**
   * a list of global parameters
   */
  private List<DevconOption> globalParameters;

  /**
   * the header of the output
   */
  private String header;

  /**
   * the footer of the output
   */
  private String footer;

  /**
   * the usage
   */
  private String usage;

  /**
   * @return name
   */
  public String getName() {

    return this.name;
  }

  /**
   * @param name new value of {@link #getname}.
   */
  public void setName(String name) {

    this.name = name;
  }

  /**
   * @return description
   */
  public String getDescription() {

    return this.description;
  }

  /**
   * @param description new value of {@link #getdescription}.
   */
  public void setDescription(String description) {

    this.description = description;
  }

  /**
   * @return statusMessage
   */
  public String getStatusMessage() {

    return this.statusMessage;
  }

  /**
   * @param statusMessage new value of {@link #getstatusMessage}.
   */
  public void setStatusMessage(String statusMessage) {

    this.statusMessage = statusMessage;
  }

  /**
   * @return modulesList
   */
  public List<Info> getModulesList() {

    return this.modulesList;
  }

  /**
   * @param modulesList new value of {@link #getmodulesList}.
   */
  public void setModulesList(List<Info> modulesList) {

    this.modulesList = modulesList;
  }

  /**
   * @return commandsList
   */
  public List<Info> getCommandsList() {

    return this.commandsList;
  }

  /**
   * @param commandsList new value of {@link #getcommandsList}.
   */
  public void setCommandsList(List<Info> commandsList) {

    this.commandsList = commandsList;
  }

  /**
   * @return commandParamsList
   */
  public List<Parameter> getCommandParamsList() {

    return this.commandParamsList;
  }

  /**
   * @param commandParamsList new value of {@link #getcommandParamsList}.
   */
  public void setCommandParamsList(List<Parameter> commandParamsList) {

    this.commandParamsList = commandParamsList;
  }

  /**
   * @return globalParameters
   */
  public List<DevconOption> getGlobalParameters() {

    return this.globalParameters;
  }

  /**
   * @param globalParameters new value of {@link #getglobalParameters}.
   */
  public void setGlobalParameters(List<DevconOption> globalParameters) {

    this.globalParameters = globalParameters;
  }

  /**
   * @return header
   */
  public String getHeader() {

    return this.header;
  }

  /**
   * @param header new value of {@link #getheader}.
   */
  public void setHeader(String header) {

    this.header = header;
  }

  /**
   * @return footer
   */
  public String getFooter() {

    return this.footer;
  }

  /**
   * @param footer new value of {@link #getfooter}.
   */
  public void setFooter(String footer) {

    this.footer = footer;
  }

  /**
   * @return usage
   */
  public String getUsage() {

    return this.usage;
  }

  /**
   * @param usage new value of {@link #getusage}.
   */
  public void setUsage(String usage) {

    this.usage = usage;
  }

}
