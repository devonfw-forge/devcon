package com.devonfw.devcon.common.api.annotations;

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
}
