package com.devonfw.devcon.module;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.devonfw.devcon.input.InputConsole;

/**
 * Tests the Dist module
 *
 * @author pparrado
 */
public class DistTest {
  InputConsole input;

  /**
   * Checks if the install command works successfully
   */
  @Test
  public void install() {

    String[] args =
        { "-np", "dist", "install", "-path", "C:\\Temp\\myTemp", "-user", "pparrado", "-password", "00120Tfsrcm8344",
        "-type", "oasp-ide" };
    this.input = new InputConsole(args);
    assertTrue(this.input.parse());
  }
}
