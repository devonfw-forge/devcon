package com.devonfw.devcon.modules.devon4j.migrate;

import java.io.File;

/**
 * Interface for a migration.
 */
public interface Migration {

  /**
   * @param file the {@link File} (or directory) to migrate.
   * @throws Exception on error.
   */
  void migrate(File file) throws Exception;

}
