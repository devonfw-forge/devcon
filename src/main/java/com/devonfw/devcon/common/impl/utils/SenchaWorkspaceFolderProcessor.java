package com.devonfw.devcon.common.impl.utils;

import java.nio.file.Path;

import com.devonfw.devcon.common.api.utils.FolderProcessor;

/**
 * Implementation of {@link FolderProcessor} which determines whether a particular folder or any of its parent folders
 * contain a Sencha Workspace folder.
 *
 * @author ivanderk
 */
public class SenchaWorkspaceFolderProcessor extends AbstractBaseFolderProcessor {

  @Override
  public boolean onFolder(Path path) {

    if (path.resolve(".sencha/workspace").toFile().exists() && path.resolve("workspace.json").toFile().exists()
        && path.resolve("StarterTemplate").toFile().exists()) {
      this.foundPath = path;
      // found, cancel further climbing
      this.found = true;
      return false;
    }
    // continue
    return true;
  }
}
