package com.devonfw.devcon.common.impl.utils;

import java.nio.file.Path;

import com.devonfw.devcon.common.api.utils.FolderProcessor;

/**
 * Implementation of {@link FolderProcessor} which determines whether a particular folder or any of its parent folders
 * contain a Devon Distribution or OASP IDE.
 *
 * @author ivanderk
 */
public class DistributionFolderProcessor extends AbstractBaseFolderProcessor {

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
