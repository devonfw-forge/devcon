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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.google.common.base.Optional;

/**
 * @author ivanderk
 */
public class BaseCommandRegistryImpl implements CommandRegistry {

  protected HashMap<String, CommandModuleInfo> modules;

  public BaseCommandRegistryImpl() {
    this.modules = new HashMap<>();
  }

  @Override
  public Optional<CommandModuleInfo> getCommandModule(String module) {

    if (this.modules.containsKey(module)) {
      return Optional.of(this.modules.get(module));
    } else {
      return Optional.absent();
    }
  }

  @Override
  public Optional<Command> getCommand(String module, String command) {

    Optional<CommandModuleInfo> mod_ = getCommandModule(module);
    if (mod_.isPresent())
      return mod_.get().getCommand(command);
    else
      return Optional.absent();
  }

  @Override
  public List<CommandModuleInfo> getCommandModules() {

    // TODO Refactot to Collection?
    return new ArrayList<CommandModuleInfo>(this.modules.values());
  }

  /**
   * @return modules
   */
  protected HashMap<String, CommandModuleInfo> getModules() {

    return this.modules;
  }

  /**
   * @param modules the modules to set
   */
  protected void setModules(HashMap<String, CommandModuleInfo> modules) {

    this.modules = modules;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void add(CommandRegistry otherRegistry) {

    BaseCommandRegistryImpl other = (BaseCommandRegistryImpl) otherRegistry;
    getModules().putAll(other.getModules());
  }

}
