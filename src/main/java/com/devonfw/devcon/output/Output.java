package com.devonfw.devcon.output;

import com.devonfw.devcon.common.api.data.Response;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public interface Output {

  void showMessage(String message);

  void showCommandHelp(Response response);

  void showModuleHelp(Response response);

  void showGeneralHelp(Response response);

  void showError(String message);

  void status(String message);

  void statusInNewLine(String message);

  void success(String command);

}