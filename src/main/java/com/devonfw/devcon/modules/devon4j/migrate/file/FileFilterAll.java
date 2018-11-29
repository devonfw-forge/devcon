package com.devonfw.devcon.modules.devon4j.migrate.file;

import java.io.File;
import java.io.FileFilter;

/**
 * {@link FileFilter} {@link #accept(File) accepting} no {@link File}.
 */
public class FileFilterAll implements FileFilter {

  /** Singleton instance. */
  public static final FileFilterAll INSTANCE = new FileFilterAll();

  @Override
  public boolean accept(File pathname) {

    return true;
  }

}
