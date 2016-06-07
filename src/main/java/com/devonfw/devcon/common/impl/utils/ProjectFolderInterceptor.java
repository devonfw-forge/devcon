package com.devonfw.devcon.common.impl.utils;

import java.nio.file.Path;

import com.devonfw.devcon.common.api.utils.FolderIterceptor;

/**
 * Implementation of {@link FolderIterceptor} which determines whether a particular folder or any of its parent folders
 * contain a devon.json project settings file.
 *
 * @author ivanderk
 */
public class ProjectFolderInterceptor implements FolderIterceptor {

  /**
   * @return found devon.json file
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
   * @return foundPath path to devon.json settings file
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

    if (path.resolve("devon.json").toFile().exists()) {
      this.foundPath = path;
      // found, cancel further climbing
      this.found = true;
      return false;
    }
    // continue
    return true;
  }
}