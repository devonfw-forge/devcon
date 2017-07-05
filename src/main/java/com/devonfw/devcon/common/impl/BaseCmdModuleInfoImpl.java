package com.devonfw.devcon.common.impl;

import java.util.Collection;
import java.util.HashMap;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.google.common.base.Optional;

/**
 * Implementation of {@link CommandModuleInfo}
 *
 * @author ivanderk
 */
public class BaseCmdModuleInfoImpl implements CommandModuleInfo {

  protected String name;

  protected String description;

  protected int sortValue;

  protected boolean isVisible;

  protected HashMap<String, Command> commands;

  public BaseCmdModuleInfoImpl() {

    this.commands = new HashMap<>();
  }

  @Override
  public String getName() {

    return this.name;
  }

  @Override
  public String getDescription() {

    return this.description;
  }

  @Override
  public boolean isVisible() {

    return this.isVisible;
  }

  @Override
  public Collection<Command> getCommands() {

    return this.commands.values();
  }

  @Override
  public Optional<Command> getCommand(String name) {

    if (this.commands.containsKey(name)) {
      return Optional.of(this.commands.get(name));
    } else {
      return Optional.absent();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(CommandModuleInfo o) {

    return getName().compareTo(o.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getSortValue() {

    return this.sortValue;
  }
}
