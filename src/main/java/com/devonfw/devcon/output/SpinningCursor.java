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
package com.devonfw.devcon.output;

/**
 * Shows a progress bar for extracting tasks
 *
 * @author pparrado
 */
public class SpinningCursor implements Runnable {
  private volatile boolean running = true;

  public void terminate() {

    this.running = false;
  }

  public void run() {

    int counter = 0;
    String[] cursor = { "-", "\\", "|", "/", "-", "\\", "|", "/" };
    while (this.running) {
      try {
        Thread.sleep(100);
        System.out.print("\b");
        System.out.print(cursor[counter]);
        counter++;
        if (counter > 7)
          counter = 0;
      } catch (InterruptedException e) {
        System.out.println("ERROR " + e.getMessage());
        this.running = false;
      }
    }
  }

}
