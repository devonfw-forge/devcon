package com.devonfw.devcon.common.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.devonfw.devcon.common.api.data.ContextType;

/**
 * Annotation for Devcon commands
 *
 * @author pparrado
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
  /**
   * Name of the command
   *
   * @return name
   */
  String name() default "";

  /**
   * Help info associated to the command
   *
   * @return help info
   */
  String description() default "";

  /**
   * Help info associated to the command
   *
   * @return help info
   */
  ContextType context() default ContextType.NONE;

}
