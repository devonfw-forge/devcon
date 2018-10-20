package com.devonfw.devcon.modules.devon4j.migrate.builder;

import java.util.regex.Pattern;

import com.devonfw.devcon.modules.devon4j.migrate.file.TextFileMigration;
import com.devonfw.devcon.modules.devon4j.migrate.line.RegexLineMigration;
import com.devonfw.devcon.modules.devon4j.migrate.line.StringReplaceLineMigration;

/**
 * Builder for textual file migrations.
 */
public class TextFileMigrationBuilder {

  private final MigrationStepBuilder parent;

  private final TextFileMigration migration;

  /**
   * The constructor.
   *
   * @param parent the parent builder.
   * @param pattern the {@link Pattern} for the filename to match and migrate.
   */
  public TextFileMigrationBuilder(MigrationStepBuilder parent, Pattern pattern) {

    super();
    this.parent = parent;
    this.migration = new TextFileMigration(pattern);
  }

  /**
   * @param pattern the {@link Pattern} to match.
   * @param replacement the replacement for the given {@link Pattern}. May contain variable expressions (e.g. "$1") to
   *        reference regex groups.
   * @return {@code this}.
   */
  public TextFileMigrationBuilder replaceRegex(Pattern pattern, String replacement) {

    this.migration.getLineMigrations().add(new RegexLineMigration(pattern, replacement));
    return this;
  }

  /**
   * @param pattern the {@link Pattern} to match as {@link String}.
   * @param replacement the replacement for the given {@link Pattern}. May contain variable expressions (e.g. "$1") to
   *        reference regex groups.
   * @return {@code this}.
   */
  public TextFileMigrationBuilder replaceRegex(String pattern, String replacement) {

    this.migration.getLineMigrations().add(new RegexLineMigration(Pattern.compile(pattern), replacement));
    return this;
  }

  /**
   * @param search the plain {@link String} to search for.
   * @param replacement the replacement for the given {@code search} {@link String}.
   * @return {@code this}.
   */
  public TextFileMigrationBuilder replace(String search, String replacement) {

    this.migration.getLineMigrations().add(new StringReplaceLineMigration(search, replacement));
    return this;
  }

  /**
   * @return the parent builder after this migration is complete.
   */
  public MigrationStepBuilder and() {

    this.parent.step.getFileMigrations().add(this.migration);
    return this.parent;
  }
}
