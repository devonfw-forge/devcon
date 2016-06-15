package com.devonfw.devcon.common.api;

import java.util.List;

import com.google.common.base.Optional;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public interface CommandRegistry {

  List<CommandModuleInfo> getCommandModules();

  Optional<CommandModuleInfo> getCommandModule(String module);

  Optional<Command> getCommand(String module, String command);

}