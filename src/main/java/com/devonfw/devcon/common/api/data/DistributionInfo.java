package com.devonfw.devcon.common.api.data;

import java.nio.file.Path;

import com.github.zafarkhaja.semver.Version;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public interface DistributionInfo {

  Path getPath();

  Version getVersion();

  DistributionType getDistributionType();
}
