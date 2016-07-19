package com.devonfw.devcon.modules.dist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class includes the extracting functionality for extracting compressed files
 *
 * @author pparrado
 */
public class Extractor {

  /**
   * Method that extracts a compressed ZIP file and stores the uncompressed files in a given path
   *
   * @param zipFile the compressed file
   * @param outputFolder the uncompressed file
   */
  public static void unZip(String zipFile, String outputFolder) {

    byte[] buffer = new byte[1024];

    try {

      File folder = new File(outputFolder);
      if (!folder.exists()) {
        folder.mkdir();
      }

      ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
      ZipEntry ze = zis.getNextEntry();

      while (ze != null) {
        if (ze.isDirectory()) {
          ze = zis.getNextEntry();
          continue;
        }

        String fileName = ze.getName();
        File newFile = new File(outputFolder + File.separator + fileName);

        System.out.println("file unzip : " + newFile.getAbsoluteFile());

        File parent = new File(newFile.getParent());
        if (!parent.exists()) {
          parent.mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(newFile);

        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }

        fos.close();
        ze = zis.getNextEntry();
      }

      zis.closeEntry();
      zis.close();

      System.out.println("Done");

    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
