package com.devonfw.devcon.modules.github;

import java.io.File;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.common.utils.Utils;
import com.devonfw.devcon.output.Output;

/**
 * This class contains command to clone oasp4j and devon repositories.
 *
 * @author ssarmoka
 */
@CmdModuleRegistry(name = "github", description = "Module to create a new workspace with all default configuration", deprecated = false)
public class Github extends AbstractCommandModule {

  Output out;

  /**
   * This command is to clone oasp4j repository.
   *
   * @param path location to download the oasp4j repository.
   * @param gitFolder GIT BIN/CMD folder where git executable is present
   * @throws Exception
   */
  @Command(name = "oasp4j", description = "This command clones oasp4j repository at given path.")
  @Parameters(values = { @Parameter(name = "path", description = "a location for the oasp4j download"),
  @Parameter(name = "gitFolder", description = "GIT BIN/CMD folder where git executable is present", optional = true) })
  public void oasp4j(String path, String gitFolder) throws Exception {

    final String REMOTE_URL = Constants.OASP4J_REPO_URL;
    final String CLONED_DIRECTORY = new StringBuffer(path).append(Constants.DOT_GIT).toString();

    File folder = new File(path);
    if (!folder.exists()) {
      folder.mkdirs();
    }

    this.output.status("Cloning from " + REMOTE_URL + " to " + path);
    try {
      Utils.cloneRepository(REMOTE_URL, path, gitFolder);
      this.output.status("Having repository: " + CLONED_DIRECTORY);
    } catch (Exception e) {
      this.output.status("[LOG]" + e.getMessage());
      throw e;
    }

  }

  /**
   * This command clones devon ditribution. This requires authentication as devon is private repository.
   *
   * @param path
   * @param username
   * @param password
   * @param gitDir
   * @throws Exception
   */
  @Command(name = "devoncode", description = "This command clones devon repository at given path.", context = ContextType.NONE)
  @Parameters(values = { @Parameter(name = "path", description = "a location for the devon download"),
  @Parameter(name = "username", description = "a user with permissions to download the Devon distribution"),
  @Parameter(name = "password", description = "the password related to the user with permissions to download the Devon distribution"),
  @Parameter(name = "gitFolder", description = "GIT BIN/CMD folder where git executable is present", optional = true) })
  public void devoncode(String path, String username, String password, String gitFolder) throws Exception {

    final String CLONED_DIRECTORY = new StringBuffer(path).append(Constants.DOT_GIT).toString();
    final String REMOTE_URL = new StringBuffer(Constants.HTTPS).append(username).append(Constants.COLON)
        .append(password).append(Constants.AT_THE_RATE).append(Constants.DEVON_REPO_URL).toString();

    File folder = new File(path);
    if (!folder.exists()) {
      folder.mkdirs();
    }

    this.output.status("Cloning from " + REMOTE_URL + " to " + path);

    try {
      Utils.cloneRepository(REMOTE_URL, path, gitFolder);
      this.output.status("Having repository: " + CLONED_DIRECTORY);
    } catch (Exception e) {
      this.output.status("[LOG]" + e.getMessage());
      throw e;
    }

  }
}