package com.devonfw.devcon.common.api;

import java.util.Collection;

import com.devonfw.devcon.common.api.data.Info;
import com.google.common.base.Optional;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public interface CommandModule extends Info {

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
