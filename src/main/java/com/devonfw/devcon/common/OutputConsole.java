package com.devonfw.devcon.common;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.devonfw.devcon.common.api.entity.Response;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 * @since 0.0.1
 */
public class OutputConsole {

  private Options options = new Options();

  public void showMessage(String message) {

    System.out.println(message);
  }

  public void showCommandHelp(Response response) {

    for (String commandParam : response.commandParamsList) {
      this.options.addOption(commandParam, false, null);
    }

    HelpFormatter formater = new HelpFormatter();

    formater.printHelp(response.name, response.description, this.options, null, true);
    // formater.printHelp(response.description, this.options);
    System.exit(0);
  }
}
