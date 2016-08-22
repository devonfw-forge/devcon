package com.devonfw.devcon.modules.github;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.impl.AbstractCommandModule;

/**
 * This class contains command to clone oasp4j and devon repositories.
 *
 * @author ssarmoka
 */
@CmdModuleRegistry(name = "github", description = "Module to get Github repositories related to Devonfw.")
public class Github extends AbstractCommandModule {

  public static final String OASP4J_URL = "https://github.com/oasp/oasp4j.git";

  public static final String DEVON_URL = "https://github.com/devonfw/devon.git";

  public static final String DOT_GIT = ".git";

  /**
   * This command is to clone oasp4j repository.
   *
   * @param path location to download the oasp4j repository.
   * @throws Exception
   */
  @Command(name = "oasp4j", description = "This command clones oasp4j repository.", context = ContextType.NONE, proxyParams = true)
  @Parameters(values = { @Parameter(name = "path", description = "a location for the oasp4j download (Current directory if not provided)", optional = true) })
  public void oasp4j(String path) throws Exception {

    path =
        path.isEmpty() ? this.contextPathInfo.getCurrentWorkingDirectory().toString() + File.separatorChar + "oasp4j"
            : path;
    try {

      File folder = new File(path);
      if (!folder.exists()) {
        folder.mkdirs();
      }

      getOutput().showMessage("Cloning from " + OASP4J_URL + " to " + path);
      Git result = Git.cloneRepository().setURI(OASP4J_URL).setDirectory(folder).call();
      getOutput().showMessage("Stored repository in: " + result.getRepository().getDirectory());

    } catch (TransportException te) {
      File dotGit = new File(path + File.separator + DOT_GIT);
      if (dotGit.exists()) {
        FileUtils.deleteDirectory(dotGit);
      }
      getOutput().showError(
          "Connection error. Please verify your proxy or use the -proxyHost and -proxyPort parameters");
      throw te;
    } catch (Exception e) {
      getOutput().showError("Getting the OASP4J code from Github: %s", e.getMessage());
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
  @Command(name = "devoncode", description = "This command clones the Devonfw repository.", context = ContextType.NONE, proxyParams = true)
  @Parameters(values = {
  @Parameter(name = "path", description = "a location for the devon download (Current directory if not provided)", optional = true),
  @Parameter(name = "username", description = "a user with permissions to download the Devon repository from Github."),
  @Parameter(name = "password", description = "the password for the user"), })
  public void devoncode(String path, String username, String password) throws Exception {

    path =
        path.isEmpty() ? this.contextPathInfo.getCurrentWorkingDirectory().toString() + File.separatorChar + "devon"
            : path;

    try {

      File folder = new File(path);
      if (!folder.exists()) {
        folder.mkdirs();
      }

      Git result =
          Git.cloneRepository().setURI(DEVON_URL).setDirectory(folder)
              .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();
      getOutput().showMessage("Stored repository in: " + result.getRepository().getDirectory());
    } catch (TransportException te) {
      File dotGit = new File(path + File.separator + DOT_GIT);
      if (dotGit.exists()) {
        FileUtils.deleteDirectory(dotGit);
      }
      getOutput()
          .showError(
              "Connection error. Please verify your github credentials. Also if you work behind a proxy verify it's configuration or use the -proxyHost and -proxyPort parameters");
      throw te;
    } catch (Exception e) {
      getOutput().showError("Getting the Devonfw code from Github: %s", e.getMessage());
      throw e;
    }

  }

}