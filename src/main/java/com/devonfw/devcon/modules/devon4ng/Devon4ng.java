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
package com.devonfw.devcon.modules.devon4ng;

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
 * Module with tasks related to devon4ng (Angular client)
 *
 * @author pparrado
 */

@CmdModuleRegistry(name = "devon4ng", description = "Module to automate tasks related to devon4ng")
public class Devon4ng extends AbstractCommandModule {

  private static String[] STATE = { "successfully", "failed" };

  private static String NG_NEW = " ng new ";

  private static String NG_BUILD = " ng build --progress false";

  private static String NG_SERVE = " ng serve --progress false";

  @Command(name = "create", description = "This command creates a basic Devon4ng app")
  @Parameters(values = { @Parameter(name = "clientname", description = "The name for the project"),
  @Parameter(name = "clientpath", description = "The location for the new project", optional = true, inputType = @InputType(name = InputTypeNames.PATH)) })
  public void create(String clientname, String clientpath) {

    getOutput().showMessage("Creating project " + clientname + "...");

    try {

      Optional<DistributionInfo> distInfo = getContextPathInfo().getDistributionRoot();
      clientpath = clientpath.isEmpty() ? this.contextPathInfo.getCurrentWorkingDirectory().toString() : clientpath;

      if (distInfo.isPresent()) {

        String projectPath = clientpath + File.separator + clientname;
        File projectFile = new File(projectPath);

        if (projectFile.exists()) {
          getOutput()
              .showError("The project " + projectPath + " already exists. Please delete it or choose other location.");
        } else {
          Process process = null;

          if (SystemUtils.IS_OS_WINDOWS) {

            process = Runtime.getRuntime().exec(Constants.WINDOWS_CMD_PROMPT + NG_NEW + clientname, null,
                new File(clientpath));

          } else if (SystemUtils.IS_OS_LINUX) {

            String args[] = new String[] { Constants.LINUX_BASH, "-c", NG_NEW + clientname };
            process = Runtime.getRuntime().exec(args, null, new File(clientpath));

          }

          String line;
          BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
          while ((line = in.readLine()) != null) {
            getOutput().showMessage(line);
          }
          in.close();
          int result = process.waitFor();
          if (result == 0) {
            getOutput().showMessage("Adding devon.json file...");
            Utils.addDevonJsonFile(projectFile.toPath(), ProjectType.DEVON4NG);
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

  @Command(name = "build", description = "This command will build the devon4ng project.", context = ContextType.PROJECT)
  public void build() {

    try {

      if (!this.projectInfo.isPresent()) {
        getOutput().showError("Not in a project or -path param not pointing to a project");
        return;
      }

      Process p;
      if (this.projectInfo.get().getProjecType().equals(ProjectType.DEVON4NG)) {

        Process process = null;

        if (SystemUtils.IS_OS_WINDOWS) {
          process = Runtime.getRuntime().exec(Constants.WINDOWS_CMD_PROMPT + NG_BUILD, null,
              this.projectInfo.get().getPath().toFile());

        } else if (SystemUtils.IS_OS_LINUX) {
          String args[] = new String[] { Constants.LINUX_BASH, "-c", "ng build --progress false" };
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
        int result = process.waitFor();

        getOutput().showMessage("Project build " + STATE[result]);

      } else {
        getOutput()
            .showError("Seems that you are not in a DEVON4NG project. Please verify the devon.json configuration file");
      }

    } catch (Exception e) {
      getOutput().showError("An error occured during execution of build command. " + e.getMessage());
    }
  }

  @Command(name = "run", description = "This command runs a debug build of devon4ng", context = ContextType.PROJECT)
  public void run() {

    try {
      if (!this.projectInfo.isPresent()) {
        getOutput().showError("Not in a project or -path param not pointing to a project");
        return;
      }

      if (this.projectInfo.isPresent()) {
        if (this.projectInfo.get().getProjecType().equals(ProjectType.DEVON4NG)) {

          Process process = null;

          if (SystemUtils.IS_OS_WINDOWS) {
            process = Runtime.getRuntime().exec(Constants.WINDOWS_CMD_PROMPT + NG_SERVE, null,
                this.projectInfo.get().getPath().toFile());

          } else if (SystemUtils.IS_OS_LINUX) {
            String args[] = new String[] { Constants.LINUX_BASH, "-c", "ng serve --progress false" };
            process = Runtime.getRuntime().exec(args, null, this.projectInfo.get().getPath().toFile());
          }

          getOutput().showMessage("Project starting...");
          String line;
          BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
          while ((line = in.readLine()) != null) {
            System.out.println(line);
            getOutput().showMessage(line);
          }
          in.close();
          int result = process.waitFor();
          if (result == 0) {
            getOutput().showMessage("Starting application");
          }

        } else {
          getOutput().showError(
              "Seems that you are not in a DEVON4NG project. Please verify the devon.json configuration file");
        }

      } else {
        getOutput().showError("devon.json configuration file not found.");
      }

    } catch (Exception e) {
      getOutput().showError("An error occured during execution of run command. " + e.getMessage());
    }
  }
}
