package com.devonfw.devcon.common.api.data;

/**
 * Defined control type of parameter
 *
 * @author ivanderk
 */
public class ParameterInputType {

  private InputTypeNames name;

  private String[] values;

  /**
   * The constructor.
   *
   * @param name
   * @param values
   */
  public ParameterInputType(InputTypeNames name, String[] values) {
    this.name = name;
    this.values = values;
  }

  public ParameterInputType(InputTypeNames name) {
    this.name = name;
    this.values = new String[] {};
  }

  /**
   * @return name
   */
  public InputTypeNames getName() {

    return this.name;
  }

  /**
   * @return values
   */
  public String[] getValues() {

    return this.values;
  }

}
