package com.devonfw.devcon.common.impl.utils;

import java.nio.file.Path;

import com.devonfw.devcon.common.api.utils.FolderIterceptor;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public class ProjectFolderInterceptor implements FolderIterceptor {

  /**
   * @return found
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
   * @return foundPath
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