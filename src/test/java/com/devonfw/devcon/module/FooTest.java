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
package com.devonfw.devcon.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.devonfw.devcon.common.CommandResult;
import com.devonfw.devcon.common.api.CommandManager;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.impl.CommandManagerImpl;
import com.devonfw.devcon.common.impl.CommandRegistryImpl;
import com.devonfw.devcon.common.utils.Utils;
import com.devonfw.devcon.input.ConsoleInput;
import com.devonfw.devcon.input.ConsoleInputManager;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.mocks.MockCommandManager;
import com.devonfw.devcon.output.ConsoleOutput;
import com.devonfw.devcon.output.Output;

/**
 * Class for prototype tests
 *
 * @author pparrado
 */
public class FooTest {

  // Directory where tests files are to be created, i.e. <<system temp folder/
  private Path testRoot;

  private Path testFoo;

  private ConsoleInputManager inputMgr;

  private CommandManager commandManager;

  private CommandRegistry registry;

  private Output output;

  private Input input;

  @SuppressWarnings("javadoc")
  @Before
  public void setup() throws IOException {

    this.registry = new CommandRegistryImpl("com.devonfw.devcon.modules.*");
    this.output = new ConsoleOutput();
    this.input = new ConsoleInput();
    this.commandManager = new CommandManagerImpl(this.registry, this.input, this.output);
    this.inputMgr = new ConsoleInputManager(this.registry, this.input, this.output, this.commandManager);

    // For testing purposes,
    // create tempFiles in System Temp File
    this.testRoot = Files.createTempDirectory("devcon");

    // OR

    // create tempFiles in fixed root on your hard drive in an accessible path, for example:
    // Path tmpRoot = FileSystems.getDefault().getPath("d:/tmp");
    // this.testRoot = tmpRoot.resolve("devcon");
    // Files.createDirectories(this.testRoot);

    this.testFoo = this.testRoot.resolve("test-devcon-foo");
    Files.createDirectories(this.testFoo);

  }

  /**
   * Checks if a simple command is launched successfully
   */
  @Test
  public void simpleCommand() {

    String[] args = { "foo", "farewell" };
    assertTrue(this.inputMgr.parse(args));
  }

  /**
   * Checks if simple command fails
   */
  @Test
  public void simpleCommandFail() {

    String[] args = { "foo", "fakeCommand" };
    assertFalse(this.inputMgr.parse(args));
  }

  /**
   * Checks if a command with one parameter is launched successfully
   */
  @Test
  public void commandWithOneParameter() {

    String[] args = { "foo", "customFarewell", "-name", "Jason" };
    assertTrue(this.inputMgr.parse(args));
  }

  /**
   * Checks if a command with one parameter fails
   */
  @Test
  public void commandWithOneParameterFail() {

    String[] args = { "foo", "customFarewell" };
    assertFalse(this.inputMgr.parse(args));
  }

  /**
   * Checks if a command with wrong parameter fails
   */
  @Test
  public void commandWithWrongParameterFail() {

    String[] args = { "foo", "customFarewell", "-surname", "Jason" };

    assertFalse(this.inputMgr.parse(args));
  }

  /**
   * Checks if a command with several parameters is launched successfully
   */
  @Test
  public void commandWithSeveralParams() {

    String[] args = { "foo", "largeCustomFarewell", "-name", "Jason", "-surname", "Lytle" };
    assertTrue(this.inputMgr.parse(args));
  }

