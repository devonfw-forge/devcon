package com.devonfw.devcon.modules.sencha;

import org.apache.commons.lang3.SystemUtils;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.google.common.base.Optional;

/**
 *
 * @author ivanderk
 */
@CmdModuleRegistry(name = "sencha", description = "Sencha related commands")
public class Sencha extends AbstractCommandModule {

  private static String SENCHA_APP_WATCH = "cmd /c start sencha app watch";

  @SuppressWarnings("javadoc")
  @Command(name = "run", help = "compiles in DEBUG mode and then runs the internal Sencha web server (\"app watch\")", context = ContextType.PROJECT)
  @Parameters(values = { @Parameter(name = "port", description = "", optional = true) })
  public void run(String port) {

    // TODO ivanderk Implementatin for MacOSX & Unix
    if (!SystemUtils.IS_OS_WINDOWS) {
      getOutput().showMessage("This task is currently only supported on Windows");
      return;
    }
    Optional<ProjectInfo> project = getProjectInfo();
    if (project.isPresent() && project.get().getProjecType().equals(ProjectType.DEVON4SENCHA)) {

      getOutput().showMessage("Sencha starting");

      Process p;
      try {
        String cmd;
        if (port.isEmpty()) {
          cmd = SENCHA_APP_WATCH;
        } else {
          cmd = SENCHA_APP_WATCH; // TODO find proper port config + " --port " + port;
        }
        p = Runtime.getRuntime().exec(cmd, null, project.get().getPath().toFile());
        p.waitFor();
      } catch (Exception e) {

        getOutput().showError("An error occured during executing Sencha Cmd");
      }

    } else {
      getOutput().showMessage("Not a Sencha project (or does not have a corresponding devon.json file)");
    }
  }
}
