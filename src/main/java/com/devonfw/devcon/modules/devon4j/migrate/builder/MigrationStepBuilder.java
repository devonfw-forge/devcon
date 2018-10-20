package com.devonfw.devcon.modules.devon4j.migrate.builder;

import com.devonfw.devcon.modules.devon4j.migrate.MigrationStepImpl;
import com.devonfw.devcon.modules.devon4j.migrate.file.TextFileMigration;
import com.devonfw.devcon.modules.devon4j.migrate.version.VersionIdentifier;

/**
 * TODO hohwille This type ...
 *
 * @since 1.5.0
 */
public class MigrationStepBuilder {

  private final MigrationBuilder parent;

  final MigrationStepImpl step;

  /**
   * The constructor.
   *
   * @param parent
   */
  public MigrationStepBuilder(MigrationBuilder parent, VersionIdentifier from, VersionIdentifier to) {

    super();
    this.parent = parent;
    this.step = new MigrationStepImpl(this.parent.output, from, to);
  }

  public TextFileMigrationBuilder java() {

    return new TextFileMigrationBuilder(this, TextFileMigration.JAVA_PATTERN);
  }

  public TextFileMigrationBuilder applicationProperties() {

    return new TextFileMigrationBuilder(this, TextFileMigration.APPLICATION_PROPERTIES_PATTERN);
  }

  public PomXmlMigrationBuilder pom() {

    return new PomXmlMigrationBuilder(this);
  }

  public MigrationBuilder next() {

    assert (!this.step.getFileMigrations().isEmpty());
    this.parent.steps.add(this.step);
    return this.parent;
  }

}
