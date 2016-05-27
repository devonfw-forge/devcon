package com.devonfw.devcon.common.api.entity;

import java.util.List;

/**
 * This entity encapsulates the info to be shown in the output
 *
 */
public class Response {

  public String name;

  public String description;

  public String statusMessage;

  public String[] commandParamsList;

  public String[] methodsList;

  public List<String> globalParameters;

  public String header;

  public String footer;

  public String usage;

}
