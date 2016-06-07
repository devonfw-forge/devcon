package com.devonfw.devcon.common.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.common.api.utils.FolderIterceptor;
import com.github.zafarkhaja.semver.Version;

/**
 * Implementation of {@link FolderIterceptor}
 *
 * @author ivanderk
 */
@SuppressWarnings("javadoc")
public class ProjectInfoImpl implements ProjectInfo {

  private Path path;

  private ProjectType projectType;

  private Version version;

  private List<ProjectInfo> subProjects;

  public ProjectInfoImpl(Path path, ProjectType projectType, Version version) {
    this.path = path;
    this.projectType = projectType;
    this.version = version;
    this.subProjects = new ArrayList<>();
  }

  public ProjectInfoImpl(Path path, ProjectType projectType, Version version, List<ProjectInfo> subProjects) {
    this(path, projectType, version);
    this.subProjects = subProjects;
  }

  @Override
  public Path getPath() {

    return this.path;
  }

  @Override
  public ProjectType getProjecType() {

    return this.projectType;
  }

  @Override
  public Version getVersion() {

    return this.version;
  }

  @Override
  public List<ProjectInfo> getSubProjects() {

    return this.subProjects;
  }

}
