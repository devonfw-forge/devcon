package com.devonfw.devcon.common;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class InputManager {

  private String[] args = null;

  private Options options = new Options();

  public InputManager(String[] args) {

    this.args = args;
    this.options.addOption("h", false, "show help");
  }

  public void parse() {

    CommandLineParser parser = new BasicParser();
    CommandLine cmd = null;
    try {
      cmd = parser.parse(this.options, this.args);

      if (cmd.hasOption("h"))
        help();

      if (cmd.hasOption("v")) {
        System.out.println("You passed option V --> " + cmd.getOptionValue("v"));
      }

    } catch (ParseException e) {
      System.out.println("ERROR: " + e.getMessage());
    }
  }

  private void help() {

    HelpFormatter formater = new HelpFormatter();
    formater.printHelp("Main", this.options);
    System.exit(0);
  }
}
