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

import com.devonfw.devcon.common.api.annotations.InputType;
import com.google.common.base.Optional;

/**
 * Contains info about a command parameter
 *
 * @author pparrado
 */
public class CommandParameter {

  private String name;

  private String description;

  private int position;

  private boolean optional;

  private Optional<String> value;

  private ParameterInputType inputType;

  public CommandParameter(String name, String description, int position, boolean isoptional, InputType inputType) {

    this(name, description, position, isoptional);
    this.inputType = new ParameterInputType(inputType.name(), inputType.values());
  }

  public CommandParameter(String name, String description, int position, boolean isoptional,
      ParameterInputType inputType) {

    this(name, description, position, isoptional);
    this.inputType = inputType;
  }

  public CommandParameter(String name, String description, int position, boolean isoptional) {

    this.name = name;
    this.description = description;
    this.position = position;
    this.optional = isoptional;
    this.value = Optional.absent();
  }

  public CommandParameter(CommandParameter other) {
    this(other.name, other.description, other.position, other.optional, other.inputType);
  }

  public static CommandParameter copy(CommandParameter other) {

    return new CommandParameter(other);
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
   * @return whether parameter is mandatory or optional
   */
  public boolean isOptional() {

    return this.optional;
  }

  /**
   * @return position
   */
  public int getPosition() {

    return this.position;
  }

  /**
   * @return value
   */
  public Optional<String> getValue() {

    return this.value;
  }

  /**
   * @param value new value of {@link #getvalue}.
   */
  public void setValue(String value) {

    this.value = Optional.of(value);
  }

  /**
   * @return inputType
   */
  public ParameterInputType getInputType() {

    return this.inputType;
  }
}
