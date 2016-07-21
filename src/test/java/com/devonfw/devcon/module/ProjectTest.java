package com.devonfw.devcon.module;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
 * Tests the Project module
 *
 * @author pparrado
 */
public class ProjectTest {
  ConsoleInputManager inputMgr;

  private CommandManager commandManager;

  private CommandRegistry registry;

  private Output output;

  private Input input;

  private Path testRoot;

  private Path testDist;

  private String serverName;

  private String serverPath;

  private String packageName;

  private String groupId;

  private String version;

  private String clientType;

  private String clientName;

  private String clientPath;

  @SuppressWarnings("javadoc")
  @Before
  public void setup() throws IOException {

    // Creating a "mock" distribution to test the module
    this.testRoot = Files.createTempDirectory("devconProjectTestTemp");
    this.testDist = this.testRoot.resolve("test-devon-dist");
    Files.createDirectories(this.testDist);

    // Directory conf
    Path conf = this.testDist.resolve("conf");
    Files.createDirectories(conf);

    // settings.json
    String content = "{\n\"version\": \"2.0.0\",\n\"type\": \"devon-dist\"\n}";
    File settingsfile = conf.resolve("settings.json").toFile();
    FileUtils.writeStringToFile(settingsfile, content, "UTF-8");

    // Directory to be in working test
    Path workspacemain = this.testDist.resolve("workspaces/main");
    Files.createDirectories(workspacemain);

    this.registry = new CommandRegistryImpl("com.devonfw.devcon.modules.*");
    this.output = new ConsoleOutput();
    this.input = new ConsoleInput();
    this.commandManager = new CommandManagerImpl(this.registry, this.input, this.output);
    this.inputMgr = new ConsoleInputManager(this.registry, this.input, this.output, this.commandManager);
    this.serverName = "serverProjectTest";
    this.serverPath = workspacemain.toString();
    this.groupId = "io.devon.application";
    this.packageName = this.groupId + "." + this.serverName;
    this.version = "0.1-SNAPSHOT";
  }

  @After
  public void end() throws IOException {

    FileUtils.forceDeleteOnExit(this.testRoot.toFile());
  }

  @Test
  public void createServerWithAngular() {

    this.clientName = "oasp4jsTest";
    this.clientPath = this.serverPath;
    this.clientType = "oasp4js";

    String[] args = { "project", "create", "-servername", this.serverName, "-distributionpath", this.serverPath,
    "-packagename", this.packageName, "-groupid", this.groupId, "-version", this.version, "-clientname",
    this.clientName, "-clientpath", this.clientPath, "-clienttype", this.clientType };

    assertTrue(this.inputMgr.parse(args));
  }

}
