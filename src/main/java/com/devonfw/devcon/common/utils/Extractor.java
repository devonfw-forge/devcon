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
