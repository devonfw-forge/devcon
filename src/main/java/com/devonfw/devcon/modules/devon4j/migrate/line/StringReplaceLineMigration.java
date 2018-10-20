package com.devonfw.devcon.modules.devon4j.migrate.line;

/**
 * Implementation of {@link LineMigration} for simple string replacement.
 */
public class StringReplaceLineMigration implements LineMigration {

  private final String search;

  private final String replacement;

  /**
   * The constructor.
   *
   * @param search the {@link String} to search for.
   * @param replacement the replacement for the {@code search} {@link String}.
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
