package com.devonfw.devcon.modules;

import com.devonfw.devcon.common.api.annotations.Command;

/**
 * Implementation of test class Foo
 *
 * @author pparrado
 */
public class Foo {
  @Command
  @SuppressWarnings("javadoc")
  public String greeting() {

    return "Hello";
  }

  @Command
  @SuppressWarnings("javadoc")
  public String farewell() {

    return "Bye";
  }
}
