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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * TODO ssarmoka This type ...
 *
 * @author ssarmoka
 */
public class ProcessUtils {

  /**
   * This process returns processId for GUI process
   *
   * @return
   */
  public long getPidForMXBean() {

    long pid = 0;
    RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
    String jvmName = bean.getName();
    pid = Long.valueOf(jvmName.split("@")[0]);
    return pid;
  }

  /**
   * This method find out subprocess created by pid.
   *
   * @param pid process id for parent process
   * @return process id for child process
   */
  public long getChildren(long pid) {

    long childProcessId = 0;
    String cmd = "WMIC process where (ParentProcessId=" + pid + ") get ProcessId";
    try {
      Process process = Runtime.getRuntime().exec(cmd);
      InputStreamReader isr = new InputStreamReader(process.getInputStream());
      BufferedReader bre = new BufferedReader(isr);
      String line;
      while ((line = bre.readLine()) != null) {
        line = line.trim();
        if (line.matches("-?[0-9]+")) {
          childProcessId = Long.valueOf(line);
          return childProcessId;
        }

      }
    } catch (IOException e) {
      System.out.println("ERROR occured while getting subprocesses: " + e.getMessage());
    }
    return childProcessId;
  }

  /**
   * This will kill parent process and subprocesses created by it
   *
   * @throws IOException
   */
  public void killProcess() throws IOException {

    long childProcessId;
    long guiProcessId = getPidForMXBean();
    childProcessId = getChildren(guiProcessId);
    while (childProcessId != 0) {
      Runtime.getRuntime().exec(Constants.TASKKILL_PID_CMD + childProcessId);
      childProcessId = getChildren(childProcessId);
    }
  }

}
