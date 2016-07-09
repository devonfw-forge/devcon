package com.devonfw.devcon.common.api;

import java.util.List;

import com.google.common.base.Optional;

/**
 * The central repository where all {@link CommandModule}s with their respective {@link Command}s are loaded and stored
 *
 * @author ivanderk
 */
public interface CommandRegistry {

  List<CommandModuleInfo> getCommandModules();

  Optional<CommandModuleInfo> getCommandModule(String module);

  Optional<Command> getCommand(String module, String command);

}