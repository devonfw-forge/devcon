/*******************************************************************************
 * Copyright 2015-2018 Capgemini SE.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package com.devonfw.devcon.common.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.common.api.utils.FolderProcessor;
import com.github.zafarkhaja.semver.Version;

/**
 * Implementation of {@link FolderProcessor}
 *
 * @author ivanderk
 */
@SuppressWarnings("javadoc")
public class ProjectInfoImpl implements ProjectInfo {

  private Path path;

  private ProjectType projectType;

  private Version version;

  private List<ProjectInfo> subProjects;

  private JSONObject config;

  public ProjectInfoImpl(Path path, ProjectType projectType, Version version, JSONObject config) {
    this.path = path;
    this.projectType = projectType;
    this.version = version;
    this.config = config;
    this.subProjects = new ArrayList<>();
  }

  public ProjectInfoImpl(Path path, ProjectType projectType, Version version, JSONObject config,
      List<ProjectInfo> subProjects) {
    this(path, projectType, version, config);
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

  @Override
  public JSONObject getConfig() {

    // TODO Auto-generated method stub
    return this.config;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getProperty(String name, Object defaultValue) {

    if (this.config.containsKey(name)) {
      return this.config.get(name);
    } else {
      return defaultValue;
    }
  }

}
