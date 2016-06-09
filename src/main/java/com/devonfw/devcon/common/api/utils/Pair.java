package com.devonfw.devcon.common.api.utils;

/**
 * Pair This interface defines a Tuple of two items
 *
 * @author ivanderk
 */
public interface Pair<T, Y> {

  /**
   * @return first, "left hand", value
   */
  public T getFirst();

  /**
   * @return second, last or "right hand", value
   */
  public Y getLast();
}
