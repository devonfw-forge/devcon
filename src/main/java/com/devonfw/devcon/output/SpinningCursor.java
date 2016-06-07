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
