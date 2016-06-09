package com.devonfw.devcon.common.utils;

import com.devonfw.devcon.common.api.utils.Pair;

/*
 * This class implemments an immutable Tuple of two items (data on reference type is NOT immutable)
 *
 * @author ivanderk
 */
public class BasicPair<T, Y> implements Pair<T, Y> {

  private T first;

  private Y last;

  public BasicPair(T first, Y last) {
    this.first = first;
    this.last = last;
  }

  @Override
  public T getFirst() {

    return this.first;
  }

  @Override
  public Y getLast() {

    return this.last;
  }

}
