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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.impl.CommandRegistryImpl;

/**
 * This class contain some basic tests to validate the Documentation functionality of Devon
 *
 * @author ivanderk
 */
public class HelpTest {

  CommandRegistry registry;

  @SuppressWarnings("javadoc")
  @Before
  public void setup() {

    this.registry = new CommandRegistryImpl("com.devonfw.devcon.modules.*");
  }

  @Test
  public void testNotInExecJar() {

    // When executing tests NOT in Executable Jar
    assertTrue(!Devcon.IN_EXEC_JAR);
  }

  @Test
  public void testCommandHelpText() {

    // given
    Command devonCmd = this.registry.getCommand("doc", "devon").get();
    Command installCmd = this.registry.getCommand("dist", "install").get();

    // when
    String devonCmdHelp = devonCmd.getHelpText();
    String installCmdHelp = installCmd.getHelpText();

    // then
    assertTrue(devonCmdHelp.isEmpty());
    assertTrue(installCmdHelp.contains("[INFO] installing distribution..."));

  }

  @Test
  public void testAllActiveCommandsNumber() {

    int total = 0;

    // given
    List<CommandModuleInfo> modules = this.registry.getCommandModules();

    // when
    System.out.println("Commands present:");

    for (CommandModuleInfo module : modules) {
      // Don´t include Foo etc
      if (!module.isVisible())
        continue;

      for (Command cmd : module.getCommands()) {
        System.out.println(module.getName() + " " + cmd.getName());
        total++;
      }
    }

    // then
    assertEquals(30, total);

  }

}
