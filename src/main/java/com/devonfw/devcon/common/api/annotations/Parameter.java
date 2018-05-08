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
 * This annotation declares a parameter of a {@link Command}
 *
 * @author pparrado
 */
public @interface Parameter {
  /**
   * Name of the parameter
   *
   * @return the name
   */
  String name() default "";

  /**
   * Description of the parameter
   *
   * @return the description
   */
  String description() default "";

  /**
   * Is Optional
   *
   * @return whether is optional
   */
  boolean optional() default false;

  /**
   * To sort parameters using sort attributes. If sort >=0, it will be sorted by descending value. Parameters which do
   * not have any value for sort attribute or which have value <1 will be omitted from numeric sort and will be sorted
   * alphabetically. This parameters will be appended to the parameters which are sorted numerically.-DevconGUI
   */
  int sort() default -1;

  /**
   * Type of input control for GUI / TUI interface (NOT the console interface)
   */
  InputType inputType() default @InputType(name = InputTypeNames.GENERIC);
}
