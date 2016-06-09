package com.devonfw.devcon.modules.github;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.output.OutputConsole;

/**
 * TODO ssarmoka This type ...
 *
 * @author ssarmoka
 */
@CmdModuleRegistry(name = "github", description = "Module to create a new workspace with all default configuration", context = "MyContextIsNotGlobal", deprecated = false)
public class Github {

  OutputConsole out;

  /**
   * This command downloads and unzips th\e Devon distribution
   *
   * @param path location to download the Devon distribution
   * @param user a user with permissions to download the Devon distribution
   * @param password the password related to the user with permissions to download the Devon distribution
   * @throws Exception
   */
  @Command(name = "oasp4j", help = "This command downloads the distribution")
  @Parameters(values = { @Parameter(name = "path", description = "a location for the oasp download") })
  public void oasp4j(String path) throws Exception {

    final String REMOTE_URL = "https://github.com/oasp/oasp4j.git";

    File folder = new File(path);
    if (!folder.exists()) {
      folder.mkdirs();
    }

    // then clone
    System.out.println("Cloning from " + REMOTE_URL + " to " + path);
    try (Git result = Git.cloneRepository().setURI(REMOTE_URL).setDirectory(folder).call()) {

      System.out.println("Having repository: " + result.getRepository().getDirectory());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Command(name = "devoncode", help = "This command downloads the distribution")
  @Parameters(values = { @Parameter(name = "path", description = "a location for the oasp download"),
  @Parameter(name = "username", description = "a user with permissions to download the Devon distribution"),
  @Parameter(name = "password", description = "the password related to the user with permissions to download the Devon distribution") })
  public void devoncode(String path, String username, String password) throws Exception {

    final String REMOTE_URL = "https://github.com/devonfw/devon.git";

    File folder = new File(path);
    if (!folder.exists()) {
      folder.mkdirs();
    }

    // then clone
    System.out.println("Cloning from " + REMOTE_URL + " to " + path);
    try (Git result = Git.cloneRepository().setURI(REMOTE_URL).setDirectory(folder)
        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call()) {// Git.cloneRepository().setURI(REMOTE_URL).setDirectory(folder).call())
                                                                                                      // {
      // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
      System.out.println("Having repository: " + result.getRepository().getDirectory());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
