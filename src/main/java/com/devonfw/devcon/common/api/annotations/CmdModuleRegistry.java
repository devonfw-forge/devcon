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
  String name() default "";

  String context() default "";

  boolean deprecated() default false;
}
