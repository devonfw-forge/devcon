package com.devonfw.devcon.common.api.utils;

import java.nio.file.Path;

import com.devonfw.devcon.common.utils.TreeClimber;

/**
 * This interface is used to implement a class which is called by {@link TreeClimber} for each path it encounters as it
 * "climbs" towards the root of its path
 *
 * @author ivanderk
 */
public interface FolderIterceptor {

  /**
   *
   * @param path current folder path
   * @return whether to continue climbing up to the tree root
   */
  boolean onFolder(Path path);
}
