package com.devonfw.devcon.modules.dist;

import com.devonfw.devcon.common.api.data.DistributionType;

/**
 * Constants for Dist Module
 *
 * @author pparrado
 */
public final class DistConstants {
  /**
   * url of the repository where the distributions are located
   */
  public static final String REPOSITORY_URL = "https://coconet.capgemini.com";

  /**
   * oasp distribution file id
   */
  public static final String OAPS_FILE_ID = "frs49076";

  /**
   * devon distribution file id
   */
  public static final String DEVON_FILE_ID = "frs50508";

  /**
   * distribution type for oasp
   */
  public static final String OASP_IDE = DistributionType.OASPIDE.toString().toLowerCase();

  /**
   * distribution type for devon
   */
  public static final String DEVON_DIST = DistributionType.DevonDist.toString().toLowerCase();

  /**
   * script to create new workspace with checkout of svn repository
   */
  public static final String CREATE_SCRIPT = "s2-create.bat";

  /**
   * script to configure settings.xml for Shared Services
   */
  public static final String INIT_SCRIPT = "s2-init.bat";

}
