package com.devonfw.devcon.common.api.data;

import com.devonfw.devcon.common.api.annotations.ParameterType;

/**
 * Contains info about a command parameter
 *
 * @author pparrado
 */
public class CommandParameter {

  private String name;

  private String description;

  private int position;

  private ParameterType paramType;

  public CommandParameter(String name, String description, int position, ParameterType paramType) {

    this.name = name;
    this.description = description;
    this.position = position;
    this.paramType = paramType;
  }

  /**
   * @return name
   */
  public String getName() {

    return this.name;
  }

  /**
   * @return description
   */
  public String getDescription() {

    return this.description;
  }

  /**
   * @return whether parameter is mandatory or optional and, if the latter, whether the value comes from a config or not
   */
  public ParameterType getParameterType() {

    return this.paramType;
  }

  /**
   * @return position
   */
  public int getPosition() {

    return this.position;
  }
}
