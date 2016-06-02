package com.devonfw.devcon.modules.dist;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

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
    String source = "";
    String tempFileName = "";

    this.output.status("installing distribution...");

    try {
      switch (type) {
      case "oasp-ide":
        source = "https://github.com/expressjs/express/archive/master.zip";
        tempFileName = type;
        break;
      case "devon-ip-ide":
        source = "https://github.com/expressjs/express/archive/master.zip";
        tempFileName = type;
        break;
      default:
        throw new Exception("The parameter 'type' of the install command is unknown");
      }

      File distribution = new File(path + File.separator + tempFileName);

      if (!distribution.exists()) {
        this.output.status("downloading " + type + " distribution...");
        downloadFile(source, path, tempFileName);
        this.output.status("distribution downloaded.");
      } else {
        this.output.status("distribution '" + tempFileName + "' founded in the directory");
      }

      this.output.status("extracting distribution...");
      Extractor.extract(path + File.separator + tempFileName /* + ".zip" */, path);

      this.output.status("distribution extracted");
      this.output.success("install");
    } catch (Exception e) {
      // TODO implement logs
      System.out.println("[ERROR]" + e.getMessage());

      this.output.showError(e.getMessage());
      throw e;
    } finally {

      File compressedFile = new File(path + File.separator + tempFileName /* + ".zip" */);
      if (compressedFile.exists()) {
        compressedFile.delete();
      }
    }

  }

  private void downloadFile(String source, String path, String tempFileName) throws Exception {

    OutputStream outputStream = null;
    InputStream inputStream = null;

    try {

      Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("1.0.5.10", 8080));

      // File ZIP without authentication [OK]------------------------------------------

      URL url = new URL(source);

      File folder = new File(path);
      if (!folder.exists()) {
        folder.mkdirs();
      }

      outputStream = new BufferedOutputStream(new FileOutputStream(new File(path + File.separator + tempFileName /*
                                                                                                                  * +
                                                                                                                  * ".zip"
                                                                                                                  */)));
      inputStream = url.openConnection(proxy).getInputStream();
      final byte[] buffer = new byte[65536];
      while (true) {
        final int len = inputStream.read(buffer);
        if (len < 0) {
          break;
        }
        outputStream.write(buffer, 0, len);

      }

    } catch (Exception e) {
      throw e;
    } finally {
      if (outputStream != null)
        outputStream.close();
    }

  }

}
