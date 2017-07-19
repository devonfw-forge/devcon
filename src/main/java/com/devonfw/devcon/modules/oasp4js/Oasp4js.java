package com.devonfw.devcon.modules.oasp4js;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.commons.lang3.SystemUtils;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.InputType;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.InputTypeNames;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.common.utils.Utils;
import com.google.common.base.Optional;

/**
 * Module with tasks related to oasp4js (Angular client)
 *
 * @author pparrado
 */

@CmdModuleRegistry(name = "oasp4js", description = "Module to automate tasks related to oasp4js")
public class Oasp4js extends AbstractCommandModule {

  private static String[] STATE = { "successfully", "failed" };

  private static String NG_NEW = " ng new ";

  private static String NG_BUILD = " ng build --progress false ";

  private static String NG_SERVE = " ng serve --progress false ";

  @Command(name = "create", description = "This command creates a basic Oasp4js app")
  @Parameters(values = { @Parameter(name = "clientname", description = "The name for the project"),
  @Parameter(name = "clientpath", description = "The location for the new project", optional = true, inputType = @InputType(name = InputTypeNames.PATH)) })
  public void create(String clientname, String clientpath) {

    getOutput().showMessage("Creating project " + clientname + "...");

    try {
      getOutput().showMessage("1.CLIENTPATH:" + clientpath);
      Optional<DistributionInfo> distInfo = getContextPathInfo().getDistributionRoot();
      getOutput().showMessage("CURRENTWORKINGDIR:" + this.contextPathInfo.getCurrentWorkingDirectory().toString());
      clientpath = clientpath.isEmpty() ? this.contextPathInfo.getCurrentWorkingDirectory().toString() : clientpath;
      getOutput().showMessage("2.CLIENTPATH:" + clientpath);
      if (distInfo.isPresent()) {

        String projectPath = clientpath + File.separator + clientname;
        getOutput().showMessage("1.PROJECTPATH:" + projectPath);
        File projectFile = new File(projectPath);

        if (projectFile.exists()) {
          getOutput()
              .showError("The project " + projectPath + " already exists. Please delete it or choose other location.");
        } else {
          getOutput().showMessage("3.CLIENTPATH:" + clientpath);
          Process process = null;

          if (SystemUtils.IS_OS_WINDOWS) {

            process = Runtime.getRuntime().exec(Constants.WINDOWS_CMD_PROMPT + NG_NEW + clientname, null,
                new File(clientpath));

          } else if (SystemUtils.IS_OS_LINUX) {
            getOutput().showMessage("Linux OS");
            String args[] = new String[] { Constants.LINUX_BASH, "-c", NG_NEW, clientname };
            for (String string : args) {
              getOutput().showMessage(string);
            }
            getOutput().showMessage("4.CLIENTPATH: ", clientpath);
            getOutput().showMessage("2.PROJECTPATH: ", projectPath);

            process = Runtime.getRuntime().exec(args, null, new File(clientpath));
          }

          String line;
          BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
          while ((line = in.readLine()) != null) {
            getOutput().showMessage(line);
          }
          in.close();
          int result = process.exitValue();
          if (result == 0) {
            getOutput().showMessage("Adding devon.json file...");
            Utils.addDevonJsonFile(projectFile.toPath(), ProjectType.OASP4JS);
          }

          getOutput().showMessage("Project create " + STATE[result]);

        }
      } else {
        getOutput().showError("Seems that you are not in a Devon distribution.");
      }

    } catch (Exception e) {
      getOutput().showError("An error occured during execution of create command. " + e.getMessage());
    }
  }

  @Command(name = "build", description = "This command will build the oasp4js project.", context = ContextType.PROJECT)
  public void build() {

    try {

      if (!this.projectInfo.isPresent()) {
        getOutput().showError("Not in a project or -path param not pointing to a project");
        return;
      }

      Process p;
      if (this.projectInfo.get().getProjecType().equals(ProjectType.OASP4JS)) {
        try {

          Process process = null;

          if (SystemUtils.IS_OS_WINDOWS) {
            process = Runtime.getRuntime().exec(Constants.WINDOWS_CMD_PROMPT + NG_BUILD, null,
                this.projectInfo.get().getPath().toFile());

          } else if (SystemUtils.IS_OS_LINUX) {
            String args[] = new String[] { Constants.LINUX_BASH, "-c", NG_BUILD };
            process = Runtime.getRuntime().exec(args, null, this.projectInfo.get().getPath().toFile());
          }

          getOutput().showMessage("Building project...");
          String line;
          BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
          while ((line = in.readLine()) != null) {
            System.out.println(line);
            getOutput().showMessage(line);
          }
          in.close();
          int result = process.exitValue();

          getOutput().showMessage("Project build " + STATE[result]);

        } catch (Exception e) {

          getOutput().showError(
              "Seems that you are not in a OASP4JS project. Please verify the devon.json configuration file");
        }
      }

    } catch (Exception e) {
      getOutput().showError("An error occured during execution of build command. " + e.getMessage());
    }
  }

  @Command(name = "run", description = "This command runs a debug build of oasp4js", context = ContextType.PROJECT)
  public void run() {

    try {
      if (!this.projectInfo.isPresent()) {
        getOutput().showError("Not in a project or -path param not pointing to a project");
        return;
      }

      if (this.projectInfo.isPresent()) {
        if (this.projectInfo.get().getProjecType().equals(ProjectType.OASP4JS)) {

          Process process = null;

          if (SystemUtils.IS_OS_WINDOWS) {
            process = Runtime.getRuntime().exec(Constants.WINDOWS_CMD_PROMPT + NG_SERVE, null,
                this.projectInfo.get().getPath().toFile());

          } else if (SystemUtils.IS_OS_LINUX) {
            String args[] = new String[] { Constants.LINUX_BASH, "-c", NG_SERVE };
            process = Runtime.getRuntime().exec(args, null, this.projectInfo.get().getPath().toFile());
          }

          getOutput().showMessage("Project starting");
          String line;
          BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
          while ((line = in.readLine()) != null) {
            System.out.println(line);
            getOutput().showMessage(line);
          }
          in.close();
          process.waitFor();
          getOutput().showMessage("Starting application");
        } else {
          getOutput().showError(
              "Seems that you are not in a OASP4JS project. Please verify the devon.json configuration file");
        }

      } else {
        getOutput().showError("devon.json configuration file not found.");
      }

    } catch (Exception e) {
      getOutput().showError("An error occured during execution of run command. " + e.getMessage());
    }
  }
}
