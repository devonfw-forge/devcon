package com.devonfw.devcon.common.api.data;

import java.util.List;

import com.devonfw.devcon.common.api.annotations.Parameter;

/**
 * This entity encapsulates the info to be shown in the output
 *
 */
public class Response {

  public String name;

  public String description;

  public String statusMessage;

  public List<Info> modulesList;

  public List<Info> commandsList;

  public List<Parameter> commandParamsList;

  public List<String> globalParameters;

  public String header;

  public String footer;

  public String usage;

}
