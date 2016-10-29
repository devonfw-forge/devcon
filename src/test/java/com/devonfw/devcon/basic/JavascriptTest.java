package com.devonfw.devcon.basic;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
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
import com.devonfw.devcon.common.impl.JavaScriptsCmdRegistryImpl;
import com.google.common.base.Optional;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 */
public class JavascriptTest {

  // Directory where tests files are to be created, i.e. <<system temp folder/
  private Path testRoot;

  private Path testDist;

  @SuppressWarnings("javadoc")
  @Before
  public void setup() throws IOException, URISyntaxException {

    // For testing purposes,
    // create tempFiles in System Temp File
    // this.testRoot = Files.createTempDirectory("devcon");

    // OR

    // create tempFiles in fixed root on your hard drive in an accessible path, for example:
    Path tmpRoot = FileSystems.getDefault().getPath("d:/tmp");
    this.testRoot = tmpRoot.resolve("devcon");
    Files.createDirectories(this.testRoot);

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

  @SuppressWarnings("deprecation")
  @Test
  public void testJsCmdRegistry() throws ParseException, IOException {

    CommandRegistry registry = new JavaScriptsCmdRegistryImpl(this.testRoot.resolve("test-javascript/st/"));
    Optional<Command> _cmd = registry.getCommand("st", "init");
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
}
