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
package com.devonfw.devcon.basic;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.InputTypeNames;
import com.devonfw.devcon.common.api.data.ParameterInputType;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.impl.CommandRegistryImpl;
import com.devonfw.devcon.common.impl.JavaScriptsCmdRegistryImpl;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.input.ConsoleInput;
import com.devonfw.devcon.output.ConsoleOutput;
import com.google.common.base.Optional;

/**
 * TODO ivanderk This class contains the unit tests related with Javascript Commands
 *
 * @author ivanderk
 */
public class JavascriptTest {

  // Directory where tests files are to be created, i.e. <<system temp folder/
  private Path testRoot;

  private Path testDist;

  CommandRegistry registry;

  CommandRegistry jsregistry;

  @SuppressWarnings("javadoc")
  @Before
  public void setup() throws IOException, URISyntaxException, ParseException {

    // For testing purposes,
    // create tempFiles in System Temp File
    this.testRoot = Files.createTempDirectory("devcon");

    // OR

    // create tempFiles in fixed root on your hard drive in an accessible path, for example:
    // Path tmpRoot = FileSystems.getDefault().getPath("d:/tmp");
    // this.testRoot = tmpRoot.resolve("devcon");
    // Files.createDirectories(this.testRoot);

    this.testDist = this.testRoot.resolve("test-javascript");
    Files.createDirectories(this.testDist);

    // Directory initModule
    Path initModule = this.testDist.resolve("st");
    Files.createDirectories(initModule);

    // commands.json
    String root = (Devcon.IN_EXEC_JAR) ? "resources/" : "";
    URL commandsJsonUrl = ClassLoader.getSystemClassLoader().getResource(root + "commands.json");

    String CommandsJsonTxt = FileUtils.readFileToString(new File(commandsJsonUrl.toURI()), "UTF-8");

    File commandsJson = initModule.resolve("commands.json").toFile();
    FileUtils.writeStringToFile(commandsJson, CommandsJsonTxt, "UTF-8");

    // init.js
    URL JsUrl = ClassLoader.getSystemClassLoader().getResource(root + "init.js");
    String JsTxt = FileUtils.readFileToString(new File(JsUrl.toURI()), "UTF-8");

    File initJsFile = initModule.resolve("init.js").toFile();
    FileUtils.writeStringToFile(initJsFile, JsTxt, "UTF-8");

    this.registry = new CommandRegistryImpl("com.devonfw.devcon.modules.*");
    this.jsregistry = new JavaScriptsCmdRegistryImpl(this.testRoot.resolve("test-javascript/st/"));
  }

  @SuppressWarnings("javadoc")
  @After
  public void teardown() throws IOException {

    // Clean up devcon ("test root") directory in system tmp directory
    // DO NOT CLEAN system tmp directory !!
    // FileUtils.deleteDirectory(this.testRoot.toFile());
    // FileUtils.forceDeleteOnExit(this.testRoot.toFile());
  }

  @Test
  public void testFilesPresence() {

    Path cmdsJson = this.testRoot.resolve("test-javascript/st/commands.json");
    assertTrue("commands.json exists", cmdsJson.toFile().exists());
    Path initJs = this.testRoot.resolve("test-javascript/st/init.js");
    assertTrue("init.js exists", initJs.toFile().exists());

  }

  @Test
  public void testJsCmdRegistry() throws ParseException, IOException {

    if (!Devcon.scriptEngine.isPresent()) {
      return;
    }

    Optional<Command> _cmd = this.jsregistry.getCommand("st", "init");
    assertTrue("Has 'st init' command", _cmd.isPresent());
    Command cmd = _cmd.get();

    assertEquals("st", cmd.getModuleName());
    assertEquals("init", cmd.getName());
    assertEquals(ContextType.NONE, cmd.getContext());

    List<CommandParameter> parameters = cmd.getDefinedParameters();
    assertEquals(1, parameters.size());

    CommandParameter param = parameters.get(0);
    assertEquals("path", param.getName());
    assertTrue(param.isOptional());

    ParameterInputType inputType = param.getInputType();
    assertEquals(InputTypeNames.LIST, inputType.getName());
    assertArrayEquals(new String[] { "hamlet", "village", "town", "city", "metropolis" }, inputType.getValues());

  }

  @Test
  public void testNashorn() {

    if (!Devcon.scriptEngine.isPresent()) {
      return;
    }

    assertTrue("Running on Java 1.8 (Nashorn)", Devcon.scriptEngine.isPresent());

    this.registry.add(this.jsregistry);
    Optional<Command> _cmd = this.registry.getCommand("st", "init");
    assertTrue("Has 'st init' command", _cmd.isPresent());
  }

  @Test
  public void testExec()
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    if (!Devcon.scriptEngine.isPresent()) {
      return;
    }

    Optional<Command> _cmd = this.jsregistry.getCommand("st", "init");
    Command cmd = _cmd.get();
    Optional<ProjectInfo> none = Optional.absent();
    cmd.injectEnvironment(this.jsregistry, new ConsoleInput(), new ConsoleOutput(), new ContextPathInfo(), none);

    Object rs = cmd.exec("ARGUMENT1", "ARGUMENT2");
    assertEquals("ARGUMENT1ARGUMENT2", rs.toString());

    Object rs2 = cmd.exec();
    assertEquals(null, rs2);
  }
}
