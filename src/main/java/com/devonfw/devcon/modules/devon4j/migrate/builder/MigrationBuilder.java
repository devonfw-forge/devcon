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
 * Top-level builder to {@link #build() build} a {@link Migrator}.
 */
public class MigrationBuilder {

  final Output output;

  private VersionIdentifier version;

  final List<MigrationStep> steps;

  /**
   * The constructor.
   *
   * @param output the {@link Output}.
   * @param from the initial starting point from where the migration is supported.
   */
  public MigrationBuilder(Output output, VersionIdentifier from) {

    super();
    this.output = output;
    this.version = from;
    this.steps = new ArrayList<>();
  }

  /**
   * @param to the {@link VersionIdentifier} to migrate to.
   * @return the builder to configure the {@link MigrationStep} to migrate to that given {@link VersionIdentifier}.
   */
  public MigrationStepBuilder to(VersionIdentifier to) {

    VersionIdentifier from = this.version;
    this.version = to;
    return new MigrationStepBuilder(this, from, to);
  }

  /**
   * @return the build {@link Migrator}.
   */
  public Migrator build() {

    VersionDetector versionDetector = new MavenVersionDetector();
    return new Migrator(this.output, versionDetector, this.steps.toArray(new MigrationStep[this.steps.size()]));
  }

  /**
   * @param version the initial supported {@link VersionIdentifier}.
   * @param output the {@link Output}.
   * @return {@code this}.
   */
  public static MigrationBuilder from(VersionIdentifier version, Output output) {

    return new MigrationBuilder(output, version);
  }

}
