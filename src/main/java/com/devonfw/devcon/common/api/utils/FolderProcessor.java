package com.devonfw.devcon.common.api.utils;

import java.nio.file.Path;

import com.devonfw.devcon.common.utils.TreeClimber;

/**
 * This interface is used to implement a class whose method {@link FolderProcessor#onFolder(Path)} is called by
 * {@link TreeClimber} to process each path it encounters as it "climbs" towards the root of its path
 *
 * @author ivanderk
 */
public interface FolderProcessor {

  /**
   *
   * @param path current folder path
   * @return whether to continue climbing up to the tree root
   */
  boolean onFolder(Path path);
}
