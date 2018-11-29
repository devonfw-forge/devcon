
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

import org.apache.commons.io.FileUtils;

/**
 * Shows a progress bar for downloading tasks
 *
 * @author pparrado
 */
public class DownloadingProgress extends Observable implements Runnable {
  private volatile boolean running = true;

  private volatile Long finalSize;

  private volatile String tempPath;

  @SuppressWarnings("javadoc")
  public static volatile double downloadProgress;

  long fileSize = 0;

  @SuppressWarnings("javadoc")
  public DownloadingProgress(Long finalSize, String tempPath) {

    this.finalSize = finalSize;
    this.tempPath = tempPath;
  }

  /**
   * terminated process of progress bar
   */
  public void terminate() {

    this.running = false;

  }

  @SuppressWarnings("javadoc")
  public void run() {

    while (this.running) {
      try {
        Thread.sleep(5000);
        File file = new File(this.tempPath);
        long filesize = FileUtils.sizeOf(file);
        downloadProgress = filesize * 100 / this.finalSize;

      } catch (InterruptedException e) {
        System.out.println("ERROR");
        this.running = false;
      }
    }

  }
}
