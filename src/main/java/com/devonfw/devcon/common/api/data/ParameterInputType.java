/*******************************************************************************
 * Copyright 2015-2018 Capgemini SE.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
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
