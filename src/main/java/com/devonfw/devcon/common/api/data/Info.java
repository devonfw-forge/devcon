package com.devonfw.devcon.common.api.data;

/**
 * Class to encapsulate the info related to an element (module, command, etc.)
 *
 * @author pparrado
 */
public class Info {

  /**
   * the name for the element
   */
  private String name;

  /**
   * the description of the element
   */
  private String description;

  /**
   * determines whether component is visible on the console
   */
  private boolean visible = true;

  /**
   * @return visible
   */
  public boolean isVisible() {

    return this.visible;
  }

  /**
   * @param visible new value of {@link #getvisible}.
   */
  public void setVisible(boolean visible) {

    this.visible = visible;
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

}
