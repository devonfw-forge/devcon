package com.devonfw.devcon.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.DistributionType;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.github.zafarkhaja.semver.Version;
import com.google.common.base.Optional;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 */
public class ContextPathInfoTest {

  private ContextPathInfo contextInfo = new ContextPathInfo();

  // Directory where tests files are to be created, i.e. <<system temp folder/
  private Path testRoot;

  private Path testDist;

  @SuppressWarnings("javadoc")
  @Before
  public void setup() throws IOException {

    // For testing purposes,
    // create tempFiles in System Temp File
    this.testRoot = Files.createTempDirectory("devcon");

    // OR

    // create tempFiles in fixed root on your hard drive in an accessible path, for example:
    // Path tmpRoot = FileSystems.getDefault().getPath("d:/tmp");
    // this.testRoot = tmpRoot.resolve("devcon");
    // Files.createDirectories(this.testRoot);

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

    // Devon project
    content =
        "{\"version\": \"2.0.0\",\n\"type\":\"combined\",\n\"projects\": [\"oasp4j/oasp4j-samples\",\"devon4sencha/ExtSample\"]\n}";
    settingsfile = workspacemain.resolve("devon.json").toFile();
    FileUtils.writeStringToFile(settingsfile, content, "UTF-8");

    // oasp4j
    Path oasp4j = workspacemain.resolve("oasp4j/oasp4j-samples");
    Files.createDirectories(oasp4j);
    content = "{\"version\": \"2.0.0\",\n\"type\":\"oasp4j\"}";
    settingsfile = oasp4j.resolve("devon.json").toFile();
    FileUtils.writeStringToFile(settingsfile, content, "UTF-8");

    // devon4sencha
    Path devon4sencha = workspacemain.resolve("devon4sencha/ExtSample");
    Files.createDirectories(devon4sencha);
    content = "{\"version\": \"2.0.0\",\n\"type\":\"devon4sencha\"}";
    settingsfile = devon4sencha.resolve("devon.json").toFile();
    FileUtils.writeStringToFile(settingsfile, content, "UTF-8");

    // this.testDist = FileSystems.getDefault().getPath("D:/devon-dist");
  }

  @SuppressWarnings("javadoc")
  @After
  public void teardown() throws IOException {

    // Clean up devcon ("test root") directory in system tmp directory
    // DO NOT CLEAN system tmp directory !!
    // FileUtils.deleteDirectory(this.testRoot.toFile());
    FileUtils.forceDeleteOnExit(this.testRoot.toFile());
  }

  @Test
  public void testDistributionInfo() {

    // given Devon Dist version 2.0.0 to be tested from sub-dir workspaces/main
    Optional<DistributionInfo> distinfo = this.contextInfo.getDistributionRoot(this.testDist);
    Version version = Version.forIntegers(2, 0, 0);

    // then
    assertNotNull(distinfo);
    assertTrue(distinfo.isPresent());
    assertEquals(0, distinfo.get().getVersion().compareTo(version));
    assertEquals(this.testDist.toUri().toString(), distinfo.get().getPath().toUri().toString());
    assertEquals(DistributionType.DevonDist, distinfo.get().getDistributionType());

    // given Devon Dist version 2.0.0 to be tested from root of distribution
    distinfo = this.contextInfo.getDistributionRoot(this.testDist);

    // then
    assertNotNull(distinfo);
    assertTrue(distinfo.isPresent());
    assertEquals(0, distinfo.get().getVersion().compareTo(version));
    assertEquals(this.testDist.toUri().toString(), distinfo.get().getPath().toUri().toString());
    assertEquals(DistributionType.DevonDist, distinfo.get().getDistributionType());

  }

  @Test
  public void testProjectInfo() {

    // given Devon4Sencha project to be tested from sub-dir workspaces/main
    Path extSample = this.testDist.resolve("workspaces/main/devon4sencha/ExtSample");

    Optional<ProjectInfo> projectInfo = this.contextInfo.getProjectRoot(extSample);
    Version version = Version.forIntegers(2, 0, 0);

    // then
    assertNotNull(projectInfo);
    assertTrue(projectInfo.isPresent());
    assertEquals(0, projectInfo.get().getVersion().compareTo(version));
    assertEquals(extSample.toUri().toString(), projectInfo.get().getPath().toUri().toString());
    assertEquals(ProjectType.DEVON4SENCHA, projectInfo.get().getProjecType());

    // given combined project to be tested from sub-dir workspaces/main
    Path combined = this.testDist.resolve("workspaces/main");

    projectInfo = this.contextInfo.getProjectRoot(combined);
    version = Version.forIntegers(2, 0, 0);

    // then
    assertNotNull(projectInfo);
    assertTrue(projectInfo.isPresent());
    assertEquals(0, projectInfo.get().getVersion().compareTo(version));
    assertEquals(combined.toUri().toString(), projectInfo.get().getPath().toUri().toString());
    assertEquals(ProjectType.COMBINED, projectInfo.get().getProjecType());
    assertEquals(2, projectInfo.get().getSubProjects().size());

    // given Devon4Sencha project to be tested from sub-dir workspaces/main
    // but needs to return FULL context, including master project
    extSample = this.testDist.resolve("workspaces/main/devon4sencha/ExtSample");

    projectInfo = this.contextInfo.getCombinedProjectRoot(extSample);
    version = Version.forIntegers(2, 0, 0);

    // then
    assertNotNull(projectInfo);
    assertTrue(projectInfo.isPresent());
    assertEquals(0, projectInfo.get().getVersion().compareTo(version));
    assertEquals(combined.toUri().toString(), projectInfo.get().getPath().toUri().toString());
    assertEquals(ProjectType.COMBINED, projectInfo.get().getProjecType());
    assertEquals(2, projectInfo.get().getSubProjects().size());

  }

}
