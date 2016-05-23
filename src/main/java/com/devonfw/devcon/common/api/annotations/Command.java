package com.devonfw.devcon.common.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for Devcon commands
 *
 * @author pparrado
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
  /**
   * Help info associated to the command
   *
   * @return help info
   */
  String help() default "";

  /**
   * Parameter for the command
   * 
   * @return parameter
   */
  String parameter() default "";
}
