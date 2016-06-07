package com.devonfw.devcon.output;

import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.data.DevconOption;
import com.devonfw.devcon.common.api.data.Info;
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

    for (Parameter commandParam : response.getCommandParamsList()) {
      this.options.addOption(commandParam.name(), false, commandParam.description());
    }

    HelpFormatter formater = new HelpFormatter();

    formater.printHelp(response.getName(), response.getDescription(), this.options, null, true);
  }

  public void showModuleHelp(Response response) {

    StringBuilder footerContent = new StringBuilder();
    footerContent.append("Available commands for module: " + response.getName() + "\n");
    for (Info command : response.getCommandsList()) {
      footerContent.append("> " + command.getName() + ": " + command.getDescription() + "\n");
    }

    HelpFormatter formater = new HelpFormatter();

    formater.printHelp(response.getName() + " <<command>> [parameters...]", response.getDescription(), new Options(),
        footerContent.toString(), true);
  }

  public void showGeneralHelp(Response response) {

    response.setHeader(response.getHeader() != null ? response.getHeader() : "");
    response.setUsage(response.getUsage() != null ? response.getUsage() : " ");
    response.setFooter(response.getFooter() != null ? response.getFooter() : "");

    for (DevconOption opt : response.getGlobalParameters()) {
      this.options.addOption(opt.getOpt(), opt.getLongOpt(), false, opt.getDescription());
    }

    StringBuilder footer = new StringBuilder();
    footer.append("List of available modules: \n");
    for (Info module : response.getModulesList()) {
      footer.append("> " + module.getName() + ": " + module.getDescription() + "\n");
    }

    HelpFormatter formater = new HelpFormatter();
    formater.printHelp(response.getUsage(), response.getHeader(), this.options, footer.toString(), false);
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

    System.out.println("\r[INFO] " + message);
  }

  public void statusInNewLine(String message) {

    System.out.println("\n[INFO] " + message);
  }

  public void success(String command) {

    System.out.println("[INFO] The command " + command.toUpperCase() + " has finished successfully");
  }

}
