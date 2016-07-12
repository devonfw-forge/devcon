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

}
