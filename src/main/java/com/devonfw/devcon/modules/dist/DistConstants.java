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
  // public static final String REPOSITORY_URL = "https://coconet.capgemini.com";

  /* Added New Url for Download dist */
  public static final String REPOSITORY_URL = "http://de-mucevolve02/files/devonfw/current/";

  /**
   * devon distribution file id
   */
  public static final String DEVON_FILE_ID = "devonide_file_id";

  /**
   * devon distribution file id
   */
  public static final String DEVON_DIST_FILE_ID = "devondist_file_id";

  /**
   * devon distribution linux file id
   */
  public static final String DEVON_FILE_LINUX_ID = "devondist_file_linux_id";

  /**
   * distribution type for devon
   */
  public static final String DEVON_IDE = DistributionType.DEVONIDE.toString().toLowerCase();

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

  /**
   * script to configure settings.xml for Shared Services
   */
  public static final String INIT_PL_SCRIPT = "s2-pl-init.bat";

  /* Added 6 New fileds for Download Dist */
  /**
   * OS type for Distribution devon
   */
  public static final String DIST_TYPE_WINDOWS = "windows";

  /**
   * OS type for Distribution devon
   */
  public static final String DIST_TYPE_LINUX = "linux";

  /**
   * devon Distribution package for windows
   */
  public static final String WINDOWS_DIST_ZIP = "Devon-dist-current.zip";

  /**
   * devon Distribution package for linux
   */
  public static final String LINUX_DIST_ZIP = "Devon-dist-current-linux.zip";

  /**
   * devon Distribution zip for windows
   */
  public static final String DIST_FILENAME_WINDOWS = "Devon-dist_2.4.0.zip";

  /**
   * devon Distribution zip for linux
   */
  public static final String DIST_FILENAME_LINUX = "Devon-dist_2.4.0_Linux.zip";

}
