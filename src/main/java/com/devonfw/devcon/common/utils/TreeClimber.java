package com.devonfw.devcon.common.utils;

import java.nio.file.Path;

import com.devonfw.devcon.common.api.utils.FolderIterceptor;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public class TreeClimber {

  public static void climb(Path path, FolderIterceptor interceptor) {

    Path _path = path.toAbsolutePath();
    Path root = path.getRoot();

    while (interceptor.onFolder(_path)) {
      if (_path.equals(root)) {
        break;
      }
      _path = _path.getParent();
    }

  }
}
