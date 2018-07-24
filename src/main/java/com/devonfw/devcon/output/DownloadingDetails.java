package com.devonfw.devcon.output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author adubey4
 *
 */
public class DownloadingDetails {
  /**
   * @param publicurl is using for download distribution
   * @return size of the distribution zip file
   */
  public static Double getSize(String publicurl) {

    URL url;
    URLConnection conn;
    int size;
    Double getSize = 0.0;

    try {
      url = new URL(publicurl);
      conn = url.openConnection();
      size = conn.getContentLength();
      getSize = (size / 1024.0) / 1024.0;
      conn.getInputStream().close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return getSize;
  }

  /**
   * @param publicurl is using for download distribution
   * @return total size of zip file
   */
  public static Long size(String publicurl) {

    URL url;
    URLConnection conn;
    long size = 0;
    try {
      url = new URL(publicurl);
      conn = url.openConnection();
      size = conn.getContentLength();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return size;
  }

  /**
   * @param fileInLocaltemp is path for saved temp file
   * @param fileInLocal is path where will keep the downloaded distribution zip folder.
   * @return success or unsuccess status after copy the file
   */
  public static boolean copy(File fileInLocaltemp, File fileInLocal) {

    InputStream inStream = null;
    OutputStream outStream = null;
    boolean flag = false;

    try {

      inStream = new FileInputStream(fileInLocaltemp);
      outStream = new FileOutputStream(fileInLocal);

      byte[] buffer = new byte[1024];

      int length;
      // copy the file content in bytes
      while ((length = inStream.read(buffer)) > 0) {
        outStream.write(buffer, 0, length);
      }

      inStream.close();
      outStream.close();

      // delete the Temp file
      fileInLocaltemp.delete();
      flag = true;
      System.out.println("File is copied successful!");

    } catch (IOException e) {
      e.printStackTrace();
    }
    return flag;
  }

}
