package com.devonfw.devcon.output;

import java.util.List;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.data.DevconOption;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public interface Output {

  void showMessage(String message);

  void showGeneralHelp(String header, String usage, List<DevconOption> options, List<CommandModuleInfo> modules);

  void showCommandHelp(Command command);

  void showModuleHelp(CommandModuleInfo module);

  void showError(String message);

  void status(String message);

  void statusInNewLine(String message);

  void success(String command);

}