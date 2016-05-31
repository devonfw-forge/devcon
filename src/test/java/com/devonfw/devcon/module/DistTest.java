package com.devonfw.devcon.module;

import static org.junit.Assert.assertFalse;
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
   * Checks if the install command works successfully You need to define a user and password in order to check this test
   */
  @Test
  public void install() {

    String[] args =
        { "-np", "dist", "install", "-path", "C:\\Temp\\myTemp", "-user", "[typeYourUser]", "-password",
        "[typeYourPassword", "-type", "oasp-ide" };
    this.input = new InputConsole(args);
    assertTrue(this.input.parse());
  }

  /**
   * Checks if the install command fails if a wrong parameter is passed
   */
  @Test
  public void installFail() {

    String[] args =
        { "-np", "dist", "install", "-path", "C:\\Temp\\myTemp", "-user", "asdf", "-password", "12345", "-type",
        "wrongType" };
    this.input = new InputConsole(args);
    assertFalse(this.input.parse());
  }
}
