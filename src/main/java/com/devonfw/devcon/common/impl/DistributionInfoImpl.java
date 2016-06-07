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
