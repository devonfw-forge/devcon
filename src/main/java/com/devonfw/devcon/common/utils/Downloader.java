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
package com.devonfw.devcon.common.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.modules.dist.DistConstants;
import com.devonfw.devcon.output.ConsoleOutput;
import com.devonfw.devcon.output.DownloadingDetails;
import com.devonfw.devcon.output.DownloadingProgress;
import com.devonfw.devcon.output.Output;
import com.google.common.base.Optional;

/**
 * Class to encapsulate the functionality related to the Team Forge download process.
 *
 * @author pparrado
 */
public class Downloader {

  public static DownloadingProgress progressBar;

  /**
   * Downloads a file from Team Forge repository
   *
   * @param path the path where the file should be downloaded
   * @param distType is the type of distribution
   * @return file download status
   */
  @SuppressWarnings("null")
  public static Optional<String> downloadFromTeamForge(String path, String distType) {

    Output out = new ConsoleOutput();
    String fileName = "";
    String userTempDir = System.getProperty("java.io.tmpdir");
    String respositiryUrl = "";
    @Nonnull
    File fileInLocaltemp = null;
    long transferedSize = 0;
    long finalsize = 0;
    boolean copyfile = false;
    try {

      /* Checking OS Type and assing file name */
      /* Assining REPOSITORY_URL in local variable based on OS type for download Dist */
      if (distType == "windows") {
        fileName = DistConstants.DIST_FILENAME_WINDOWS;
        respositiryUrl = DistConstants.REPOSITORY_URL + DistConstants.WINDOWS_DIST_ZIP;
      } else {
        fileName = DistConstants.DIST_FILENAME_LINUX;
        respositiryUrl = DistConstants.REPOSITORY_URL + DistConstants.LINUX_DIST_ZIP;
      }

      /* Creating Local and Temp File path for save the Dist */
      fileInLocaltemp = new File(userTempDir + File.separator + fileName);
      File fileInLocal = new File(path + File.separator + fileName);

      if (fileInLocal.exists()) {
        throw new FileAlreadyExistsException(path + File.separator + fileName);
      }
      if (fileInLocaltemp.exists()) {
        fileInLocaltemp.delete();
      }
      /* Creating Local directory */
      fileInLocal.getParentFile().mkdirs();
      /* calculating size of zip file */
      Double size = DownloadingDetails.getSize(respositiryUrl);
      DecimalFormat df = new DecimalFormat("#.##");
      df.setRoundingMode(RoundingMode.CEILING);
      finalsize = DownloadingDetails.size(respositiryUrl);

      out.status("Downloading-- " + fileName + " (" + df.format(size) + "MB). It may take a few minutes.");
      /* download file from Url using Javanio lib */
      transferedSize = Downloader.downloadingFile(fileInLocaltemp.toString(), respositiryUrl);

      if (finalsize != transferedSize) {
        throw new DownloadFileException("downloading failed, some issue in downloading .");
      }
      // copy Temp to to target using Files Class
      copyfile = DownloadingDetails.copy(fileInLocaltemp, fileInLocal);
      if (copyfile != true) {
        fileInLocal.delete();
        throw new DownloadFileException("issue while copying the file .");
      }
      out.statusInNewLine("File downloaded successfully.");

      return Optional.of(fileName);

    } catch (RemoteException e) {
      out.showError(e.getMessage());
      return null;
    } catch (FileNotFoundException e) {

      out.showError("Download failed. File " + fileName + " not found in the repository " + respositiryUrl + ". "
          + e.getMessage());
      return null;
    } catch (FileAlreadyExistsException e) {
      out.showError("Download failed. File " + e.getFile() + " already exists.");
      return null;
    }

    catch (DownloadFileException de) {
      out.showError(de.getMessage());
      return null;
    } catch (Exception e) {
      out.showError(e.getMessage());
      return null;
    } finally {
      if (fileInLocaltemp.exists()) {
        // TODO implement logs
        System.out.println("[LOG] Deleting temp file " + fileInLocaltemp.getPath() + "...");
        fileInLocaltemp.delete();
        System.out.println("[LOG] Temp file " + fileInLocaltemp.getPath() + " deleted.");
      }

    }

  }

  /**
   * Downloads a file from a URL
   *
   * @param source the location of the file
   * @param path the file destiny in local machine
   * @param tempFileName the temporary file name
   * @throws Exception if the file can not be found or the streams can't be managed
   */
  public void downloadFile(String source, String path, String tempFileName) throws Exception {

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

      outputStream = new BufferedOutputStream(
          new FileOutputStream(new File(path + File.separator + tempFileName /*
                                                                              * + ".zip"
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

  /**
   * Method to obtain the Devcon's configuration properties from the devonfw.github.io repository, from the
   * "version.json" file
   *
   * @param property the reference of the file in Teamforge
   * @return the file ID
   */
  public static Optional<String> getDevconConfigProperty(String property) {

    try {
      String propertyValue = null;
      JSONObject json = null;
      json = new JSONObject(IOUtils.toString(new URL(Devcon.VERSION_URL), Charset.forName("UTF-8")));
      propertyValue = (String) json.get(property);
      return Optional.of(propertyValue);
    } catch (Exception e) {
      return Optional.absent();
    }

  }

  private static long downloadingFile(String file, String urlStr) throws IOException, InterruptedException {

    URL url = new URL(urlStr);
    ReadableByteChannel rbc = Channels.newChannel(url.openStream());
    FileOutputStream fos = new FileOutputStream(file);
    long finalsize = DownloadingDetails.size(urlStr);
    long downloadfilesize = 0;
    /* Starting Progress Bar */
    progressBar = new DownloadingProgress(finalsize, file);
    Thread thread = new Thread(progressBar);
    thread.start();
    downloadfilesize = fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

    if (thread != null) {
      progressBar.terminate();
      thread.join();
    }
    /* finished Progress Bar */
    fos.close();
    rbc.close();
    return downloadfilesize;
  }

}
