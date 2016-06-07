package com.devonfw.devcon.output;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

/**
 * Shows a progress bar for downloading tasks
 *
 * @author pparrado
 */
public class DownloadingProgress implements Runnable {
  private volatile boolean running = true;

  private volatile Long finalSize;

  private volatile String tempPath;

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
