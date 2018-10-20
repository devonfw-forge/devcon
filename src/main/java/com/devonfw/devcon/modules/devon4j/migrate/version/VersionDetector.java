package com.devonfw.devcon.modules.devon4j.migrate.version;

import java.io.File;
import java.io.IOException;

/**
 * Detector that can determine the current {@link VersionIdentifier} of an existing project.
 */
public interface VersionDetector {

  VersionIdentifier detectVersion(File projectFolder) throws IOException;

}
