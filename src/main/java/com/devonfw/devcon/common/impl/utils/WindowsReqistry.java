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
package com.devonfw.devcon.common.impl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * @author Oleg Ryaboy, based on work by Miguel Enriquez Released to the Public Domain patched by ivanderk - surely not
 *         stable nor reliable
 *
 *         Simple interface to the REG utility which allows reading/writing of Windows registry keys & values
 */
public class WindowsReqistry {
  /**
   * @param location path in the registry
   * @param key registry key
   * @return registry value or null if not found
   */
  public static String readRegistry(String location, String key) {

    try {
      // Run reg query, then read output with StreamReader (internal class)
      Process process = Runtime.getRuntime().exec("reg query " + '"' + location + "\" /v " + key);
      StreamReader reader = new StreamReader(process.getInputStream());
      reader.start();
      process.waitFor();
      reader.join();
      String output = reader.getResult().trim();

      // Output has the following format:
      // KEY_CURRENT_USER\Environment<CF>
      // <var> REG_SZ <value>
      //
      int start = output.indexOf("    ", 0);
      start = output.indexOf("    ", start + 4);
      start = output.indexOf("    ", start + 4);

      String parsed = output.substring(start + 4, output.length());
      return parsed;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * @param location path in the registry
   * @param key registry key
   * @value registry value to write
   */
  public static void writeRegistry(String location, String key, String value) {

    try {
      // Run reg add command
      String cmd = String.format("reg add %s /v %s /F /d \"%s\"", location, key, value);
      Process process = Runtime.getRuntime().exec(cmd);
      StreamReader reader = new StreamReader(process.getInputStream());
      reader.start();
      process.waitFor();
      reader.join();
      String output = reader.getResult();
      // Error check ??

    } catch (Exception e) {
      System.err.println("writeRegistry: " + e.getMessage());
    }
  }

  /**
   * @param location path in the registry
   * @param key registry key
   * @return
   */
  public static void deleteRegistry(String location, String key) {

    try {
      // Run reg delete command
      String cmd = String.format("reg delete %s /v %s /f", location, key);
      Process process = Runtime.getRuntime().exec(cmd);
      StreamReader reader = new StreamReader(process.getInputStream());
      reader.start();
      process.waitFor();
      reader.join();
      String output = reader.getResult();
      // Error check ??

    } catch (Exception e) {
      System.err.println("writeRegistry: " + e.getMessage());
    }
  }

  static class StreamReader extends Thread {
    private InputStream is;

    private StringWriter sw = new StringWriter();

    public StreamReader(InputStream is) {
      this.is = is;
    }

    @Override
    public void run() {

      try {
        int c;
        while ((c = this.is.read()) != -1)
          this.sw.write(c);
      } catch (IOException e) {
      }
    }

    public String getResult() {

      return this.sw.toString();
    }
  }
}
