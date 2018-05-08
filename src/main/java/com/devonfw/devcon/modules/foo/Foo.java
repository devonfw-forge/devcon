/*******************************************************************************
 * Copyright 2015-2018 Capgemini SE.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package com.devonfw.devcon.modules.foo;

import java.lang.reflect.InvocationTargetException;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.google.common.base.Optional;

/**
 * Implementation of test class Foo, Hidden from console with Annotation parameter hidden=true
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

  @Command(name = "greeting", description = "This command is used to say hello.")
  @SuppressWarnings("javadoc")
  public void greetingMethod() {

    this.output.showMessage("Hello");
  }

  @Command(name = "farewell", description = "This command is used to say bye.")
  @SuppressWarnings("javadoc")
  public void farewell() {

    this.output.showMessage("Bye");
  }

  @Command(name = "customFarewell", description = "This command is used to say a custom farewell")
  @Parameters(values = { @Parameter(name = "name", description = "this is the description for name parameter") })
  @SuppressWarnings("javadoc")
  public void customFarewell(String name) {

    this.output.showMessage("Bye " + name);
  }

  @Command(name = "largeCustomFarewell", description = "This command is used to say a large custom bye")
  @Parameters(values = { @Parameter(name = "name", description = "this is the name parameter"),
  @Parameter(name = "surname", description = "this is the description for the surname parameter") })
  @SuppressWarnings("javadoc")
  public void largeCustomFarewell(String name, String surname) {

    this.output.showMessage("Bye " + name + " " + surname);
  }

  @Command(name = "saySomething", description = "This command is for say something", context = ContextType.PROJECT)
  @Parameters(values = { @Parameter(name = "message", description = "the message to be written"),
  @Parameter(name = "signature", description = "the signature", optional = true) })
  @SuppressWarnings("javadoc")
  public void saySomething(String message, String signature) {

    this.output.showMessage(message + "\n" + signature + "\n" + this.projectInfo.get().getPath().toString());
  }

  @Command(name = "multipleWords", description = "This command is to say multiple words", context = ContextType.PROJECT)
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

  @Command(name = "multipleWordsNoContext", description = "This command is to say multiple words (without context)")
  @Parameters(values = { @Parameter(name = "first", description = "the first word", optional = true),
  @Parameter(name = "second", description = "the second word", optional = true),
  @Parameter(name = "third", description = "the third word", optional = true),
  @Parameter(name = "FOURTH", description = "the fourth word", optional = true) })
  @SuppressWarnings("javadoc")
  public String multipleWordsNoCtx(String first, String second, String third, String fourth) {

    return first + second + third + fourth;

  }

  @Command(name = "delegateCommand", description = "This command is to delegate to another")
  @Parameters(values = { @Parameter(name = "first", description = "the first word", optional = true),
  @Parameter(name = "second", description = "the second word", optional = true),
  @Parameter(name = "third", description = "the third word", optional = true),
  @Parameter(name = "FOURTH", description = "the fourth word", optional = true) })
  @SuppressWarnings("javadoc")
  public String delegateCommand(String first, String second, String third, String fourth)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    Optional<com.devonfw.devcon.common.api.Command> cmd = getCommand("foo", "multipleWordsNoContext");
    return (String) cmd.get().exec(first, "Big", third, fourth);

  }

  @Command(name = "generateError", description = "This command generated an error")
  @SuppressWarnings("javadoc")
  public String generateError() throws Exception {

    // throw new Exception("BOOM!!!");
    throw new Error("BOOM!!!");

  }

}
