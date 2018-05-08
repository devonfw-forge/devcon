/*******************************************************************************
 * Copyright 2015-2018 Capgemini SE.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
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
