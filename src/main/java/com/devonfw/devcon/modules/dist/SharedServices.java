package com.devonfw.devcon.modules.dist;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;

import com.devonfw.devcon.output.Output;

/**
 * Encapsulates functionality for s2 command of module Dist
 *
 * @author pparrado
 */
public class SharedServices {

  private Output out;

  public SharedServices(Output out) {
    this.out = out;
  }

  public int init(Path distPath, String artUser, String artEncPass) throws Exception {

    try {

      File batchFile = new File(distPath.toString() + File.separator + DistConstants.INIT_SCRIPT);
      ProcessBuilder processBuilder = new ProcessBuilder(batchFile.getAbsolutePath(), artUser, artEncPass);
      processBuilder.redirectErrorStream(true);
      processBuilder.redirectOutput(Redirect.PIPE);

      processBuilder.directory(new File(distPath.toString()));
      Process process = processBuilder.start();

      BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line = null;
      while ((line = in.readLine()) != null) {
        this.out.showMessage(line);
      }

      int exitStatus = process.waitFor();
      return exitStatus;
    } catch (Exception e) {
      throw e;
    }
  }

  public int create(Path distPath, String projectName, String svnUrl, String svnUser, String svnPass) throws Exception {

    try {

      File batchFile = new File(distPath.toString() + File.separator + DistConstants.CREATE_SCRIPT);
      ProcessBuilder processBuilder =
          new ProcessBuilder(batchFile.getAbsolutePath(), projectName, svnUrl, svnUser, svnPass);
      processBuilder.redirectErrorStream(true);
      processBuilder.redirectOutput(Redirect.PIPE);

      processBuilder.directory(new File(distPath.toString()));
      Process process = processBuilder.start();

      BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line = null;
      while ((line = in.readLine()) != null) {
        this.out.showMessage(line);
      }
      int exitStatus = process.waitFor();
      return exitStatus;
    } catch (Exception e) {
      throw e;
    }

  }

}
