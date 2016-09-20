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

  /**
   * Proxy configuration associated to the command
   *
   * @return if there are Proxy parameters associated to the command execution
   */
  boolean proxyParams() default false;

  /**
   * To sort commands using sort attribute. If sort >=0, it will be sorted by descending value. Commands which do not
   * have any value for sort attribute or which have value <1 will be omitted from numeric sort and will be sorted
   * alphabetically. This commands will be appended to the commands which are sorted numerically.-DevconGUI
   */
  int sort() default -1;

}
