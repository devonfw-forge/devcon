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
package com.devonfw.devcon.common.utils;

import java.lang.reflect.Array;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.devonfw.devcon.common.api.utils.JsonArrayConverter;
import com.devonfw.devcon.common.api.utils.JsonObjectConverter;

/**
 * Utility class to support safely obtaining values from JSONObject & JSONArray objects
 *
 * @author ivanderk
 */
public class JsonValues {

  /**
   * Get & convert Json object 'number' property value to Long
   *
   * @param obj raw Json object
   * @param string key name
   * @param dflt default value
   * @return a long value
   */
  public static long getJsonLong(JSONObject obj, String key, int dflt) {

    Object target = obj.get(key);
    if (target == null) {
      return dflt;
    } else {
      return ((Long) target).longValue();
    }
  }

  /**
   * Get Json array from object property
   *
   * @param obj raw Json object
   * @param key key name
   * @return the Json array
   */
  public static JSONArray getJSONArray(JSONObject obj, String key) {

    Object target = obj.get(key);
    if (target == null) {
      return new JSONArray();
    } else {
      return (JSONArray) target;
    }
  }

  /**
   * Get & convert Json object 'bool' property value to boolean
   *
   * @param obj raw Json object
   * @param string key name
   * @param dflt default value
   * @return a boolean value
   */
  public static boolean getJsonBoolean(JSONObject obj, String key, boolean deflt) {

    Object target = obj.get(key);
    if (target == null) {
      return deflt;
    } else {
      return ((Boolean) target).booleanValue();
    }
  }

  /**
   * Get & convert Json object 'string' property value to String
   *
   * @param obj raw Json object
   * @param string key name
   * @param dflt default value
   * @return a String value
   */
  public static String getJsonString(JSONObject obj, String key, String deflt) {

    Object target = obj.get(key);
    if (target == null) {
      return deflt;
    } else {
      return target.toString();
    }
  }

  /**
   * Get & convert Json 'object' property to class instance
   *
   * @param obj raw Json object
   * @param string key name
   * @return a class instance
   */
  public static <T> T getTypedObject(JSONObject obj, String key, JsonObjectConverter<T> converter) {

    return getTypedObject(obj, key, null, converter);
  }

  /**
   * Get & convert Json 'object' property to class instance with possible default valie
   *
   * @param obj raw Json object
   * @param string key name
   * @param deflt default value
   * @param converter Converter logic for whole object
   * @return a class instance
   */
  public static <T> T getTypedObject(JSONObject obj, String key, T deflt, JsonObjectConverter<T> converter) {

    JSONObject target = (JSONObject) obj.get(key);
    if (target == null) {
      return deflt;
    } else {
      return converter.convert(target);
    }
  }

  /**
   * Get & convert Json 'array' property to typed array
   *
   * @param obj raw Json object
   * @param string key name
   * @param klass Array element class variable used to instantiate Array
   * @param converter Converter logic for array element
   * @return a class instance
   */
  public static <T> T[] getTypedArray(JSONObject obj, String key, Class<T> klass, JsonArrayConverter<T> converter) {

    JSONArray target = (JSONArray) obj.get(key);
    if (target == null) {
      return (T[]) Array.newInstance(klass);
    } else {

      T[] ts = (T[]) Array.newInstance(klass, target.size());
      for (int i = 0; i < target.size(); i++) {

        T e = converter.convertElement(target.get(i));
        ts[i] = e;
      }
      return ts;
    }
  }

}
