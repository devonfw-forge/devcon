package com.devonfw.devcon.modules.devon4j.migrate.builder;

import java.util.regex.Pattern;

import com.devonfw.devcon.modules.devon4j.migrate.file.TextFileMigration;
import com.devonfw.devcon.modules.devon4j.migrate.line.RegexLineMigration;
import com.devonfw.devcon.modules.devon4j.migrate.line.StringReplaceLineMigration;

/**
 * TODO hohwille This type ...
 *
 * @since 1.5.0
 */
public class TextFileMigrationBuilder {

  private final MigrationStepBuilder parent;

  private final TextFileMigration migration;

  /**
   * The constructor.
   *
   * @param migrationStepBuilder
   * @param javaPattern
   */
  public TextFileMigrationBuilder(MigrationStepBuilder parent, Pattern pattern) {

    super();
    this.parent = parent;
    this.migration = new TextFileMigration(pattern);
  }

  public TextFileMigrationBuilder replaceRegex(Pattern pattern, String replacement) {

    this.migration.getLineMigrations().add(new RegexLineMigration(pattern, replacement));
    return this;
  }

  public TextFileMigrationBuilder replaceRegex(String pattern, String replacement) {

    this.migration.getLineMigrations().add(new RegexLineMigration(Pattern.compile(pattern), replacement));
    return this;
  }

  public TextFileMigrationBuilder replace(String search, String replacement) {

    this.migration.getLineMigrations().add(new StringReplaceLineMigration(search, replacement));
    return this;
  }

  public MigrationStepBuilder and() {

    this.parent.step.getFileMigrations().add(this.migration);
    return this.parent;
  }
}
