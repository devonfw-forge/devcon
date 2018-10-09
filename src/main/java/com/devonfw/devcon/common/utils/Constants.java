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
package com.devonfw.devcon.common.utils;

import java.io.File;

/**
 * Class to encapsulate common constant values
 *
 * @author pparrado
 */
public final class Constants {
  /**
   * the package where the modules must be placed
   */
  public static final String MODULES_PACKAGE = "com.devonfw.devcon.modules";

  /**
   * the location for the Devcon modules
   */
  public static final String MODULES_LOCATION = "com.devonfw.devcon.modules.*";

  /**
   * the path to the folder where the resources are located
   */
  public static final String RESOURCES_PATH = "src/main/resources/";

  /**
   * The name of the global parameters file
   */
  public static final String GLOBAL_PARAMS_FILE = "globalParameters.json";

  /**
   * The name of the version parameters file
   */
  public static final String VERSION_PARAMS_FILE = "version.json";

  /**
   * The full path of the version parameters file
   */
  public static final String VERSION_PARAMS_FILE_FULL_PATH = "conf" + File.separator + VERSION_PARAMS_FILE;

  /**
   * Constant defined for .git directory
   */
  public static final String DOT_GIT = "\\.git";

  /**
   * Constant defined for the clone option
   */
  public static final String CLONE_OPTION = "clone";

  /**
   * Constant defined for git.exe
   */
  public static final String GIT_EXE = "\\git.exe";

  /**
   * Constant defined for the word https
   */
  public static final String HTTPS = "https://";

  /**
   * Constant defined for Colon
   */
  public static final String COLON = ":";

  /**
   * @ Symbol defined as constant
   */
  public static final String AT_THE_RATE = "@";

  /**
   * Devon Repo Url
   */
  public static final String DEVON_REPO_URL = "github.com/devonfw/devon.git";

  /**
   * DEVON4J Repo Url
   */
  public static final String DEVON4J_REPO_URL = "https://github.com/devonfw/devon4j.git";

  /**
   * BAT FILE TO UPDATE ALL WORKSPACES
   */
  public static final String UPDATE_ALL_WORKSPACES_BAT = "update-all-workspaces.bat";

  /**
   * Workspaces Folder
   */
  public static final String WORKSPACES = "workspaces";

  /**
   * DEVON TEMPLATE VERSION
   */
  public final static String DEVON_TEMPLATE_VERSION = "devon_template_version";

  /**
   * DEVON TEMPLATE LAST STABLE VERSION
   */
  public final static String DEVON_TEMPLATE_LAST_STABLE_VERSION = "2.5.0";

  /**
   * DEVON TEMPLATE GROUP ID
   */
  public final static String DEVON_TEMPLATE_GROUP_ID = "com.devonfw.java.templates";

  /**
   * DEVON ARTIFACT ID
   */
  public final static String DEVON_ARTIFACT_ID = "devon4j-template-server";

  /**
   *
   */
  public final static String DEVON4NG = "devon4ng";

  /**
   *
   */
  public final static String DEVON4J = "devon4j";

  /**
   *
   */
  public final static String BUILD = "build";

  /**
   *
   */
  public final static String RUN = "run";

  public final static String USER = "user";

  public final static String PASSWORD = "password";

  public final static String USERNAME = "username";

  public final static String TOMCAT = "tomcat";

  public final static String ROLES = "roles";

  public final static String ROLE = "Role";

  public final static String ROLE_NAME = "rolename";

  public final static String ALL_ROLES = "manager-script,admin-gui,manager-gui";

  public final static String MANAGER_SCRIPT = "manager-script";

  public final static String ADMIN_GUI = "admin-gui";

  public final static String MANAGER_GUI = "manager-gui";

  public final static String PROFILE = "profile";

  public final static String PLUGINS = "plugins";

  public final static String PLUGIN = "plugin";

  public final static String GROUP_ID = "groupId";

  public final static String ARTIFACT_ID = "artifactId";

  public final static String TOMCAT_SERVER = "TomcatServer";

  public final static String SERVERS = "servers";

  public final static String SERVER = "server";

  public final static String SETTING_FILE_PATH = File.separator + "conf" + File.separator + ".m2" + File.separator
      + "settings.xml";

  public final static String TOMCAT_USER_FILE_PATH = File.separator + "software" + File.separator + "tomcat"
      + File.separator + "conf" + "tomcat-users.xml";

  public final static String TOMCAT_START_UP_BAT_FILES = File.separator + "software" + File.separator + "tomcat"
      + File.separator + "bin";

  public final static String DEFAULT_PORT = "8080";

  /**
   * Devcon logo file name
   */
  public final static String DEVCON_LOGO = "Logo_Devcon-background.jpg";

  public final static String DEVCON_ICON = "Logo_Devcon-icon.png";

  /**
   * HEIGHT
   */
  public static final double HEIGHT = 25;

  /**
   * WIDTH
   */
  public static final double WIDTH = 180;

  /**
   * MANDATORY_FIELD
   */
  public static final String MANDATORY_FIELD = "* : Indicates mandatory field";

  /**
   * ASTRIKE
   */
  public static final String ASTRIKE = "*";

  /**
   * SELECT_PATH
   */
  public static final String SELECT_PATH = "Choose Directory";

  /**
   * CONSOLE_PROMPT_TEXT
   */
  public static final String CONSOLE_PROMPT_TEXT = "Console output here... ";

  /**
   * BACK
   */
  public static final String BACK = "back";

  /**
   * START
   */
  public static final String START = "start";

  /**
   * STANDARD_BUTTON_START_STYLE
   */
  public static final String STANDARD_BUTTON_START_STYLE = "-fx-background-color: #0099ff; -fx-text-fill: white;";

  /**
   * STANDARD_BUTTON_START_STYLE
   */
  public static final String HOVERED_BUTTON_START_STYLE = "-fx-background-color: #99d6ff; -fx-text-fill: black;";

  /**
   * Taskkill commands in windows
   */
  public static final String TASKKILL_PID_CMD = "taskkill /F /PID ";

  /**
   * Script (JS) engine name
   */
  public static final String SCRIPT_ENGINE_NAME = "nashorn";

  /**
   *
   */
  public static final String WINDOWS_CMD_PROMPT = "cmd /c ";

  /**
   *
   */

  public static final String LINUX_BASH = "/bin/bash";

  public static final String DEVCON_SCRIPT = "devconScript.sh";

  public static final String UPDATE_ALL_WORKSPACES_SH = "update-all-workspaces.sh";
}
