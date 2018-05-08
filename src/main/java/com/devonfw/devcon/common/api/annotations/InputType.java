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
package com.devonfw.devcon.common.api.annotations;

import com.devonfw.devcon.common.api.data.InputTypeNames;

/**
 * This annotation declares a InputType of a {@link Parameter}
 *
 * @author ivanderk
 */
public @interface InputType {
  /**
   *
   * See https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.6.1
   *
   * Name of the type (can be a UI control or other input related aspect) Possible values: - generic - Plain text field
   * (used as default-value) - path - Text field with “file selector” button (showing a file selector dialog box when
   * pressed) - password - Text field with password mask (“***”) so the password cannot be read from the screen -
   * pulldown - List of values (configurable through the "values" attribute)
   *
   * @return the name
   */
  InputTypeNames name() default InputTypeNames.GENERIC;

  /**
   * Default value
   *
   * @return the description
   */
  // String value() default "";

  /**
   * Default multiple values (in case relevant)
   *
   * @return whether is optional
   */
  String[] values() default {};
}
