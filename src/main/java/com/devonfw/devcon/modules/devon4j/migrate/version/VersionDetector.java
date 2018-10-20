package com.devonfw.devcon.modules.devon4j.migrate.version;

import java.io.File;

/**
 * Detector that can determine the current {@link VersionIdentifier} of an existing project.
 */
public interface VersionDetector {

  /**
   * @param projectFolder the {@link File} pointing to the project to migrate.
   * @return the detected {@link VersionIdentifier}.
   * @throws Exception on error.
   */
  VersionIdentifier detectVersion(File projectFolder) throws Exception;

}
