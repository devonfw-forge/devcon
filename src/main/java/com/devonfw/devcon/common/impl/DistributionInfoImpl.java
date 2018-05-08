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

import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.DistributionType;
import com.github.zafarkhaja.semver.Version;

/**
 * Implementation of {@link DistributionInfo}
 *
 * @author ivanderk
 */
@SuppressWarnings("javadoc")
public class DistributionInfoImpl implements DistributionInfo {

  private Path path;

  private DistributionType distType;

  private Version version;

  public DistributionInfoImpl(Path path, DistributionType distType, Version version) {

    this.path = path;
    this.distType = distType;
    this.version = version;
  }

  @Override
  public Path getPath() {

    return this.path;
  }

  @Override
  public Version getVersion() {

    return this.version;
  }

  @Override
  public DistributionType getDistributionType() {

    return this.distType;
  }

}
