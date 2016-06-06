package com.devonfw.devcon.modules.dist;

import java.io.File;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.impl.AbstractCommandHolder;

/**
 * Module with general tasks related to the distribution itself
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "dist", description = "Module with general tasks related to the distribution itself", context = "global", deprecated = false)
public class Dist extends AbstractCommandHolder {

  /**
   * This command downloads and unzips the Devon distribution
   *
   * @param path location to download the Devon distribution
   * @param user a user with permissions to download the Devon distribution
   * @param password the password related to the user with permissions to download the Devon distribution
   * @throws Exception
   */
  @Command(name = "install", help = "This command downloads the distribution")
  @Parameters(values = {
  @Parameter(name = "path", description = "a location for the Devon distribution download"),
  @Parameter(name = "type", description = "the type of the distribution, the options are: \n 'oasp-ide' to download OASP IDE\n 'devon-ip-ide' to download Devon IP IDE"),
  @Parameter(name = "user", description = "a user with permissions to download the Devon distribution"),
  @Parameter(name = "password", description = "the password related to the user with permissions to download the Devon distribution") })
  public void install(String path, String type, String user, String password) throws Exception {

    // TODO: read source value from a config file
    String frsFileId = "";
    String tempFileName = "";

    this.output.status("installing distribution...");

    try {

      // TODO create an enum with distribution types and replace if/else with switch
      if (type.equals("oasp-ide")) {
        frsFileId = DistConstants.OAPS_FILE_ID;
      } else if (type.equals("devon-ip-ide")) {
        frsFileId = DistConstants.DEVON_FILE_ID;
      } else {
        throw new Exception("The parameter 'type' of the install command is unknown");
      }

      String fileDownloaded = Downloader.downloadFromTeamForge(path, user, password, frsFileId);

      if (fileDownloaded != null && !fileDownloaded.equals("")) {
        Extractor.extract(path + File.separator + fileDownloaded, path);
      }

      this.output.success("install");
    } catch (Exception e) {
      // TODO implement logs
      System.out.println("[LOG]" + e.getMessage());
      throw e;
    } finally {

      // File compressedFile = new File(path + File.separator + tempFileName /* + ".zip" */);
      // if (compressedFile.exists()) {
      // compressedFile.delete();
      // }
    }

  }

}
