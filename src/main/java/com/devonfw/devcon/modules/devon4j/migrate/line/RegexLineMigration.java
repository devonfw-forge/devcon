package com.devonfw.devcon.modules.devon4j.migrate.line;

import java.util.regex.Pattern;

/**
 * Implementation of {@link LineMigration} based on regex {@link Pattern} replacement.
 */
public class RegexLineMigration implements LineMigration {

  private final Pattern regex;

  private final String replacement;

  /**
   * The constructor.
   * 
   * @param regex the {@link Pattern} to match.
   * @param replacement the replacement.
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
