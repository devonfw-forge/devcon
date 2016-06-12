package com.devonfw.devcon.modules.github;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.output.OutputConsole;

/**
 * This class contains command to clone oasp4j and devon repositories.
 *
 * @author ssarmoka
 */
@CmdModuleRegistry(name = "github", description = "Module to create a new workspace with all default configuration", deprecated = false)
public class Github extends AbstractCommandModule {

  OutputConsole out;

  /**
   * This command is to clone oasp4j repository.
   *
   * @param path location to download the oasp4j repository.
   * @throws Exception
   */
  @Command(name = "oasp4j", help = "This command clones oasp4j repository at given path.")
  @Parameters(values = { @Parameter(name = "path", description = "a location for the oasp4j download") })
  public void oasp4j(String path) throws Exception {

    final String REMOTE_URL = "https://github.com/oasp/oasp4j.git";

    File folder = new File(path);
    if (!folder.exists()) {
      folder.mkdirs();
    }

    this.output.status("Cloning from " + REMOTE_URL + " to " + path);
    try (Git result = Git.cloneRepository().setURI(REMOTE_URL).setDirectory(folder).call()) {

      this.output.status("Having repository: " + result.getRepository().getDirectory());
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
   * @throws Exception
   */
  @Command(name = "devoncode", help = "This command clones devon repository at given path.")
  @Parameters(values = { @Parameter(name = "path", description = "a location for the devon download"),
  @Parameter(name = "username", description = "a user with permissions to download the Devon distribution"),
  @Parameter(name = "password", description = "the password related to the user with permissions to download the Devon distribution") })
  public void devoncode(String path, String username, String password) throws Exception {

    final String REMOTE_URL = "https://github.com/devonfw/devon.git";

    File folder = new File(path);
    if (!folder.exists()) {
      folder.mkdirs();
    }

    this.output.status("Cloning from " + REMOTE_URL + " to " + path);
    try (Git result = Git.cloneRepository().setURI(REMOTE_URL).setDirectory(folder)
        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call()) {

      this.output.status("Having repository: " + result.getRepository().getDirectory());
    } catch (Exception e) {
      this.output.status("[LOG]" + e.getMessage());
      throw e;
    }

  }

}
