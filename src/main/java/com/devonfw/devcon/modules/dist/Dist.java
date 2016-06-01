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
    OutputConsole out = new OutputConsole();

    out.status("installing distribution...");

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

      out.status("downloading " + type + " distribution...");
      downloadFile(source, path, tempFileName);
      out.status("distribution downloaded.");
      // extractZipFile(path, tempFileName);

      // extract7ZipFile("C:\\Temp", "express-master");

      // WORKS
      // extract("C:\\Temp\\express-master.7z", "C:\\Temp\\Express\\");

      out.status("extracting distribution...");
      Extractor.extract(path + File.separator + tempFileName + ".zip", path);

      out.status("distribution extracted");
      out.success("install");
    } catch (Exception e) {
      // TODO implement logs
      System.out.println("[ERROR]" + e.getMessage());

      out.showError(e.getMessage());
      throw e;
    } finally {

      // File compressedFile = new File(path + File.separator + tempFileName + ".zip");
      // if (compressedFile.exists()) {
      // compressedFile.delete();
      // }
    }

  }

  private void downloadFile(String source, String path, String tempFileName) throws Exception {

    OutputStream outputStream = null;
    InputStream inputStream = null;

    try {

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

      URL url = new URL(source);

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
      throw e;
    } finally {
      if (outputStream != null)
        outputStream.close();
    }

  }

  // private void extractZipFile(String path, String tempFileName) throws Exception {
  //
  // byte[] buffer = new byte[65536];
  // ZipInputStream zis = null;
  // FileOutputStream fos = null;
  //
  // try {
  // zis = new ZipInputStream(new FileInputStream(path + File.separator + tempFileName + ".zip"));
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
  // new File(newFile.getParent()).mkdirs();
  //
  // fos = new FileOutputStream(newFile);
  //
  // int len;
  // while ((len = zis.read(buffer)) > 0) {
  // fos.write(buffer, 0, len);
  // }
  //
  // // fos.close();
  // ze = zis.getNextEntry();
  //
  // }
  //
  // // zis.closeEntry();
  // // zis.close();
  // } catch (Exception e) {
  // // TODO: implement logs
  // throw e;
  // } finally {
  // if (fos != null)
  // fos.close();
  // if (zis != null) {
  // zis.closeEntry();
  // zis.close();
  // }
  // }
  // }
  //
  // private void extract7ZipFile(String path, String tempFileName) {
  //
  // RandomAccessFile randomAccessFile = null;
  // IInArchive inArchive = null;
  // try {
  // randomAccessFile = new RandomAccessFile(new File(path + File.separator + tempFileName + ".7z"), "r");
  // inArchive = SevenZip.openInArchive(null, // autodetect archive type
  // new RandomAccessFileInStream(randomAccessFile));
  //
  // // Getting simple interface of the archive inArchive
  // ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
  //
  // System.out.println("   Hash   |    Size    | Filename");
  // System.out.println("----------+------------+---------");
  //
  // for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
  // final int[] hash = new int[] { 0 };
  // if (!item.isFolder()) {
  // ExtractOperationResult result;
  //
  // final long[] sizeArray = new long[1];
  // result = item.extractSlow(new ISequentialOutStream() {
  // @Override
  // public int write(byte[] data) throws SevenZipException {
  //
  // hash[0] ^= Arrays.hashCode(data); // Consume data
  // sizeArray[0] += data.length;
  // return data.length; // Return amount of consumed data
  // }
  // });
  //
  // if (result == ExtractOperationResult.OK) {
  // System.out.println(String.format("%9X | %10s | %s", hash[0], sizeArray[0], item.getPath()));
  // } else {
  // System.err.println("Error extracting item: " + result);
  // }
  // }
  // }
  // } catch (Exception e) {
  // System.err.println("Error occurs: " + e);
  // } finally {
  // if (inArchive != null) {
  // try {
  // inArchive.close();
  // } catch (SevenZipException e) {
  // System.err.println("Error closing archive: " + e);
  // }
  // }
  // if (randomAccessFile != null) {
  // try {
  // randomAccessFile.close();
  // } catch (IOException e) {
  // System.err.println("Error closing file: " + e);
  // }
  // }
  // }
  // }

}
