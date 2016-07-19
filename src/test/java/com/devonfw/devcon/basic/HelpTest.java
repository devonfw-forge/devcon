package com.devonfw.devcon.basic;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.api.Command;
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

}
