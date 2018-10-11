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
 * Tests the Devon4ng module
 *
 * @author pparrado
 */
public class Devon4ngTest {
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
    this.clientPath = "D:\\devconDevon4ngTestTemp";
  }

  @Test
  public void create() {

    String[] args = { "devon4ng", "create", "-clientname", this.clientName, "-clientpath", this.clientPath };

    assertTrue(this.inputMgr.parse(args));
  }

  // THIS TEST NEEDS AN 'npm install' COMMAND PREVIOUS TO THE EXECUTION IN ORDER TO RESOLVE THE JUST CREATED PROJECT
  // DEPENDENCIES.
  // @Test
  // public void run() throws IOException, InterruptedException {
  //
  // String[] args = { "devon4ng", "run", "-clientpath", this.clientPath + File.separator + this.clientName };
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
