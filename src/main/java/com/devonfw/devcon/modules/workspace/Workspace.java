package com.devonfw.devcon.modules.workspace;

import java.io.File;
import java.nio.file.Path;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.google.common.base.Optional;

/**
 * This class contains command to generate a new workspace with default configuration.
 *
 * @author ssarmoka
 */
@CmdModuleRegistry(name = "workspace", description = "Module to create a new workspace with all default configuration")
public class Workspace extends AbstractCommandModule {

  public Workspace() {
    super();
  }

  /**
   * This command allow to create a new workspace with default configuration.
   *
   * @param devonpath
   * @param foldername
   * @throws Exception
   */
  @Command(name = "create", description = "This command is used to create new workspace with all default configuration")
  @Parameters(values = { @Parameter(name = "workspace", description = "This is the name of workspace to create"),
  @Parameter(name = "distribution", description = "This is the location of the devon distribution (default: from current dir)", optional = true) })
  public void create(String workspace, String distribution) throws Exception {

    Optional<DistributionInfo> distInfo = getContextPathInfo().getDistributionRoot(distribution);
    if (distInfo.isPresent()) {
      Path distPath = distInfo.get().getPath();
      Path workspacePath = distPath.resolve("workspaces" + File.separator + workspace);
      if (!workspacePath.toFile().exists()) {

        workspacePath.toFile().mkdir();
        Runtime rt = Runtime.getRuntime();
        Process process = null;

        try {
          process = rt.exec(distPath.resolve("update-all-workspaces.bat").toString());
          process.waitFor();
        } catch (Exception e) {
          this.output.showError("Errr creating workspace: " + e.getMessage());
        }

      } else {
        getOutput().showError("This workspace already exists!");
      }
    } else {
      getOutput().showError("Not a Devon distribution");
    }
  }
}
