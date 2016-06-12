package com.devonfw.devcon.common.api;

import com.google.common.base.Optional;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public interface CommandRegistry {

  Optional<CommandModule> getCommandModule(String module);

  Optional<Command> getCommand(String module, String command);
}
