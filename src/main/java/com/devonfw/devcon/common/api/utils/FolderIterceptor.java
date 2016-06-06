package com.devonfw.devcon.common.api.utils;

import java.nio.file.Path;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public interface FolderIterceptor {

  /**
   *
   * @param path current folder path
   * @return whether to continue climbing up to the tree root
   */
  boolean onFolder(Path path);
}
