package com.devonfw.devcon.modules.dist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.devonfw.devcon.output.OutputConsole;

/**
 * Encapsulates functionality for s2 command of module Dist
 *
 * @author pparrado
 */
public class SharedServices {

  public static int init(Path distPath, String artUser, String artEncPass) {

    OutputConsole out = new OutputConsole();
    try {
      File outputFile = File.createTempFile("devconS2create_", ".txt");

      File batchFile = new File(distPath.toString() + File.separator + DistConstants.INIT_SCRIPT);
      ProcessBuilder processBuilder = new ProcessBuilder(batchFile.getAbsolutePath(), artUser, artEncPass);
      processBuilder.redirectErrorStream(true);
      processBuilder.redirectOutput(outputFile);

      processBuilder.directory(new File(distPath.toString()));
      Process process = processBuilder.start();

      int exitStatus = process.waitFor();
      out.status(DistConstants.INIT_SCRIPT + " finished with status: " + exitStatus);
      return exitStatus;
    } catch (Exception e) {
      out.showError(e.getMessage());
      return -1;
    }
  }

  public static int create(Path distPath, String projectName, String svnUrl, String svnUser, String svnPass)
      throws IOException, InterruptedException {

    OutputConsole out = new OutputConsole();
    try {
      File outputFile = File.createTempFile("devconS2create_", ".txt");

      File batchFile = new File(distPath.toString() + File.separator + DistConstants.CREATE_SCRIPT);
      ProcessBuilder processBuilder =
          new ProcessBuilder(batchFile.getAbsolutePath(), projectName, svnUrl, svnUser, svnPass);
      processBuilder.redirectErrorStream(true);
      processBuilder.redirectOutput(outputFile);

      processBuilder.directory(new File(distPath.toString()));
      Process process = processBuilder.start();

      int exitStatus = process.waitFor();
      out.status(DistConstants.CREATE_SCRIPT + " finished with status: " + exitStatus);
      return exitStatus;
    } catch (Exception e) {
      out.showError(e.getMessage());
      return -1;
    }

  }

}
