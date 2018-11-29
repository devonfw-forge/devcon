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

import static org.junit.Assert.assertFalse;

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
 * Tests the Dist module
 *
 * @author pparrado
 */
public class DistTest {
  ConsoleInputManager inputMgr;

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
    this.commandManager = new CommandManagerImpl(this.registry, this.input, this.output);
    this.inputMgr = new ConsoleInputManager(this.registry, this.input, this.output, this.commandManager);
  }

  /**
   * Checks if the install command fails if a wrong parameter is passed
   */
  @Test
  public void installFail_WrongUser() {

    String[] args = { "-np", "dist", "install", "-path", "C:\\Temp\\myTemp", "-user", "asdf", "-password", "12345",
    "-type", "devonide" };

    assertFalse(this.inputMgr.parse(args));
  }

  /**
   * Checks if the install command fails if a wrong parameter is passed
   */
  @Test
  public void installFail_WrongType() {

    String[] args = { "-np", "dist", "install", "-path", "C:\\Temp\\myTemp", "-user", "asdf", "-password", "12345",
    "-type", "wrongType" };

    assertFalse(this.inputMgr.parse(args));
  }

}
