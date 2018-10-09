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
package com.devonfw.devcon.modules.github;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.InputType;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.InputTypeNames;
import com.devonfw.devcon.common.impl.AbstractCommandModule;

/**
 * This class contains command to clone devon4j and devon repositories.
 *
 * @author ssarmoka
 */
@CmdModuleRegistry(name = "github", description = "Module to get Github repositories related to Devonfw.", sort = 4)
public class Github extends AbstractCommandModule {

  public static final String DEVON4J_URL = "https://github.com/devonfw/devon4j.git";

  public static final String DEVON_URL = "https://github.com/devonfw/devon.git";

  public static final String DOT_GIT = ".git";

  /**
   * This command is to clone devon4j repository.
   *
   * @param path location to download the devon4j repository.
   * @throws Exception
   */
  @Command(name = "devon4j", description = "This command clones devon4j repository.", context = ContextType.NONE, proxyParams = true)
  @Parameters(values = {
  @Parameter(name = "path", description = "a location for the devon4j download (Current directory if not provided)", optional = true, inputType = @InputType(name = InputTypeNames.PATH)) })
  public void devon4j(String path) throws Exception {

    path = path.isEmpty()
        ? this.contextPathInfo.getCurrentWorkingDirectory().toString() + File.separatorChar + "devon4j"
        : path;
    try {

      File folder = new File(path);
      if (!folder.exists()) {
        folder.mkdirs();
      }

      getOutput().showMessage("Cloning from " + DEVON4J_URL + " to " + path);
      Git result = Git.cloneRepository().setURI(DEVON4J_URL).setDirectory(folder).call();
      getOutput().showMessage("Stored repository in: " + result.getRepository().getDirectory());

    } catch (TransportException te) {
      File dotGit = new File(path + File.separator + DOT_GIT);
      if (dotGit.exists()) {
        FileUtils.deleteDirectory(dotGit);
      }
      getOutput()
          .showError("Connection error. Please verify your proxy or use the -proxyHost and -proxyPort parameters");
      throw te;
    } catch (Exception e) {
      getOutput().showError("Getting the DEVON4J code from Github: %s", e.getMessage());
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
  @Parameter(name = "path", description = "a location for the devon download (Current directory if not provided)", optional = true, inputType = @InputType(name = InputTypeNames.PATH)),
  @Parameter(name = "username", description = "a user with permissions to download the Devon repository from Github."),
  @Parameter(name = "password", description = "the password for the user", inputType = @InputType(name = InputTypeNames.PASSWORD)) })
  public void devoncode(String path, String username, String password) throws Exception {

    path = path.isEmpty() ? this.contextPathInfo.getCurrentWorkingDirectory().toString() + File.separatorChar + "devon"
        : path;

    try {

      File folder = new File(path);
      if (!folder.exists()) {
        folder.mkdirs();
      }

      Git result = Git.cloneRepository().setURI(DEVON_URL).setDirectory(folder)
          .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();
      getOutput().showMessage("Stored repository in: " + result.getRepository().getDirectory());
    } catch (TransportException te) {
      File dotGit = new File(path + File.separator + DOT_GIT);
      if (dotGit.exists()) {
        FileUtils.deleteDirectory(dotGit);
      }
      getOutput().showError(
          "Connection error. Please verify your github credentials. Also if you work behind a proxy verify it's configuration or use the -proxyHost and -proxyPort parameters");
      throw te;
    } catch (Exception e) {
      getOutput().showError("Getting the Devonfw code from Github: %s", e.getMessage());
      throw e;
    }

  }

}
