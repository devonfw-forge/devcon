package com.devonfw.devcon.modules.dist;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.output.OutputConsole;

/**
 * Module with general tasks related to the distribution itself
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "dist", description = "Module with general tasks related to the distribution itself", context = "global", deprecated = false)
public class Dist {

  OutputConsole out;

  /**
   * This command downloads and unzips the Devon distribution
   *
   * @param path location to download the Devon distribution
   * @param user a user with permissions to download the Devon distribution
   * @param password the password related to the user with permissions to download the Devon distribution
   * @throws IOException if the outputStream can not be closed
   */
  @Command(name = "largeCustomFarewell", help = "This command is used to say a large custom bye")
  @Parameters(values = {
  @Parameter(name = "path", description = "a location for the Devon distribution download"),
  @Parameter(name = "type", description = "the type of the distribution, the options are: \n 'oasp-ide' to download OASP IDE\n 'devon-ip-ide' to download Devon IP IDE"),
  @Parameter(name = "user", description = "a user with permissions to download the Devon distribution"),
  @Parameter(name = "password", description = "the password related to the user with permissions to download the Devon distribution") })
  public void install(String path, String type, String user, String password) throws IOException {

    System.out.println("installing...");
    String source = "";
    String tempFileName = "";
    OutputStream outputStream = null;
    InputStream inputStream = null;

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
      }
      // if (type.equals("oasp-ide")) {
      // source =
      // "https://coconet.capgemini.com/sf/frs/do/downloadFile/projects.apps2_devon/frs.oasp4j_ide.oasp_ide_2_0_0/frs48558?dl=1";
      // }
      //
      // Authenticator.setDefault(new CustomAuthenticator());
      //
      // FileUtils.copyURLToFile(new URL(source), new File(path + "\\oasp.zip"));

      Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("1.0.5.10", 8080));

      // // File without authentication [OK]-------------------------------------------
      // URL url = new URL("https://github-windows.s3.amazonaws.com/GitHubSetup.exe");
      // outputStream = new BufferedOutputStream(new FileOutputStream(new File("C:\\Temp\\git.exe")));
      // inputStream = url.openConnection(proxy).getInputStream();
      // final byte[] buffer = new byte[65536];
      // while (true) {
      // final int len = inputStream.read(buffer);
      // if (len < 0) {
      // break;
      // }
      // outputStream.write(buffer, 0, len);
      // }

      // File ZIP without authentication [OK]------------------------------------------
      URL url = new URL("https://github.com/expressjs/express/archive/master.zip");

      File folder = new File(path);
      if (!folder.exists()) {
        folder.mkdirs();
      }

      outputStream =
          new BufferedOutputStream(new FileOutputStream(new File(path + File.separator + tempFileName + ".zip")));
      inputStream = url.openConnection(proxy).getInputStream();
      final byte[] buffer = new byte[65536];
      while (true) {
        final int len = inputStream.read(buffer);
        if (len < 0) {
          break;
        }
        outputStream.write(buffer, 0, len);

      }

      // --------------------------------
      // --------------------------------
      // --------------------------------
      // ZipInputStream zis = new ZipInputStream(new FileInputStream(path + File.separator + "express.zip"));
      //
      // ZipEntry ze = zis.getNextEntry();
      //
      // while (ze != null) {
      //
      // String fileName = ze.getName();
      // File newFile = new File(path + File.separator + fileName);
      //
      // System.out.println("file unzip : " + newFile.getAbsoluteFile());
      //
      // if (ze.isDirectory()) {
      // ze = zis.getNextEntry();
      // continue;
      // }
      //
      // // create all non exists folders
      // // else you will hit FileNotFoundException for compressed folder
      // new File(newFile.getParent()).mkdirs();
      //
      // FileOutputStream fos = new FileOutputStream(newFile);
      //
      // int len;
      // while ((len = zis.read(buffer)) > 0) {
      // fos.write(buffer, 0, len);
      // }
      //
      // fos.close();
      // ze = zis.getNextEntry();
      //
      // }
      //
      // zis.closeEntry();
      // zis.close();
      // --------------------------------
      // --------------------------------
      // --------------------------------

      extractZipFile(path, tempFileName);

      // File zipFile = new File(path + File.separator + "express.zip");
      // if (zipFile.exists()) {
      // zipFile.delete();
      // }

      // // File with authentication------------------------------------------------------
      // Authenticator.setDefault(new CustomAuthenticator());
      // source =
      // "https://coconet.capgemini.com/sf/frs/do/downloadFile/projects.apps2_devon/frs.oasp4j_ide.oasp_ide_2_0_0/frs48558?dl=1";
      // URL url = new URL(source);
      // outputStream = new BufferedOutputStream(new FileOutputStream(new File("C:\\Temp\\oasp.zip")));
      //
      // URLConnection connection = url.openConnection(proxy);
      // connection.connect();
      // // inputStream = url.openConnection(proxy).getInputStream();
      // inputStream = connection.getInputStream();
      // final byte[] buffer = new byte[65536];
      // while (true) {
      // final int len = inputStream.read(buffer);
      // if (len < 0) {
      // break;
      // }
      // outputStream.write(buffer, 0, len);
      // }

    } catch (Exception e) {
      // TODO implement logs
      System.out.println("[ERROR]" + e.getMessage());

      this.out = new OutputConsole();
      this.out.showError(e.getMessage());
    } finally {
      if (outputStream != null)
        outputStream.close();

      File zipFile = new File(path + File.separator + tempFileName + ".zip");
      if (zipFile.exists()) {
        zipFile.delete();
      }
    }

  }

  private void extractZipFile(String path, String tempFileName) throws Exception {

    byte[] buffer = new byte[65536];
    ZipInputStream zis = null;
    FileOutputStream fos = null;

    try {
      zis = new ZipInputStream(new FileInputStream(path + File.separator + tempFileName + ".zip"));

      ZipEntry ze = zis.getNextEntry();

      while (ze != null) {

        String fileName = ze.getName();
        File newFile = new File(path + File.separator + fileName);

        System.out.println("file unzip : " + newFile.getAbsoluteFile());

        if (ze.isDirectory()) {
          ze = zis.getNextEntry();
          continue;
        }

        new File(newFile.getParent()).mkdirs();

        fos = new FileOutputStream(newFile);

        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }

        // fos.close();
        ze = zis.getNextEntry();

      }

      // zis.closeEntry();
      // zis.close();
    } catch (Exception e) {
      // TODO: implement logs
      throw e;
    } finally {
      if (fos != null)
        fos.close();
      if (zis != null) {
        zis.closeEntry();
        zis.close();
      }
    }
  }
}
