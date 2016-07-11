package com.devonfw.devcon.input;

/**
 * This defines the component which is responsible for all user input to the application
 *
 * @author ivanderk
 */
public interface Input {

  String promptForArgument(String argName);

  boolean askForUserConfirmation(String message);

}
