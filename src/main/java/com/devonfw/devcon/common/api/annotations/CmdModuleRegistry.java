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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for Devcon command modules
 *
 * @author pparrado
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CmdModuleRegistry {
  /**
   * The name of the module
   *
   * @return name
   */
  String name() default "";

  /**
   * Description of the module
   *
   * @return description
   */
  String description() default "";

  /**
   * Show in console or not
   *
   * @return
   */
  boolean visible() default true;

  /**
   * To sort modules using sort attributes. If sort >=0, it will be sorted by descending value. Modules which do not
   * have any value for sort attribute or which have value <1 will be omitted from numeric sort and will be sorted
   * alphabetically. This modules will be appended to the modules which are sorted numerically.-DevconGUI
   */
  int sort() default -1;

}
