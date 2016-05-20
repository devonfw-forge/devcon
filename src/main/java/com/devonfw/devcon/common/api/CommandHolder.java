package com.devonfw.devcon.common.api;

import java.util.List;

import org.apache.commons.cli.Option;

import com.devonfw.devcon.common.api.annotations.Command;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public interface CommandHolder {

  /**
   * @return the list of available {@link @Command} commands
   */
  List<Command> getCommands();

  /**
   * @param name of the {@link Command}
   * @return a {@link Command}
   */
  Option getCommand(String name);

}
