package com.devonfw.devcon.modules.doc;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.impl.AbstractCommandModule;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 */

@CmdModuleRegistry(name = "doc", description = "Module with tasks related with obtaining specific documentation", deprecated = false)
public class Doc extends AbstractCommandModule {

  /**
   *
   */
  private static final String SENCHA_EXTJS_DOCS = "https://docs.sencha.com/extjs/6.0/index.html";

  private static final String DEVON_GET_STARTED = "https://troom.capgemini.com/sites/vcc/devon/getstarted.aspx";

  private static final String DEVON_TROOM_SITE = "https://troom.capgemini.com/sites/vcc/devon/index.aspx";

  private static final String DEVON_GUIDE = "https://google.com";

  private static final String OASP4J_GUIDE = "https://google.com";

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
  @SuppressWarnings("javadoc")
  @Command(name = "devon", help = "Opens the Devon site in the default web browser")
  public void devon() {

    openUrl(DEVON_TROOM_SITE);
  }

  @SuppressWarnings("javadoc")
  @Command(name = "devonguide", help = "Opens the Devon Guide")
  public void devonguide() {

    openUrl(DEVON_GUIDE);
  }

  @SuppressWarnings("javadoc")
  @Command(name = "oasp4jguide", help = "Opens the OASP4J Guide")
  public void oasp4jguide() {

    openUrl(OASP4J_GUIDE);
  }

  @Command(name = "getstarted", help = "Opens the \"Getting Started\" web site")
  public void getstarted() {

    openUrl(DEVON_GET_STARTED);
  }

  @Command(name = "sencha", help = "Show Sencha Ext JS 6 documentation site")
  public void sencha() {

    openUrl(SENCHA_EXTJS_DOCS);
  }

  @Command(name = "links", help = "Print Devonfw \"business card\" with info & links to the console")
  public void links() {

    String blurb = "devonfw is the CSD standard platform for Capgemini APPS2. It provides a\n"
        + "standardized architecture blueprint for web and Java-applications, an open\n"
        + "best-of-breed technology stack  as well as industry proven best practices and code\n"
        + "conventions. It is a industrialization initiative that is aligned across Capgemini APPS2.\n" + "\n"
        + "You can find a brief introduction and flyer about devonfw at:\n"
        + "https://www.de.capgemini.com/devonfw (public site)\n" + "\n"
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

    boolean res = this.dUtils.openUri(url);
    if (!res) {
      this.output.showError("Opening a web browser window not supported!\nOperation aborted");
    }
    return res;
  }

}
