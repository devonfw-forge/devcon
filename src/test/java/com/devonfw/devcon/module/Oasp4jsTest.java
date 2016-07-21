package com.devonfw.devcon.module;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.devonfw.devcon.common.api.CommandManager;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.impl.CommandManagerImpl;
import com.devonfw.devcon.common.impl.CommandRegistryImpl;
import com.devonfw.devcon.input.ConsoleInput;
import com.devonfw.devcon.input.ConsoleInputManager;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.output.ConsoleOutput;
import com.devonfw.devcon.output.Output;

/**
 * Tests the Oasp4js module
 *
 * @author pparrado
 */
public class Oasp4jsTest {
  ConsoleInputManager inputMgr;

  private CommandManager commandManager;

  private CommandRegistry registry;

  private Output output;

  private Input input;

  private String clientName;

  private String clientPath;

  @SuppressWarnings("javadoc")
  @Before
  public void setup() {

    this.registry = new CommandRegistryImpl("com.devonfw.devcon.modules.*");
    this.output = new ConsoleOutput();
    this.input = new ConsoleInput();
    this.commandManager = new CommandManagerImpl(this.registry, this.input, this.output);
    this.inputMgr = new ConsoleInputManager(this.registry, this.input, this.output, this.commandManager);
    this.clientName = "angularProjectTest";
    this.clientPath = "D:\\devconOasp4jsTestTemp";
  }

  @Test
  public void create() {

    String[] args = { "oasp4js", "create", "-clientname", this.clientName, "-clientpath", this.clientPath };

    assertTrue(this.inputMgr.parse(args));
  }

  // THIS TEST NEEDS AN 'npm install' COMMAND PREVIOUS TO THE EXECUTION IN ORDER TO RESOLVE THE JUST CREATED PROJECT
  // DEPENDENCIES.
  // @Test
  // public void run() throws IOException, InterruptedException {
  //
  // String[] args = { "oasp4js", "run", "-clientpath", this.clientPath + File.separator + this.clientName };
  // assertTrue(this.inputMgr.parse(args));
  // }

  @After
  public void end() {

    try {
      FileUtils.forceDeleteOnExit(new File(this.clientPath));
      System.out.println("Deleted " + this.clientPath + " test file.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
