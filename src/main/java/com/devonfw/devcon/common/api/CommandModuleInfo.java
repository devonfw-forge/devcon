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
