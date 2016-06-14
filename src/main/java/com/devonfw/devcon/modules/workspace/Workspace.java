package com.devonfw.devcon.modules.workspace;

import java.io.File;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.ParameterType;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.impl.AbstractCommandModule;

/**
 * TODO ssarmoka This class contains command to generate a new workspace with default configuration.
 *
 * @author ssarmoka
 */
@CmdModuleRegistry(name = "workspace", description = "Module to create a new workspace with all default configuration", deprecated = false)
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
  @Command(name = "create", help = "This command is used to create new workspace with all default configuration")
  @Parameters(values = { @Parameter(name = "foldername", description = "This is the name of workspace to create"),
  @Parameter(name = "devonpath", description = "This is the location of devon distribution", parametertype = ParameterType.OptionalFromConfig) })
  public void create(String foldername, String devonpath) throws Exception {

    String workspace_path = devonpath + File.separator + "workspaces" + File.separator + foldername;
    getOutput().status("creating workspace at path " + workspace_path);
    File folder = new File(workspace_path);
    if (!folder.exists()) {
      folder.mkdirs();
      Runtime rt = Runtime.getRuntime();
      Process process = null;
      try {
        process = rt.exec(devonpath + File.separator + "update-all-workspaces.bat");
        process.waitFor();
      } catch (Exception e) {
        this.output.status("[LOG]" + e.getMessage());
        throw e;
      }
      getOutput().status("Workspace created successfully");
    } else {
      getOutput().status("This workspace already exists!");
    }
  }
}
