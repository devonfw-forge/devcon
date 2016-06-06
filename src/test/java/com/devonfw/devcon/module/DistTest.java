package com.devonfw.devcon.module;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.devonfw.devcon.input.InputConsole;

/**
 * Tests the Dist module
 *
 * @author pparrado
 */
public class DistTest {
  InputConsole input;

  // /**
  // * THIS TEST NEEDS A VALID TEAM FORGE USER AND PASSWORD TO WORK PROPERLY Checks if the install command works
  // * successfully
  // */
  // @Test
  // public void install() {
  //
  // String[] args =
  // { "-np", "dist", "install", "-path", "C:\\Temp\\myTemp", "-user", "yourUser", "-password", "yourPassword",
  // "-type", "oasp-ide" };
  // this.input = new InputConsole(args);
  // assertTrue(this.input.parse());
  // }

  /**
   * Checks if the install command fails if a wrong parameter is passed
   */
  @Test
  public void installFail_WrongUser() {

    String[] args =
        { "-np", "dist", "install", "-path", "C:\\Temp\\myTemp", "-user", "asdf", "-password", "12345", "-type",
        "oasp-ide" };
    this.input = new InputConsole(args);
    assertFalse(this.input.parse());
  }

  /**
   * Checks if the install command fails if a wrong parameter is passed
   */
  @Test
  public void installFail_WrongType() {

    String[] args =
        { "-np", "dist", "install", "-path", "C:\\Temp\\myTemp", "-user", "asdf", "-password", "12345", "-type",
        "wrongType" };
    this.input = new InputConsole(args);
    assertFalse(this.input.parse());
  }
}
