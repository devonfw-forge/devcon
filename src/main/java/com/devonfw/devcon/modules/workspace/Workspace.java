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
package com.devonfw.devcon.modules.workspace;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.commons.lang3.SystemUtils;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.InputType;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.InputTypeNames;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.common.utils.Utils;
import com.google.common.base.Optional;

/**
 * This class contains command to generate a new workspace with default configuration.
 *
 * @author ssarmoka
 */
@CmdModuleRegistry(name = "workspace", description = "Module to create a new workspace with all default configuration")
public class Workspace extends AbstractCommandModule {

  /**
   * The constructor.
   */
  public Workspace() {

    super();
  }

  /**
   * This command allow to create a new workspace with default configuration.
   *
   * @param distribution Path to Devon Distribution
   * @param workspace Name of the workspace folder
   * @throws Exception Exception thrown by workspace create command
   */
  @Command(name = "create", description = "This command creates a new workspace with all default configuration in a Devonfw distribution.")
  @Parameters(values = { @Parameter(name = "workspace", description = "This is the name of workspace to create"),
  @Parameter(name = "distribution", description = "This is the location of the devon distribution (default: from current dir)", optional = true, inputType = @InputType(name = InputTypeNames.PATH)) })
  public void create(String workspace, String distribution) throws Exception {

    Optional<DistributionInfo> distInfo;
    if (distribution.isEmpty()) {
      distInfo = getContextPathInfo().getDistributionRoot();
    } else {
      distInfo = getContextPathInfo().getDistributionRoot(distribution);
    }

    if (distInfo.isPresent()) {
      Path distPath = distInfo.get().getPath();

      File w = new File(distPath + File.separator + Constants.WORKSPACES + File.separator + workspace);
      ProcessBuilder processBuilder = null;
      if (!w.exists()) {
        w.mkdirs();
        String noPause = "noPause";
        if (SystemUtils.IS_OS_WINDOWS) {
          processBuilder = new ProcessBuilder(
              distPath.toFile().getAbsolutePath() + File.separator + Constants.UPDATE_ALL_WORKSPACES_BAT, noPause);
          processBuilder.directory(distPath.toFile());

        } else if (SystemUtils.IS_OS_LINUX) {
          String args[] = new String[] { Constants.LINUX_BASH, "-c",
          ". " + distPath.toFile().getAbsolutePath() + File.separator + Constants.UPDATE_ALL_WORKSPACES_SH, noPause };
          processBuilder = new ProcessBuilder(args);
          processBuilder.directory(distPath.toFile());

        }

        Process process = processBuilder.start();

        final InputStream isError = process.getErrorStream();
        final InputStream isOutput = process.getInputStream();

        Utils.processOutput(isError, isOutput, getOutput());

      } else {
        getOutput().showError("This workspace already exists!");
      }
    } else {
      getOutput().showError("Not a Devon distribution");
    }
  }
}
