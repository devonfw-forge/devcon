package com.devonfw.devcon.common.api;

import java.util.Collection;

import com.devonfw.devcon.common.api.data.Info;
import com.google.common.base.Optional;

/**
 * Contains information about a {@link CommandModule}
 *
 * @author pparrado
 */
public interface CommandModuleInfo extends Info, Comparable<CommandModuleInfo> {

  public boolean isVisible();

  /**
   * @return the list of available {@link @Command} commands
   */
  Collection<Command> getCommands();

  /**
   * @param name of the {@link Command}
   * @return a {@link Command}
   */
  Optional<Command> getCommand(String name);

}
