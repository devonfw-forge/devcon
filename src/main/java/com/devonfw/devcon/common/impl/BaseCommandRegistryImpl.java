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

}
