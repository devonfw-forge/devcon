package com.devonfw.devcon.common.impl.utils;

import java.nio.file.Path;

import com.devonfw.devcon.common.api.utils.FolderProcessor;

/**
 * Abstract base class for differnt FolderProcessors
 *
 * @author ivanderk
 */
public abstract class AbstractBaseFolderProcessor implements FolderProcessor {

  /**
   * @return found folder
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
   * @return foundPath path to folder
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

  protected boolean found = false;

  protected Path foundPath = null;

  @Override
  public abstract boolean onFolder(Path path);

}
