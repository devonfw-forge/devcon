package com.devonfw.devcon.modules.devon4j.migrate;

import java.io.File;

/**
 * Interface for a migration
 */
public interface Migration {

  void migrate(File file) throws Exception;

}
