package com.devonfw.devcon.common.api.data;

import java.nio.file.Path;
import java.util.List;

import com.github.zafarkhaja.semver.Version;

/**
 * Contains information about the Devon project
 *
 * @author ivanderk
 */
public interface ProjectInfo {

  /**
   *
   * @return get Path of Project folder
   */
  Path getPath();

  /**
   *
   * @return get version as specified in the devon.json file (does NOT refer to version in particular project artifact
   *         itself)
   */
  Version getVersion();

  /**
   *
   * @return whether is Combined project, oasp4j, oasp4js or devon4secha
   */
  ProjectType getProjecType();

  /**
   * In case of projecType == Combined, contains subprojects as defined in the devon.json file
   * 
   * @return
   */
  List<ProjectInfo> getSubProjects();
}
