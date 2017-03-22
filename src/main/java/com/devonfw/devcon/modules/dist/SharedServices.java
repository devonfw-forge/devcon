package com.devonfw.devcon.modules.dist;

import java.io.File;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;

import com.devonfw.devcon.common.utils.Utils;
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

  public int init(Path distPath, String artUser, String artEncPass, String engagementName, String ciaas)
      throws Exception {

    try {

      File batchFile = new File(distPath.toString() + File.separator + DistConstants.INIT_SCRIPT);
      ProcessBuilder processBuilder =
          new ProcessBuilder(batchFile.getAbsolutePath(), artUser, artEncPass, engagementName, ciaas);
      processBuilder.redirectErrorStream(true);
      processBuilder.redirectOutput(Redirect.PIPE);

      processBuilder.directory(new File(distPath.toString()));
      Process process = processBuilder.start();

      final InputStream isError = process.getErrorStream();
      final InputStream isOutput = process.getInputStream();

      Utils.processErrorAndOutPut(isError, isOutput, this.out);

      int exitStatus = process.waitFor();
      System.out.println("s2-init.bat exit status: " + exitStatus);
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

      final InputStream isError = process.getErrorStream();
      final InputStream isOutput = process.getInputStream();

      Utils.processErrorAndOutPut(isError, isOutput, this.out);

      int exitStatus = process.waitFor();
      return exitStatus;
    } catch (Exception e) {
      throw e;
    }

  }
  
public int initPL(Path distPath, String plname, String pluser, String projectName) throws Exception {
	  
	  try {
	  
	  File batchFile = new File(distPath.toString() + File.separator + DistConstants.INIT_PL_SCRIPT);
      ProcessBuilder processBuilder =
          new ProcessBuilder(batchFile.getAbsolutePath(), plname, pluser, projectName);
      processBuilder.redirectErrorStream(true);
      processBuilder.redirectOutput(Redirect.PIPE);

      processBuilder.directory(new File(distPath.toString()));
      Process process = processBuilder.start();

      final InputStream isError = process.getErrorStream();
      final InputStream isOutput = process.getInputStream();

      Utils.processErrorAndOutPut(isError, isOutput, this.out);

      int exitStatus = process.waitFor();
      System.out.println("s2-pl-init.bat exit status: " + exitStatus);
      return exitStatus;
    } catch (Exception e) {
      throw e;
    }
  }

}
