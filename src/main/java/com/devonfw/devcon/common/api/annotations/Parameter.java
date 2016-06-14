package com.devonfw.devcon.common.api.annotations;

/**
 * TODO pparrado This type ...
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
   * @return The parameter type
   */
  ParameterType parametertype() default ParameterType.Mandatory;
  // boolean optional() default false;
}
