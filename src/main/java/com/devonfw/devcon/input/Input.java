package com.devonfw.devcon.input;

/**
 * This defines the component which is responsible for all user input to the application
 *
 * @author ivanderk
 */
public interface Input {

  String promptUser(String msg, String... args);

  boolean askForUserConfirmation(String message, String... args);

}
