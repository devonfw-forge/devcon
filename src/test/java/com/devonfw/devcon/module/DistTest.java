package com.devonfw.devcon.module;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.devonfw.devcon.common.CommandManager;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.impl.CommandRegistryImpl;
import com.devonfw.devcon.input.ConsoleInput;
import com.devonfw.devcon.output.Output;
import com.devonfw.devcon.output.ConsoleOutput;

/**
 * Tests the Dist module
 *
 * @author pparrado
 */
public class DistTest {
  ConsoleInput input;

  // /**
  // * THIS TEST NEEDS A VALID TEAM FORGE USER AND PASSWORD TO WORK PROPERLY
  // * Checks if the install command works
  // * successfully
  // */
  // @Test
  // public void install() {
  //
  // String[] args =
  // { "-np", "dist", "install", "-path", "C:\\Temp\\myTemp", "-user", "yourUser", "-password", "yourPassword",
  // "-type", "oaspide" };
  // this.input = new InputConsole(args);
  // assertTrue(this.input.parse());
  // }

  private CommandManager commandManager;

  private CommandRegistry registry;

  private Output output;

  @SuppressWarnings("javadoc")
  @Before
  public void setup() {

    this.registry = new CommandRegistryImpl("com.devonfw.devcon.modules.*");
    this.output = new ConsoleOutput();
    this.commandManager = new CommandManager(this.registry, this.output);
    this.input = new ConsoleInput(this.commandManager);
  }

  /**
   * Checks if the install command fails if a wrong parameter is passed
   */
  @Test
  public void installFail_WrongUser() {

    String[] args = { "-np", "dist", "install", "-path", "C:\\Temp\\myTemp", "-user", "asdf", "-password", "12345",
    "-type", "oaspide" };

    assertFalse(this.input.parse(args));
  }

  /**
   * Checks if the install command fails if a wrong parameter is passed
   */
  @Test
  public void installFail_WrongType() {

    String[] args = { "-np", "dist", "install", "-path", "C:\\Temp\\myTemp", "-user", "asdf", "-password", "12345",
    "-type", "wrongType" };

    assertFalse(this.input.parse(args));
  }
}