  /**
   * Checks if a command with less parameters than needed fails
   */
  @Test
  public void commandWithSeveralParamsFail() {

    String[] args = { "foo", "largeCustomFarewell", "-name", "Jason" };
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
    String content = "{\"version\": \"2.0.0\",\n\"type\":\"devon4j\",\n\"signature\":\"from json\"}";
    File tempSettings = tmp.resolve("devon.json").toFile();
    FileUtils.writeStringToFile(tempSettings, content, "UTF-8");

    String[] args = { "foo", "saySomething", "-message", "This is a message" };

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

  @Test
  public void testInputManager() {

    // given
    MockCommandManager cm = new MockCommandManager(this.registry, this.output);
    ConsoleInputManager inputMgr_ = new ConsoleInputManager(this.registry, this.input, this.output, cm);

    // when
    String[] args = { "foo", "multipleWordsNoContext", "-first", "The", "-third", "Brown", "-fourth", "Fox" };

    boolean ignore = inputMgr_.parse(args);
    Sentence sentence = cm.getSentence();
    Map<String, String> params = Utils.pairsToMap(sentence.getParams());

    // then
    assertTrue(params.containsKey("first"));
    assertTrue(params.containsKey("third"));
    assertTrue(params.containsKey("fourth"));

    assertEquals("The", params.get("first"));
    assertEquals("Brown", params.get("third"));
    assertEquals("Fox", params.get("fourth"));

    // when
    String[] args2 = { "foo", "multipleWordsNoContext", "-help" };
    ignore = inputMgr_.parse(args2);
    sentence = cm.getSentence();

    // then
    assertTrue(sentence.isHelpRequested());
  }

  @Test
  public void testExecParametersFromConfig() throws Exception {

    Sentence sentence = new Sentence();
    sentence.setModuleName("foo");
    sentence.setCommandName("multipleWords");

    sentence.addParam("second", "Brown");

    // Devon project containing missing values
    String content = "{\"version\": \"2.0.0\",\n\"type\":\"devon4j\",\n\"first\": \"The\",\n\"fourth\": \"Fox\"\n}";
    File settingsfile = this.testFoo.resolve("devon.json").toFile();
    FileUtils.writeStringToFile(settingsfile, content, "UTF-8");

    sentence.addParam("path", this.testFoo.toString());
    Pair<CommandResult, Object> result = this.commandManager.execCmdLine(sentence);

    assertEquals("TheBrownFox", result.getRight());
  }

  @Test
  public void testExecParameterOrderChanged() throws Exception {

    // given
    Sentence sentence = new Sentence();
    sentence.setModuleName("foo");
    sentence.setCommandName("multipleWordsNoContext");

    sentence.addParam("fourth", "Fox");
    sentence.addParam("SECOND", "Brown");

    // when
    Pair<CommandResult, Object> result = this.commandManager.execCmdLine(sentence);

    // then
    assertEquals("BrownFox", result.getRight());
  }

  @Test
  public void testExecReturns() throws Exception {

    // given
    Sentence sentence = new Sentence();

    // when
    sentence.setModuleName("fooP");
    sentence.setCommandName("multipleWordsNoContext");
    Pair<CommandResult, Object> result = this.commandManager.execCmdLine(sentence);

    // then
    assertEquals(CommandResult.UNKNOWN_MODULE, result.getLeft());

    // when
    sentence = new Sentence();
    sentence.setModuleName("foo");
    sentence.setCommandName("multipleWordsNoContext_NOTEXIST");
    result = this.commandManager.execCmdLine(sentence);

    // then
    assertEquals(CommandResult.UNKNOWN_COMMAND, result.getLeft());

    // when
    sentence = new Sentence();
    sentence.setModuleName("foo");
    sentence.setCommandName("multipleWordsNoContext");
    sentence.setHelpRequested(true);
    result = this.commandManager.execCmdLine(sentence);

    // then
    assertEquals(CommandResult.HELP_SHOWN, result.getLeft());

    // when
    sentence = new Sentence();
    sentence.setModuleName("foo");
    sentence.setCommandName("multipleWordsNoContext");
    sentence.addParam("MEH", "NOH");
    result = this.commandManager.execCmdLine(sentence);

    // then NOT CORRECT
    assertEquals(CommandResult.UNKNOWN_PARAMS, result.getLeft());

    // when
    sentence = new Sentence();
    sentence.setModuleName("foo");
    sentence.setCommandName("customFarewell");
    result = this.commandManager.execCmdLine(sentence);

    // then NOT CORRECT
    assertEquals(CommandResult.MANDATORY_PARAMS_MISSING, result.getLeft());

  }

  @Test
  public void testDelegateCommand() throws Exception {

    // given
    Sentence sentence = new Sentence();
    sentence.setModuleName("foo");
    sentence.setCommandName("delegateCommand");

    sentence.addParam("first", "The");
    sentence.addParam("fourth", "Fox");
    sentence.addParam("third", "Brown");
    sentence.addParam("SECOND", "Hello");

    // when
    Pair<CommandResult, Object> result = this.commandManager.execCmdLine(sentence);

    // then
    assertEquals("TheBigBrownFox", result.getRight());
  }

}
