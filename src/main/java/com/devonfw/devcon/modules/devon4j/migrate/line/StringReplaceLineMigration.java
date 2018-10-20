package com.devonfw.devcon.modules.devon4j.migrate.line;

/**
 * TODO hohwille This type ...
 *
 * @since 1.5.0
 */
public class StringReplaceLineMigration implements LineMigration {

  private final String search;

  private final String replacement;

  /**
   * The constructor.
   * 
   * @param search
   * @param replacement
   */
  public StringReplaceLineMigration(String search, String replacement) {

    super();
    this.search = search;
    this.replacement = replacement;
  }

  @Override
  public String migrateLine(String line) {

    return line.replace(this.search, this.replacement);
  }

}
