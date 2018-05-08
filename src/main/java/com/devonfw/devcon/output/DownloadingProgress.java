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
package com.devonfw.devcon.output;

import java.io.File;
import java.util.Observable;

import org.apache.commons.io.FilenameUtils;

/**
 * Shows a progress bar for downloading tasks
 *
 * @author pparrado
 */
public class DownloadingProgress extends Observable implements Runnable {
  private volatile boolean running = true;

  private volatile Long finalSize;

  private volatile String tempPath;

  public static volatile double downloadProgress;;

  public DownloadingProgress(Long finalSize, String tempPath) {

    this.finalSize = finalSize;
    this.tempPath = tempPath;
  }

  public void terminate() {

    this.running = false;
  }

  public void run() {

    while (this.running) {
      try {
        Thread.sleep(5000);
        File dir = new File(this.tempPath);
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
          File lastModifiedFile = files[0];
          for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()
                && FilenameUtils.getExtension(files[i].getPath()).equals("att")) {
              lastModifiedFile = files[i];
            }
          }

          if (FilenameUtils.getExtension(lastModifiedFile.getPath()).equals("att")) {
            double perc = lastModifiedFile.length() * 100 / this.finalSize;

            int p = (int) Math.round(perc);
            int rest = 10 - (p / 10);
            StringBuilder bar = new StringBuilder();
            bar.append("[");
            for (int i = 0; i < (p / 10); i++) {
              bar.append("=");
            }
            for (int j = 0; j < rest; j++) {
              bar.append(" ");
            }
            bar.append("]");
            downloadProgress = p;
            System.out.print("\r" + bar.toString() + " " + p + "% downloaded");
          }

        }

      } catch (InterruptedException e) {
        System.out.println("ERROR");
        this.running = false;
      }
    }

  }
}
