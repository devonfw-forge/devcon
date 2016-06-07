package com.devonfw.devcon.common.impl.utils;

import java.nio.file.Path;

import com.devonfw.devcon.common.api.utils.FolderProcessor;

/**
 * Implementation of {@link FolderProcessor} which determines whether a particular folder or any of its parent folders
 * contain a Devon Distribution or OASP IDE.
 *
 * @author ivanderk
 */
public class DistributionFolderProcessor implements FolderProcessor {

  /**
   * @return found Devon Distribution or OASP IDE config file
   */
  public boolean isFound() {

    return this.found;
  }

  /**
   * @param found new value of {@link #getfound}.
   */
  public void setFound(boolean found) {

    this.found = found;
  }

  /**
   * @return foundPath path to Devon Distribution or OASP IDE config file
   */
  public Path getFoundPath() {

    return this.foundPath;
  }

  /**
   * @param foundPath new value of {@link #getfoundPath}.
   */
  public void setFoundPath(Path foundPath) {

    this.foundPath = foundPath;
  }

  private boolean found = false;

  private Path foundPath = null;

  @Override
  public boolean onFolder(Path path) {

    Path settingsPath = path.resolve("conf/settings.json");
    if (settingsPath.toFile().exists() && path.resolve("workspaces").toFile().exists()) {
      this.foundPath = path;
      // found, cancel further climbing
      this.found = true;
      return false;
    }
    // continue
    return true;
  }
}
