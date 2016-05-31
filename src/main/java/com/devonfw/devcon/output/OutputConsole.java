package com.devonfw.devcon.output;

import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.data.Response;

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

    for (Parameter commandParam : response.commandParamsList) {
      this.options.addOption(commandParam.name(), false, commandParam.description());
    }

    HelpFormatter formater = new HelpFormatter();

    formater.printHelp(response.name, response.description, this.options, null, true);
    // System.exit(0);
  }

  public void showModuleHelp(Response response) {

    StringBuilder footerContent = new StringBuilder();
    footerContent.append("Available commands for module: " + response.name + "\n");
    for (String method : response.methodsList) {
      footerContent.append("> " + method + "\n");
    }

    HelpFormatter formater = new HelpFormatter();

    formater.printHelp(response.name, response.description, new Options(), footerContent.toString(), true);
    // System.exit(0);
  }

  public void showGeneralHelp(Response response) {

    response.header = response.header != null ? response.header : "";
    response.usage = response.usage != null ? response.usage : " ";
    response.footer = response.footer != null ? response.footer : "";

    HelpFormatter formater = new HelpFormatter();
    formater.printHelp(response.usage, response.header, new Options(), response.footer, false);
  }

  public String promptForArgument(String argName) {

    Scanner reader = new Scanner(System.in);
    System.out.printf("Please introduce value for missing param %s: ", argName);
    return reader.next();
  }

  public boolean askForUserConfirmation(String message) {

    String[] validResponses = { "yes", "y", "no", "n" };
    Scanner reader = new Scanner(System.in);
    System.out.println(message);
    System.out.println("Y/N");
    String response = reader.next();
    while (!Arrays.asList(validResponses).contains(response.toLowerCase())) {
      System.out.println("Please type 'yes' or 'no'");
      response = reader.next();
    }
    if (response.toLowerCase().equals("yes") || response.toLowerCase().equals("y")) {
      return true;
    } else {
      return false;
    }
  }

  public void showError(String message) {

    System.out.println("[ERROR]" + message);
  }

  public void status(String message) {

    System.out.println(message);
  }

  public void success(String command) {

    System.out.println("The command " + command + " has finished successfully");
  }

}
