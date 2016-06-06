package com.devonfw.devcon.common.api.data;

import java.nio.file.Path;
import java.util.List;

import com.github.zafarkhaja.semver.Version;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public interface ProjectInfo {

  Path getPath();

  Version getVersion();

  ProjectType getProjecType();

  List<ProjectInfo> getSubProjects();
}
