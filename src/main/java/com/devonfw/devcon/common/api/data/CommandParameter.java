package com.devonfw.devcon.common.api.data;

/**
 * Contains info about a command parameter
 *
 * @author pparrado
 */
public class CommandParameter {

  private String name;

  private String description;

  private boolean isOptional;

  public CommandParameter(String name, String description, boolean isOptional) {

    this.name = name;
    this.description = description;
    this.isOptional = isOptional;
  }

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
   * @return isOptional
   */
  public boolean isOptional() {

    return this.isOptional;
  }

  /**
   * @param isOptional new value of {@link #getisOptional}.
   */
  public void setOptional(boolean isOptional) {

    this.isOptional = isOptional;
  }

}
