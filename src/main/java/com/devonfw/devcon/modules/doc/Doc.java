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
package com.devonfw.devcon.modules.doc;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Utils;

/**
 * Documentation related commands
 *
 * @author ivanderk
 */

@CmdModuleRegistry(name = "doc", description = "Module with tasks related with obtaining specific documentation")
public class Doc extends AbstractCommandModule {

  /**
   *
   */
  private static final String DEVON_GET_STARTED = "https://troom.capgemini.com/sites/vcc/devon/getstarted.aspx";

  private static final String DEVON_SITE = "http://devonfw.com";
  // private static final String DEVON_TROOM_SITE = "https://troom.capgemini.com/sites/vcc/devon/index.aspx";

  private static final String DEVON_GUIDE = "https://github.com/devonfw/devon/wiki";

  private static final String DEVON4J_GUIDE = "https://github.com/devonfw/devon4j/wiki";

  private static final String DEVCON_USER_GUIDE = "https://github.com/devonfw/devon/wiki/devcon-user-guide";

  /**
   * The constructor.
   */
  public Doc() {

    super();
  }

  /**
   * This command shows the main devon web site
   *
   */
  @Command(name = "devon", description = "Opens the Devonfw site in the default web browser")
  public void devon() {

    openUrl(DEVON_SITE);
  }

  /**
   * This command shows the main devon web site
   *
   */
  @Command(name = "userguide", description = "Show the Devcon user guide")
  public void devconman() {

    openUrl(DEVCON_USER_GUIDE);
  }

  @SuppressWarnings("javadoc")
  @Command(name = "devonguide", description = "Opens the Devonfw Guide")
  public void devonguide() {

    openUrl(DEVON_GUIDE);
  }

  @SuppressWarnings("javadoc")
  @Command(name = "devon4jguide", description = "Opens the DEVON4J Guide")
  public void devon4jguide() {

    openUrl(DEVON4J_GUIDE);
  }

  @Command(name = "getstarted", description = "Opens the Devonfw \"Getting Started\" page")
  public void getstarted() {

    openUrl(DEVON_GET_STARTED);
  }

  @Command(name = "links", description = "Print Devonfw \"business card\" with info & links to the console")
  public void links() {

    String blurb = "devonfw is the CSD standard platform for Capgemini APPS2. It provides a\n"
        + "standardized architecture blueprint for web and Java-applications, an open\n"
        + "best-of-breed technology stack  as well as industry proven best practices and code\n"
        + "conventions. It is a industrialization initiative that is aligned across Capgemini APPS2.\n" + "\n"
        + "You can find more information at:\n" + "http://devonfw.com\n\n"
        + "You can also find more information at our internal site:\n"
        + "https://troom.capgemini.com/sites/vcc/devon/index.aspx\n" + "\n"
        + "View the video “iCSD devonfw overview” here:\n"
        + "http://talent.capgemini.com/fi/pages/the_way_we_work/tools/icsd_and_devon\n" + "\n"
        + "or get started right away!" + "https://troom.capgemini.com/sites/vcc/devon/getstarted.aspx\n"
        + "Feel free to  take a look at our Yammer group:\n"
        + "https://www.yammer.com/capgemini.com/#/threads/inGroup?type=in_group&feedId=5030942\n"
        + "and watch a demo of custom built devonfw applications:\n"
        + "http://devon-ci.cloudapp.net/troom/Webcast_Devon.mp4\n";

    this.output.showMessage(blurb);
  }

  private boolean openUrl(String url) {

    boolean res = Utils.openUri(url);
    if (!res) {
      this.output.showError("Opening a web browser window not supported!\nOperation aborted");
    }
    return res;
  }

}
