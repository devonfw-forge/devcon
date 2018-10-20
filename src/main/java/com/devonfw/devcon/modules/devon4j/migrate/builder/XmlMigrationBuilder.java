package com.devonfw.devcon.modules.devon4j.migrate.builder;

import java.util.regex.Pattern;

import com.devonfw.devcon.modules.devon4j.migrate.file.XmlFileMigration;

/**
 * TODO hohwille This type ...
 *
 * @since 1.5.0
 */
public class XmlMigrationBuilder {

  private final MigrationStepBuilder parent;

  protected final XmlFileMigration migration;

  /**
   * The constructor.
   *
   * @param migrationStepBuilder
   * @param pomXmlPattern
   */
  public XmlMigrationBuilder(MigrationStepBuilder parent, Pattern pattern) {

    super();
    this.parent = parent;
    this.migration = new XmlFileMigration(pattern);
  }

  public MigrationStepBuilder and() {

    this.parent.step.getFileMigrations().add(this.migration);
    return this.parent;
  }

}
