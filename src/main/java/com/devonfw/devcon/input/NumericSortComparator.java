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
