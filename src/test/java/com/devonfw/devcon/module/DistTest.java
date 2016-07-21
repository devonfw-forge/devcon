package com.devonfw.devcon.module;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.devonfw.devcon.common.api.CommandManager;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.impl.CommandManagerImpl;
import com.devonfw.devcon.common.impl.CommandRegistryImpl;
import com.devonfw.devcon.input.ConsoleInput;
import com.devonfw.devcon.input.ConsoleInputManager;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.output.ConsoleOutput;
import com.devonfw.devcon.output.Output;

/**
 * Tests the Dist module
 *
 * @author pparrado
 */
public class DistTest {
  ConsoleInputManager inputMgr;

  private CommandManager commandManager;

  private CommandRegistry registry;

  private Output output;

  private Input input;

  @SuppressWarnings("javadoc")
  @Before
  public void setup() {

    this.registry = new CommandRegistryImpl("com.devonfw.devcon.modules.*");
    this.output = new ConsoleOutput();
    this.input = new ConsoleInput();
    this.commandManager = new CommandManagerImpl(this.registry, this.input, this.output);
    this.inputMgr = new ConsoleInputManager(this.registry, this.input, this.output, this.commandManager);
  }

  /**
   * Checks if the install command fails if a wrong parameter is passed
   */
  @Test
  public void installFail_WrongUser() {

    String[] args = { "-np", "dist", "install", "-path", "C:\\Temp\\myTemp", "-user", "asdf", "-password", "12345",
    "-type", "oaspide" };

    assertFalse(this.inputMgr.parse(args));
  }

  /**
   * Checks if the install command fails if a wrong parameter is passed
   */
  @Test
  public void installFail_WrongType() {

    String[] args = { "-np", "dist", "install", "-path", "C:\\Temp\\myTemp", "-user", "asdf", "-password", "12345",
    "-type", "wrongType" };

    assertFalse(this.inputMgr.parse(args));
  }

}
