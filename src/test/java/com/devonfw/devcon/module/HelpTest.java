package com.devonfw.devcon.module;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.devonfw.devcon.input.InputConsole;

/**
 * Tests the Help module
 *
 * @author pparrado
 */
public class HelpTest {

  InputConsole input;

  /**
   * Checks if the guide command works successfully
   */
  @Test
  public void guide() {

    String[] args = { "-np", "help", "guide" };
    this.input = new InputConsole(args);
    assertTrue(this.input.parse());
  }
}
