package com.devonfw.devcon.modules.devon4j.migrate.version;

import java.io.File;
import java.io.IOException;

/**
 * TODO hohwille This type ...
 *
 * @since 1.5.0
 */
public class MavenVersionDetector implements VersionDetector {

  @Override
  public VersionIdentifier detectVersion(File projectFolder) throws IOException {

    return VersionIdentifier.ofOasp4j("2.6.0");
  }

}
