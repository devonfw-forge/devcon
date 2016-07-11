package com.devonfw.devcon.common.api.data;

import java.nio.file.Path;

import com.github.zafarkhaja.semver.Version;

/**
 * Contains information about OASP-IDE or Devon Distribution
 *
 * @author ivanderk
 */
public interface DistributionInfo {

  /**
   *
   * @return get Path of Distribution
   */
  Path getPath();

  /**
   *
   * @return get version of distribution as specified in the conf/settings.json file
   */
  Version getVersion();

  /**
   *
   * @return whether is OASP-IDE or Devon Distribution
   */
  DistributionType getDistributionType();
}
