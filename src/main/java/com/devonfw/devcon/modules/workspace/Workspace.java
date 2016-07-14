package com.devonfw.devcon.modules.workspace;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.DistributionInfo;
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
  @Command(name = "create", description = "This command is used to create new workspace with all default configuration")
  @Parameters(values = { @Parameter(name = "workspace", description = "This is the name of workspace to create"),
  @Parameter(name = "distribution", description = "This is the location of the devon distribution (default: from current dir)", optional = true) })
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

      if (!w.exists()) {
        w.mkdirs();
        String noPause = "noPause";
        ProcessBuilder processBuilder = new ProcessBuilder(
            distPath.toFile().getAbsolutePath() + File.separator + Constants.UPDATE_ALL_WORKSPACES_BAT, noPause);
        processBuilder.directory(distPath.toFile());

        Process process = processBuilder.start();

        final InputStream isError = process.getErrorStream();
        final InputStream isOutput = process.getInputStream();

        Utils.processErrorAndOutPut(isError, isOutput);

      } else {
        getOutput().showError("This workspace already exists!");
      }
    } else {
      getOutput().showError("Not a Devon distribution");
    }
  }
}
