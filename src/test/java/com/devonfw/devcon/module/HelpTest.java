package com.devonfw.devcon.module;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.devonfw.devcon.common.CommandManager;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.impl.CommandRegistryImpl;
import com.devonfw.devcon.input.ConsoleInput;
import com.devonfw.devcon.output.Output;
import com.devonfw.devcon.output.ConsoleOutput;

/**
 * Tests the Help module
 *
 * @author pparrado
 */
public class HelpTest {

  ConsoleInput input;

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
   * Checks if the guide command works successfully
   */
  @Test
  public void guide() {

    String[] args = { "-np", "help", "guide" };
    assertTrue(this.input.parse(args));
  }
}
