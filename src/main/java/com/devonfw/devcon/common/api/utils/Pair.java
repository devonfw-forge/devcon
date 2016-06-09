package com.devonfw.devcon.common.api.utils;

/**
 * Pair This interface defines a Tuple of two items
 * 
 * @author ivanderk
 */
public interface Pair<T, Y> {

  public T getFirst();

  public Y getLast();
}
