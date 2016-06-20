package com.devonfw.devcon.input;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public class ConsoleInput implements Input {

  private PrintStream out_;

  private InputStream in_;

  public ConsoleInput() {
    this.in_ = System.in;
    this.out_ = System.out;
  }

  public ConsoleInput(InputStream in, PrintStream out) {
    this();
    this.in_ = in;
    this.out_ = out;
  }

  @Override
  public String promptForArgument(String argName) {

    Scanner reader = new Scanner(this.in_); // new Scanner(System.in);
    this.out_.printf("Please introduce value for missing param %s: ", argName);
    return reader.next();
  }

  @Override
  public boolean askForUserConfirmation(String message) {

    String[] validResponses = { "yes", "y", "no", "n" };
    Scanner reader = new Scanner(this.in_); // new Scanner(System.in);
    this.out_.println(message);
    this.out_.println("Y/N");
    String response = reader.next();
    while (!Arrays.asList(validResponses).contains(response.toLowerCase())) {
      this.out_.println("Please type 'yes' or 'no'");
      response = reader.next();
    }
    if (response.toLowerCase().equals("yes") || response.toLowerCase().equals("y")) {
      return true;
    } else {
      return false;
    }
  }
}
