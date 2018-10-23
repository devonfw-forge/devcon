package com.devonfw.devcon.modules.devon4j.migrate.line;

/**
 * Interface to migrate a single line of text (code, etc.)
 */
public interface LineMigration {

  /**
   * @param line the line of text to process.
   * @return the migrated line (may be the original or a modified one).
   */
  String migrateLine(String line);

}
