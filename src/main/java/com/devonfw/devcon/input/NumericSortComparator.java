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
package com.devonfw.devcon.input;

import java.util.Comparator;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.impl.CommandModuleInfoImpl;

/**
 * TODO ssarmoka This type ...
 *
 * @author ssarmoka
 * @param <T>
 */
public class NumericSortComparator<T> implements Comparator<T> {

  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(T object1, T object2) {

    if (object1 instanceof Command && object2 instanceof Command) {
      Command cmd1 = (Command) object1;
      Command cmd2 = (Command) object2;
      if (cmd1.getSortValue() == cmd2.getSortValue()) {
        return 0;
      } else if (cmd1.getSortValue() > cmd2.getSortValue()) {
        return -1;
      }
      return 1;
    }
    if (object1 instanceof CommandModuleInfo && object2 instanceof CommandModuleInfo) {
      CommandModuleInfoImpl cmd1 = (CommandModuleInfoImpl) object1;
      CommandModuleInfoImpl cmd2 = (CommandModuleInfoImpl) object2;
      if (cmd1.getSortValue() == cmd2.getSortValue()) {
        return 0;
      } else if (cmd1.getSortValue() > cmd2.getSortValue()) {
        return -1;
      }
      return 1;
    }
    return 0;
  }

}
