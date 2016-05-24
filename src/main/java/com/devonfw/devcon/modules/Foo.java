package com.devonfw.devcon.modules;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.impl.AbstractCommandHolder;

/**
 * Implementation of test class Foo
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "foo", description = "This is only a test module.", context = "MyContextIsNotGlobal", deprecated = false)
public class Foo extends AbstractCommandHolder {

  /**
   * The constructor.
   */
  public Foo() {

    super();
  }

  @Command(name = "greetings", help = "This command is used to say hello.")
  @SuppressWarnings("javadoc")
  public String greeting() {

    return "Hello";
  }

  @Command(name = "farewell", help = "This command is used to say bye.")
  @SuppressWarnings("javadoc")
  public void farewell() {

    // return "Bye";
    System.out.println("Bye");
  }

  @Command(name = "customFarewell", help = "This command is used to say a custom bye", parameters = { "name" })
  @SuppressWarnings("javadoc")
  public void customFarewell(String name) {

    // return "Bye " + name;
    System.out.println("Bye " + name);
  }

  @Command(name = "largeCustomFarewell", help = "This command is used to say a large custom bye", parameters = {
  "name", "surname" })
  @SuppressWarnings("javadoc")
  public void largeCustomFarewell(String name, String surname) {

    System.out.println("Bye " + name + " " + surname);
  }

}
