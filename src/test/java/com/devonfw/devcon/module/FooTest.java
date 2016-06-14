package com.devonfw.devcon.module;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.devonfw.devcon.common.CommandManager;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.impl.CommandRegistryImpl;
import com.devonfw.devcon.input.ConsoleInput;
import com.devonfw.devcon.input.ConsoleInputManager;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.output.ConsoleOutput;
import com.devonfw.devcon.output.Output;

/**
 * Class for prototype tests
 *
 * @author pparrado
 */
public class FooTest {

  private ConsoleInputManager inputMgr;

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
    this.commandManager = new CommandManager(this.registry, this.input, this.output);
    this.inputMgr = new ConsoleInputManager(this.commandManager);
  }

  /**
   * Checks if a simple command is launched successfully
   */
  @Test
  public void simpleCommand() {

    String[] args = { "-np", "foo", "farewell" };
    assertTrue(this.inputMgr.parse(args));
  }

  /**
   * Checks if simple command fails
   */
  @Test
  public void simpleCommandFail() {

    String[] args = { "-np", "foo", "fakeCommand" };
    assertFalse(this.inputMgr.parse(args));
  }

  /**
   * Checks if a command with one parameter is launched successfully
   */
  @Test
  public void commandWithOneParameter() {

    String[] args = { "-np", "foo", "customFarewell", "-name", "Jason" };
    assertTrue(this.inputMgr.parse(args));
  }

  /**
   * Checks if a command with one parameter fails
   */
  @Test
  public void commandWithOneParameterFail() {

    String[] args = { "-np", "foo", "customFarewell" };
    assertFalse(this.inputMgr.parse(args));
  }

  /**
   * Checks if a command with wrong parameter fails
   */
  @Test
  public void commandWithWrongParameterFail() {

    String[] args = { "-np", "foo", "customFarewell", "-surname", "Jason" };

    assertFalse(this.inputMgr.parse(args));
  }

  /**
   * Checks if a command with several parameters is launched successfully
   */
  @Test
  public void commandWithSeveralParams() {

    String[] args = { "-np", "foo", "largeCustomFarewell", "-name", "Jason", "-surname", "Lytle" };
    assertTrue(this.inputMgr.parse(args));
  }

  /**
   * Checks if a command with less parameters than needed fails
   */
  @Test
  public void commandWithSeveralParamsFail() {

    String[] args = { "-np", "foo", "largeCustomFarewell", "-name", "Jason" };
    assertFalse(this.inputMgr.parse(args));
  }

  /**
   * Checks if a command with optional parameter works. It reads the optional parameter 'signature' from a json temp
   * file
   *
   * @throws IOException
   */
  @Test
  public void commandWithOptionalParameter() throws IOException {

    Path tmp = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
    String content =
        "{\"version\": \"2.0.0\",\n\"type\":\"oasp4j\",\n\"optionalParameters\": {\"signature\":\"from json\"}\n}";
    File tempSettings = tmp.resolve("devon.json").toFile();
    FileUtils.writeStringToFile(tempSettings, content, "UTF-8");

    String[] args = { "-np", "foo", "saySomething", "-message", "This is a message" };

    assertTrue(this.inputMgr.parse(args));
  }

  @After
  public void end() throws IOException {

    File tempSettings = new File(System.getProperty("user.dir") + File.separator + "devon.json");
    if (tempSettings.exists())
      FileUtils.forceDeleteOnExit(tempSettings);
  }

  /**
   * Checks if getting a unknown module fails
   */
  @Test
  public void wrongModule() {

    String[] args = { "-np", "wrongModule", "command" };

    assertFalse(this.inputMgr.parse(args));
  }

  /**
   * Checks if the help info of a module is launched successfully
   */
  @Test
  public void moduleHelp() {

    String[] args = { "foo", "-help" };

    assertTrue(this.inputMgr.parse(args));
  }

  /**
   * Checks if the help info of a command is launched successfully
   */
  @Test
  public void commandHelp() {

    String[] args = { "foo", "farewell", "-h" };
    assertTrue(this.inputMgr.parse(args));
  }

  /**
   * Checks if the help info of a command is launched successfully
   */
  @Test
  public void commandWithParametersHelp() {

    String[] args = { "foo", "largeCustomFarewell", "-h" };

    assertTrue(this.inputMgr.parse(args));
  }

  /**
   * Checks if the help info of a wrong command fails
   */
  @Test
  public void commandHelpFail() {

    String[] args = { "foo", "fakeCommand", "-help" };

    assertFalse(this.inputMgr.parse(args));
  }

}
