package com.devonfw.devcon.common;

import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.devonfw.devcon.common.api.entity.Response;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
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
    System.exit(0);
  }

  public void showModuleHelp(Response response) {

    StringBuilder footerContent = new StringBuilder();
    footerContent.append("Available commands for module: " + response.name + "\n");
    for (String method : response.methodsList) {
      footerContent.append("> " + method + "\n");
    }

    HelpFormatter formater = new HelpFormatter();

    formater.printHelp(response.name, response.description, new Options(), footerContent.toString(), true);
    System.exit(0);
  }

  public HashMap promptForArgument(String argName) {

    Scanner reader = new Scanner(System.in);
    System.out.printf("Please introduce value for missing param %s", argName);
    String value = reader.next();
    HashMap<String, String> hmap = new HashMap();
    hmap.put(argName, value);

    return hmap;
  }
}
