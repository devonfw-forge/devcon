package com.devonfw.devcon.common.impl.utils;

import java.nio.file.Path;

import com.devonfw.devcon.common.api.utils.FolderProcessor;

/**
 * Implementation of {@link FolderProcessor} which determines whether a particular folder or any of its parent folders
 * contain a devon.json project settings file.
 *
 * @author ivanderk
 */
public class ProjectFolderProcessor extends AbstractBaseFolderProcessor {

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