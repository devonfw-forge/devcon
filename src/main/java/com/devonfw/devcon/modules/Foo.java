package com.devonfw.devcon.modules;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.impl.AbstractCommandHolder;

/**
 * Implementation of test class Foo
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "MyNameIsModuleFoo", context = "MyContextIsNotGlobal", deprecated = false)
public class Foo extends AbstractCommandHolder {

  /**
   * The constructor.
   */
  public Foo() {

    super();
  }

  @Command(help = "This command is used to say hello.")
  @SuppressWarnings("javadoc")
  public String greeting() {

    return "Hello";
  }

  @Command(help = "This command is used to say bye.")
  @SuppressWarnings("javadoc")
  public String farewell() {

    return "Bye";
  }
}
