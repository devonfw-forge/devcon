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
package com.devonfw.devcon.modules.dist;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.commons.lang3.SystemUtils;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.InputType;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.DistributionType;
import com.devonfw.devcon.common.api.data.InputTypeNames;
import com.devonfw.devcon.common.exception.InvalidConfigurationStateException;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.common.utils.Downloader;
import com.devonfw.devcon.common.utils.Extractor;
import com.devonfw.devcon.common.utils.Utils;
import com.google.common.base.Optional;

/**
 * Module with general tasks related to the distribution itself
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "dist", description = "Module with general tasks related to the distribution itself")
public class Dist extends AbstractCommandModule {

  /**
   * This command downloads and unzips the Devon distribution
   *
   * @param path location to download the Devon distribution
   * @param type the {@link DistributionType} of the distribution
   * @param user a user with permissions to download the Devon distribution
   * @param password the password related to the user with permissions to download the Devon distribution
   * @throws Exception
   */
  @Command(name = "install", description = "This command downloads the last version of a distribution from Teamforge. You can select between Devonfw distribution (by default) and Devon-ide distribution.", context = ContextType.NONE)
  @Parameters(values = {
  @Parameter(name = "path", description = "a location for the Devon distribution download", optional = true, inputType = @InputType(name = InputTypeNames.PATH)),
  @Parameter(name = "type", description = "the type of the distribution, the options are: \n 'devonide' to download DEVON IDE\n 'devondist' to download Devon IP IDE", optional = true, inputType = @InputType(name = InputTypeNames.LIST, values = {
  "devondist"/* ,"devonide" */ }))/*
                                   * ,
                                   *
                                   * @Parameter(name = "user", description =
                                   * "a user with permissions to download the Devon distribution"),
                                   *
                                   * @Parameter(name = "password", description =
                                   * "the password related to the user with permissions to download the Devon distribution"
                                   * , inputType = @InputType(name = InputTypeNames.PASSWORD))
                                   */ })
  public void install(String path, String type/* , String user, String password */) throws Exception {

    Optional<String> teamforgeFileId;
    String distType = "";

    // Default parameters
    path = path.isEmpty() ? getContextPathInfo().getCurrentWorkingDirectory().toString() : path.trim();
    type = type.isEmpty() ? DistConstants.DEVON_DIST : type.trim();
    this.output.status("installing distribution...");

    try {

      if (type.toLowerCase().equals(DistConstants.DEVON_IDE)) {

      } else if (type.toLowerCase().equals(DistConstants.DEVON_DIST) && SystemUtils.IS_OS_WINDOWS) {
        distType = DistConstants.DIST_TYPE_WINDOWS;
      } else if (type.toLowerCase().equals(DistConstants.DEVON_DIST) && SystemUtils.IS_OS_LINUX) {
        distType = DistConstants.DIST_TYPE_LINUX;
      } else {
        throw new Exception("The parameter 'type' of the install command is unknown");
      }

      // Optional<String> fileDownloaded = Downloader.downloadFromTeamForge(path, user, password,
      // teamforgeFileId.get());
      Optional<String> fileDownloaded = Downloader.downloadFromTeamForge(path, distType);
      File downloadedfile = new File(path + File.separator + fileDownloaded.get().toString());
      if (fileDownloaded.isPresent()) {
        Extractor.unZip(path + File.separator + fileDownloaded.get().toString(), path);
        this.output
            .showMessage("Distribution successfully installed. You can now follow the manual steps as described\n"
                + "in the Devonfw Guide or, alternatively, run 'devon dist init' to initialize the distribution.");

      } else {
        if (downloadedfile.exists()) {
          downloadedfile.delete();
        }
        throw new Exception("An error occurred while downloading the file.");
      }
      if (downloadedfile.exists()) {
        downloadedfile.delete();
      }
    } catch (Exception e) {
      getOutput().showError(e.getMessage());
      throw e;
    }

  }

  /**
   * This command initializes the Devon distribution (principally the workspaces and conf directory, so its ready for
   * use
   *
   * @param path location of the Devon distribution
   * @throws Exception
   */
  @Command(name = "init", description = "This command initializes a newly downloaded distribution", context = ContextType.NONE)
  @Parameters(values = {
  @Parameter(name = "path", description = "location of the Devon distribution (current dir if not given)", optional = true, inputType = @InputType(name = InputTypeNames.PATH)) })
  public void init(String path) throws Exception {

    String frsFileId = "";
    File updatebat = null;

    // Default parameters
    path = path.isEmpty() ? getContextPathInfo().getCurrentWorkingDirectory().toString() : path.trim();
    Path path_ = getPath(path);

    if (SystemUtils.IS_OS_WINDOWS) {
      updatebat = path_.resolve(Constants.UPDATE_ALL_WORKSPACES_BAT).toFile();
    } else if (SystemUtils.IS_OS_LINUX) {
      updatebat = path_.resolve(Constants.UPDATE_ALL_WORKSPACES_SH).toFile();
    }

    if (!updatebat.exists()) {
      this.output.showError("Not a Devon distribution");
      return;
    }

    this.output.showMessage("Initializing distribution...");

    try {

      // Run file update-all-workspaces.bat which initializes the distro on first run
      ProcessBuilder processBuilder = new ProcessBuilder(updatebat.getAbsolutePath());
      processBuilder.directory(path_.toFile());
      Process process = processBuilder.start();

      final InputStream isError = process.getErrorStream();
      final InputStream isOutput = process.getInputStream();

      Utils.processOutput(isError, isOutput, this.output);

      this.output.showMessage("Distribution initialized.");

    } catch (Exception e) {
      getOutput().showError(e.getMessage());
      throw e;
    }
  }

  /**
   * This command initializes a Devon distribution for use with Shared Services
   *
   * @param projectname the name for the new project
   * @param artuser the user with permissions in the artifactory repository
   * @param artencpass the encrypted password of the user with permissions in the artifactory repository
   * @param svnurl the URL of the svn repository to do the checkout
   * @param svnuser the user with permissions in the svn repository
   * @param svnpass the password of the user with permissions in the svn repository
   */
  @Command(name = "s2", description = "This command initializes a Devonfw distribution configuring it for Shared Services use.")
  @Parameters(values = {
  @Parameter(name = "projectname", description = "the name for the new project", inputType = @InputType(name = InputTypeNames.GENERIC)),
  @Parameter(name = "user", description = "the userId for Artifactory provided by S2 for the project", inputType = @InputType(name = InputTypeNames.GENERIC)),
  @Parameter(name = "pass", description = "the password for Artifactory", inputType = @InputType(name = InputTypeNames.PASSWORD)),
  @Parameter(name = "engagementname", description = "the name of the repository for the engagement", inputType = @InputType(name = InputTypeNames.GENERIC)),
  @Parameter(name = "ciaas", description = "if the settings.xml must be configured for CIaaS set this as TRUE. Is an optional parameter with FALSE as default value.", optional = true, inputType = @InputType(name = InputTypeNames.LIST, values = {
  "False", "True" })),
  @Parameter(name = "svnurl", description = "the url for the SVN provided by S2", optional = true, inputType = @InputType(name = InputTypeNames.GENERIC)),
  @Parameter(name = "svnuser", description = "the user for the SVN", optional = true, inputType = @InputType(name = InputTypeNames.GENERIC)),
  @Parameter(name = "svnpass", description = "the password for the SVN", optional = true, inputType = @InputType(name = InputTypeNames.PASSWORD)),
  @Parameter(name = "plurl", description = "the url for the Production Line Instance provided by S2", optional = true, inputType = @InputType(name = InputTypeNames.GENERIC)),
  @Parameter(name = "pluser", description = "the user login for the PL instance", optional = true, inputType = @InputType(name = InputTypeNames.GENERIC)),
  @Parameter(name = "plpass", description = "the user passwod for the PL instance", optional = true, inputType = @InputType(name = InputTypeNames.PASSWORD)),
  @Parameter(name = "plJenkinsConnectionName", description = "Eclipse Jenkins connection Name", optional = true, inputType = @InputType(name = InputTypeNames.GENERIC)),
  @Parameter(name = "plSonarQubeConnectionName", description = "Eclipse SonarQube connection Name", optional = true, inputType = @InputType(name = InputTypeNames.GENERIC)),
  @Parameter(name = "plGerritConnectionName", description = "Eclipse Gerrit connection Name", optional = true, inputType = @InputType(name = InputTypeNames.GENERIC)) })
  public void s2(String projectname, String user, String pass, String engagementname, String ciaas, String svnurl,
      String svnuser, String svnpass, String plurl, String pluser, String plpass, String plJenkinsConnectionName,
      String plSonarQubeConnectionName, String plGerritConnectionName) {

    Optional<DistributionInfo> distInfo = getContextPathInfo().getDistributionRoot();
    SharedServices s2 = new SharedServices(this.output);
    try {
      if (distInfo.isPresent()) {
        Path distPath = distInfo.get().getPath();

        if (distInfo.get().getDistributionType().equals(DistributionType.DevonDist)) {

          boolean configureForCiaas = Boolean.parseBoolean(ciaas);
          String ciaas_value = configureForCiaas ? "ciaas" : "";
          int initResult = s2.init(distPath, user, pass, engagementname, ciaas_value);
          if (initResult > 0)
            this.output.showMessage(
                "The configuration of the conf/settings.xml file could not be completed successfully. Please verify it");

          int createResult = s2.create(distPath, projectname, svnurl, svnuser, svnpass);
          if (createResult > 0)
            throw new Exception("An error occurred while project creation.");

          int initPL = s2.initPL(distPath, plurl, pluser, plpass, plJenkinsConnectionName, plSonarQubeConnectionName,
              plGerritConnectionName);
          if (initPL > 0)
            this.output.showMessage(
                "The configuration of the eclipse views could not be completed successfully. Please verify it");

        } else {
          throw new InvalidConfigurationStateException("The conf/settings.json seems to be invalid");
        }

      } else {
        this.output.showMessage("Seems that you are not in a Devon distribution.");
      }

    } catch (Exception e) {
      this.output.showError(e.getMessage());
    }
  }

  /**
   * This command provides the user with basic information about the Devon distribution
   *
   * @param path location to download the Devon distribution
   *
   */
  @Command(name = "info", description = "Basic info about the distribution")
  @Parameters(values = {
  @Parameter(name = "path", description = "a location for the Devon distribution download", optional = true, inputType = @InputType(name = InputTypeNames.PATH)) })
  public void info(String path) {

    try {
      Optional<DistributionInfo> distInfo = getContextPathInfo().getDistributionRoot(path);
      if (distInfo.isPresent()) {
        DistributionInfo info = distInfo.get();

        this.output.showMessage("Distro '%s', version: '%s', present in: %s", info.getDistributionType().name(),
            info.getVersion().toString(), info.getPath().toString());
      } else {
        this.output.showMessage("Seems that you are not in a Devon distribution.");
      }
    } catch (Exception e) {
      this.output.showError(e.getMessage());
    }
  }
}
