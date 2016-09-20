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
  String value() default "";

  /**
   * Default multiple values (in case relevant)
   *
   * @return whether is optional
   */
  String[] values() default {};
}
