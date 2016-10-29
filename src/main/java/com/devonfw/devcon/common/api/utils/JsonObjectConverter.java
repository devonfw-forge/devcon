package com.devonfw.devcon.common.api.utils;

import org.json.simple.JSONObject;

/**
 *
 * @author ivanderk
 */
public interface JsonObjectConverter<T> {

  T convert(JSONObject obj);
}
