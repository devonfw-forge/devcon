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
   * @param root
   * @param string
   * @param i
   * @return
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
   * @param obj
   * @param key
   * @return
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
   * @param obj
   * @param key
   * @param deflt
   * @return
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
   * @param obj
   * @param key
   * @param deflt
   * @return
   */
  public static String getJsonString(JSONObject obj, String key, String deflt) {

    Object target = obj.get(key);
    if (target == null) {
      return deflt;
    } else {
      return target.toString();
    }
  }

  public static <T> T getTypedObject(JSONObject obj, String key, JsonObjectConverter<T> converter) {

    return getTypedObject(obj, key, null, converter);
  }

  public static <T> T getTypedObject(JSONObject obj, String key, T deflt, JsonObjectConverter<T> converter) {

    JSONObject target = (JSONObject) obj.get(key);
    if (target == null) {
      return deflt;
    } else {
      return converter.convert(target);
    }
  }

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
