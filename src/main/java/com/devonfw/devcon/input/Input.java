package com.devonfw.devcon.input;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public interface Input {

  String promptForArgument(String argName);

  boolean askForUserConfirmation(String message);

}
