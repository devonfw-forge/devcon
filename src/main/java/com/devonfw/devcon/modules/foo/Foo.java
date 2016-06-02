package com.devonfw.devcon.modules.foo;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.impl.AbstractCommandHolder;

/**
 * Implementation of test class Foo
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "foo", description = "This is only a test module.", context = "fooContext", deprecated = false)
public class Foo extends AbstractCommandHolder {

  /**
   * The constructor.
   */
  public Foo() {

    super();
  }

  @Command(name = "greeting", help = "This command is used to say hello.")
  @SuppressWarnings("javadoc")
  public void greeting() {

    this.output.showMessage("Hello");
  }

  @Command(name = "farewell", help = "This command is used to say bye.")
  @SuppressWarnings("javadoc")
  public void farewell() {

    this.output.showMessage("Bye");
  }

  @Command(name = "customFarewell", help = "This command is used to say a custom bye")
  @Parameters(values = { @Parameter(name = "name", description = "this is the description for name parameter") })
  @SuppressWarnings("javadoc")
  public void customFarewell(String name) {

    this.output.showMessage("Bye " + name);
  }

  @Command(name = "largeCustomFarewell", help = "This command is used to say a large custom bye")
  @Parameters(values = { @Parameter(name = "name", description = "this is the name parameter"),
  @Parameter(name = "surname", description = "this is the description for the surname parameter") })
  @SuppressWarnings("javadoc")
  public void largeCustomFarewell(String name, String surname) {

    this.output.showMessage("Bye " + name + " " + surname);
  }

}