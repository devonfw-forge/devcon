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
