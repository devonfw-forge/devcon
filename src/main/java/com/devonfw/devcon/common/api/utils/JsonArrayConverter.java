package com.devonfw.devcon.common.api.utils;

/**
 *
 * Defines interface for conversion of all Json Array to array with type T
 *
 * @author ivanderk
 */
public interface JsonArrayConverter<T> {

  T convertElement(Object obj);
}
