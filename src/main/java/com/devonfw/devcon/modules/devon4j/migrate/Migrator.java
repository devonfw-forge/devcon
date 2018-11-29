package com.devonfw.devcon.modules.devon4j.migrate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.devonfw.devcon.modules.devon4j.migrate.version.VersionDetector;
import com.devonfw.devcon.modules.devon4j.migrate.version.VersionIdentifier;
import com.devonfw.devcon.output.Output;

/**
 * Implementation of {@link Migration} that combines all {@link MigrationStep}s.
 */
public class Migrator implements Migration {

  private final Output output;

  private final VersionDetector versionDetector;

  private final Map<VersionIdentifier, MigrationStep> fromVersion2MigrationStepMap;

  /**
   * The constructor.
   *
   * @param output the {@link Output}.
   * @param versionDetector the {@link VersionDetector}.
   * @param steps the {@link MigrationStep}s.
   */
  public Migrator(Output output, VersionDetector versionDetector, MigrationStep... steps) {

    super();
    this.output = output;
    this.versionDetector = versionDetector;
    this.fromVersion2MigrationStepMap = new HashMap<>();
    for (MigrationStep step : steps) {
      this.fromVersion2MigrationStepMap.put(step.getFrom(), step);
    }
  }

  @Override
  public void migrate(File projectFolder) throws Exception {

    migrate(projectFolder, false);
  }

  /**
   * @param projectFolder the {@link File} (or directory) to migrate.
   * @param singleStep - {@code true} to only migrate to the next version, {@code false} otherwise (migrate to latest
   *        version).
   * @throws Exception on error.
   */
  public void migrate(File projectFolder, boolean singleStep) throws Exception {

    VersionIdentifier startVersion;
    try {
      startVersion = this.versionDetector.detectVersion(projectFolder);
    } catch (Exception e1) {
      this.output.showError(e1.getMessage());
      e1.printStackTrace();
      return;
    }
    int migrations = 0;
    VersionIdentifier version = startVersion;
    while (true) {
      MigrationStep step = this.fromVersion2MigrationStepMap.get(version);
      if (step == null) {
        complete(migrations, startVersion, version);
        return;
      } else {
        try {
          this.output.showMessage("***********************************************************");
          this.output.showMessage("Migrating from version %s to %s ...", step.getFrom().toString(),
              step.getTo().toString());
          this.output.showMessage("***********************************************************");
          step.migrate(projectFolder);
          if (singleStep) {
            return;
          }
          migrations++;
        } catch (Exception e) {
          this.output.showError("Migration from %s to %s failed: %s", step.getFrom().toString(),
              step.getTo().toString(), e.getMessage());
          e.printStackTrace();
        }
        version = step.getTo();
      }
    }
  }

  /**
   * @param migrations number of migrations that have been applied.
   * @param startVersion the initial {@link VersionIdentifier} before the migration.
   * @param endVersion the final {@link VersionIdentifier} after the migration.
   */
  private void complete(int migrations, VersionIdentifier startVersion, VersionIdentifier endVersion) {

    if (migrations == 0) {
      this.output.showError("Project is already on version %s. No migrations available to update.",
          startVersion.toString());
    } else {
      this.output.showMessage("***********************************************************");
      this.output.showMessage("Successfully applied %s migrations to migrate project from version %s to %s.",
          Integer.toString(migrations), startVersion.toString(), endVersion.toString());
      this.output.showMessage("***********************************************************");
    }
  }

}
