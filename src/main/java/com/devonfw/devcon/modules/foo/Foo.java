package com.devonfw.devcon.modules.foo;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.impl.AbstractCommandModule;

/**
 * Implementation of test class Foo Hidden from console with Annotation parameter hidden=true
 *
 * @author pparrado
 */

@CmdModuleRegistry(name = "foo", description = "This is only a test module.", visible = false)
public class Foo extends AbstractCommandModule {

  /**
   * The constructor.
   */
  public Foo() {

    super();
  }

  @Command(name = "greeting", help = "This command is used to say hello.")
  @SuppressWarnings("javadoc")
  public void greetingMethod() {

    this.output.showMessage("Hello");
  }

  @Command(name = "farewell", help = "This command is used to say bye.")
  @SuppressWarnings("javadoc")
  public void farewell() {

    this.output.showMessage("Bye");
  }

  @Command(name = "customFarewell", help = "This command is used to say a custom farewell")
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

  @Command(name = "saySomething", help = "This command is for say something", context = ContextType.PROJECT)
  @Parameters(values = { @Parameter(name = "message", description = "the message to be written"),
  @Parameter(name = "signature", description = "the signature", optional = true) })
  @SuppressWarnings("javadoc")
  public void saySomething(String message, String signature) {

    this.output.showMessage(message + "\n" + signature + "\n" + this.projectInfo.get().getPath().toString());
  }

  @Command(name = "multipleWords", help = "This command is to say multiple words", context = ContextType.PROJECT)
  @Parameters(values = { @Parameter(name = "first", description = "the first word", optional = true),
  @Parameter(name = "second", description = "the second word", optional = true),
  @Parameter(name = "third", description = "the third word", optional = true),
  @Parameter(name = "fourth", description = "the fourth word", optional = true) })
  @SuppressWarnings("javadoc")
  public String multipleWords(String first, String second, String third, String fourth) {

    if (this.projectInfo.isPresent()) {
      return first + second + third + fourth;
    } else {
      return "Project Info not Preset";
    }

  }

  @Command(name = "multipleWordsNoContext", help = "This command is to say multiple words (without context)")
  @Parameters(values = { @Parameter(name = "first", description = "the first word", optional = true),
  @Parameter(name = "second", description = "the second word", optional = true),
  @Parameter(name = "third", description = "the third word", optional = true),
  @Parameter(name = "FOURTH", description = "the fourth word", optional = true) })
  @SuppressWarnings("javadoc")
  public String multipleWordsNoCtx(String first, String second, String third, String fourth) {

    return first + second + third + fourth;

  }

}