package com.devonfw.devcon.modules.dist;

import java.io.File;
import java.nio.file.Path;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.DistributionType;
import com.devonfw.devcon.common.exception.InvalidConfigurationStateException;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
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
  @Command(name = "install", description = "This command downloads the distribution", context = ContextType.NONE)
  @Parameters(values = {
  @Parameter(name = "path", description = "a location for the Devon distribution download", optional = true),
  @Parameter(name = "type", description = "the type of the distribution, the options are: \n 'oaspide' to download OASP IDE\n 'devondist' to download Devon IP IDE", optional = true),
  @Parameter(name = "user", description = "a user with permissions to download the Devon distribution"),
  @Parameter(name = "password", description = "the password related to the user with permissions to download the Devon distribution") })
  public void install(String path, String type, String user, String password) throws Exception {

    String frsFileId = "";

    // Default parameters
    path = path.isEmpty() ? getContextPathInfo().getCurrentWorkingDirectory().toString() : path.trim();
    type = type.isEmpty() ? DistConstants.DEVON_DIST : type.trim();

    this.output.status("installing distribution...");

    try {

      if (type.toLowerCase().equals(DistConstants.OASP_IDE)) {
        frsFileId = DistConstants.OAPS_FILE_ID;
      } else if (type.toLowerCase().equals(DistConstants.DEVON_DIST)) {
        frsFileId = DistConstants.DEVON_FILE_ID;
      } else {
        throw new Exception("The parameter 'type' of the install command is unknown");
      }

      Optional<String> fileDownloaded = Downloader.downloadFromTeamForge(path, user, password, frsFileId);

      if (fileDownloaded.isPresent()) {
        // Extractor.extract(path + File.separator + fileDownloaded.get().toString(), path);
        Extractor.unZip(path + File.separator + fileDownloaded.get().toString(), path);
        this.output.success("install");
      } else {
        throw new Exception("An error occurred while downloading the file.");
      }

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
  @Command(name = "s2", description = "Initializes a Devon distribution for use with Shared Services.")
  @Parameters(values = {
  @Parameter(name = "projectname", description = "the name for the new project"),
  @Parameter(name = "artuser", description = "the user with permissions in the artifactory repository"),
  @Parameter(name = "artencpass", description = "the encrypted password of the user with permissions in the artifactory repository"),
  @Parameter(name = "svnurl", description = "the URL of the svn repository to do the checkout"),
  @Parameter(name = "svnuser", description = "the user with permissions in the svn repository"),
  @Parameter(name = "svnpass", description = "the password of the user with permissions in the svn repository") })
  public void s2(String projectname, String artuser, String artencpass, String svnurl, String svnuser, String svnpass) {

    Optional<DistributionInfo> distInfo = getContextPathInfo().getDistributionRoot();
    SharedServices s2 = new SharedServices(this.output);

    try {
      if (distInfo.isPresent()) {
        Path distPath = distInfo.get().getPath();

        if (distInfo.get().getDistributionType().equals(DistributionType.DevonDist)) {

          int initResult = s2.init(distPath, artuser, artencpass);
          if (initResult > 0)
            this.output
                .showMessage("The configuration of the conf/settings.xml file could not be completed successfully. Please verify it");

          int createResult = s2.create(distPath, projectname, svnurl, svnuser, svnpass);
          if (createResult > 0)
            throw new Exception("An error occurred while project creation.");

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

}
