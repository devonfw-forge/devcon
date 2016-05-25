package com.devonfw.devcon.module.foo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.devonfw.devcon.input.InputConsole;

/**
 * Class for prototype tests
 *
 * @author pparrado
 */
public class FooTest {
  InputConsole input;

  @Test
  public void simpleCommand() {

    String[] args = { "-np", "foo", "farewell" };
    this.input = new InputConsole(args);
    assertTrue(this.input.parse());
  }

  @Test
  public void simpleCommandFail() {

    String[] args = { "-np", "fakeModule", "fakeCommand" };
    this.input = new InputConsole(args);
    assertFalse(this.input.parse());
  }

  @Test
  public void commandWithOneParameter() {

    String[] args = { "-np", "foo", "customFarewell", "-name", "Jason" };
    this.input = new InputConsole(args);
    assertTrue(this.input.parse());
  }

  @Test
  public void commandWithOneParameterFail() {

    String[] args = { "-np", "foo", "customFarewell" };
    this.input = new InputConsole(args);
    assertFalse(this.input.parse());
  }

  @Test
  public void commandWithSeveralParams() {

    String[] args = { "-np", "foo", "largeCustomFarewell", "-name", "Jason", "-surname", "Lytle" };
    this.input = new InputConsole(args);
    assertTrue(this.input.parse());
  }

  @Test
  public void commandWithSeveralParamsFail() {

    String[] args = { "-np", "foo", "largeCustomFarewell", "-name", "Jason" };
    this.input = new InputConsole(args);
    assertFalse(this.input.parse());
  }

  @Test
  public void moduleHelp() {

    String[] args = { "foo", "-help" };
    this.input = new InputConsole(args);
    assertTrue(this.input.parse());
  }

  @Test
  public void commandHelp() {

    String[] args = { "foo", "customFarewell", "-help" };
    this.input = new InputConsole(args);
    assertTrue(this.input.parse());
  }

}
