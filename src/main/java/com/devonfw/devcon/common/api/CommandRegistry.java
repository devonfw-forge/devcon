package com.devonfw.devcon.common.api;

import java.util.Collection;

import com.google.common.base.Optional;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public interface CommandRegistry {

  Collection<CommandModule> getCommandModules();

  Optional<CommandModule> getCommandModule(String module);

  Optional<Command> getCommand(String module, String command);

}