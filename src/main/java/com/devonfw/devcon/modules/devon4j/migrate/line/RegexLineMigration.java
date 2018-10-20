package com.devonfw.devcon.modules.devon4j.migrate.line;

import java.util.regex.Pattern;

/**
 * TODO hohwille This type ...
 *
 * @since 1.5.0
 */
public class RegexLineMigration implements LineMigration {

  private final Pattern regex;

  private final String replacement;

  /**
   * The constructor.
   */
  public RegexLineMigration(Pattern regex, String replacement) {

    super();
    this.regex = regex;
    this.replacement = replacement;
  }

  @Override
  public String migrateLine(String line) {

    return this.regex.matcher(line).replaceAll(this.replacement);
  }

}
