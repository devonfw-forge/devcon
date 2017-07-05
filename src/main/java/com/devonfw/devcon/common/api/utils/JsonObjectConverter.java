package com.devonfw.devcon.common.api.utils;

import org.json.simple.JSONObject;

/**
 * Defines interface for conversion of Json object to instance of class T
 *
 * @author ivanderk
 */
public interface JsonObjectConverter<T> {

  T convert(JSONObject obj);
}
