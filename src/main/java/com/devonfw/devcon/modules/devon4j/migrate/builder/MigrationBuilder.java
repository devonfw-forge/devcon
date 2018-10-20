package com.devonfw.devcon.modules.devon4j.migrate.builder;

import java.util.ArrayList;
import java.util.List;

import com.devonfw.devcon.modules.devon4j.migrate.MigrationStep;
import com.devonfw.devcon.modules.devon4j.migrate.Migrator;
import com.devonfw.devcon.modules.devon4j.migrate.version.MavenVersionDetector;
import com.devonfw.devcon.modules.devon4j.migrate.version.VersionDetector;
import com.devonfw.devcon.modules.devon4j.migrate.version.VersionIdentifier;
import com.devonfw.devcon.output.Output;

/**
 * TODO hohwille This type ...
 *
 * @since 1.5.0
 */
public class MigrationBuilder {

  final Output output;

  private VersionIdentifier version;

  final List<MigrationStep> steps;

  /**
   * The constructor.
   *
   * @param from
   * @param steps
   */
  public MigrationBuilder(Output output, VersionIdentifier from) {

    super();
    this.output = output;
    this.version = from;
    this.steps = new ArrayList<>();
  }

  public MigrationStepBuilder to(VersionIdentifier to) {

    VersionIdentifier from = this.version;
    this.version = to;
    return new MigrationStepBuilder(this, from, to);
  }

  public Migrator build() {

    VersionDetector versionDetector = new MavenVersionDetector();
    return new Migrator(this.output, versionDetector, this.steps.toArray(new MigrationStep[this.steps.size()]));
  }

  public static MigrationBuilder from(VersionIdentifier version, Output output) {

    return new MigrationBuilder(output, version);
  }

}
