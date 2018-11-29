package com.devonfw.devcon.modules.devon4j.migrate.builder;

import java.util.regex.Pattern;

import com.devonfw.devcon.modules.devon4j.migrate.file.XmlFileMigration;
import com.devonfw.devcon.modules.devon4j.migrate.xml.XmlStringReplacement;

/**
 * Builder for an {@link XmlFileMigration}.
 */
public class XmlMigrationBuilder {

  private final MigrationStepBuilder parent;

  /** The actual migration to build. */
  protected final XmlFileMigration migration;

  /**
   * The constructor.
   *
   * @param parent the parent builder.
   * @param pattern the {@link Pattern} for the filenames to match.
   */
  public XmlMigrationBuilder(MigrationStepBuilder parent, Pattern pattern) {

    super();
    this.parent = parent;
    this.migration = new XmlFileMigration(parent.parent.output, pattern);
  }

  /**
   * @param search the {@link String} to search for.
   * @param replacement the replacement for the given {@code search} {@link String}.
   * @return {@code this}.
   */
  public XmlMigrationBuilder replaceText(String search, String replacement) {

    this.migration.getMigrations().add(new XmlStringReplacement(search, replacement));
    return this;
  }

  /**
   * @return the parent builder after this XML migration is complete.
   */
  public MigrationStepBuilder and() {

    this.parent.step.getFileMigrations().add(this.migration);
    return this.parent;
  }

}
