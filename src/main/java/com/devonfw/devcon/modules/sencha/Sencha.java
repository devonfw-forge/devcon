package com.devonfw.devcon.modules.sencha;

import java.io.IOException;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.common.impl.AbstractCommandHolder;
import com.google.common.base.Optional;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
@CmdModuleRegistry(name = "sencha", description = "Sencha related commands", context = "global", deprecated = false)
public class Sencha extends AbstractCommandHolder {

  @Command(name = "run", help = "compiles in DEBUG mode and then runs the internal Sencha web server (\"app watch\")")
  // @Parameters(values = { @Parameter(name = "name", description = "this is the name parameter") })
  @SuppressWarnings("javadoc")
  public void run() throws IOException {

    Optional<ProjectInfo> root = getContextPathInfo().getProjectRoot();
    if (root.isPresent() && root.get().getProjecType().equals(ProjectType.Devon4Sencha)) {
      getOutput().showMessage("Bom boom");
      ProjectInfo project = root.get();
      // project.getPath();

      // TODO add replace by new command here

      // Runtime.getRuntime().exec("sencha app watch", null, project.getPath().toFile());
      getOutput().showMessage("Euh?");

    } else {
      getOutput().showMessage("Not a Sencha project (or does not have a corresponding devon.json file)");
    }
  }
}
