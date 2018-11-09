package com.devonfw.devcon.modules.devon4j.migrate.file;

import java.io.File;
import java.util.regex.Pattern;

import com.devonfw.devcon.modules.devon4j.migrate.Migration;
import com.devonfw.devcon.output.Output;

/**
 * {@link Migration} for a single {@link File}.
 */
public abstract class FileMigration implements Migration {

  final Output output;

  private final Pattern namePattern;

  /**
   * The constructor.
   *
   * @param output the {@link Output}.
   * @param namePattern the {@link Pattern} used to match the {@link File#getName() filename} to apply this migration
   *        to.
   */
  public FileMigration(Output output, Pattern namePattern) {

    super();
    this.output = output;
    this.namePattern = namePattern;
  }

  @Override
  public final void migrate(File file) throws Exception {

    String name = file.getName();
    if (this.namePattern.matcher(name).matches()) {
      migrateFile(file);
    }
  }

  /**
   * @param file the matching {@link File} to migrate.
   * @throws Exception on error.
   */
  protected abstract void migrateFile(File file) throws Exception;

}
