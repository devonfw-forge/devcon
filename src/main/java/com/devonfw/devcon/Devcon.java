package com.devonfw.devcon;

import com.devonfw.devcon.input.InputConsole;

/**
 * Main class of DevCon
 *
 * @author pparrado
 */
public class Devcon {

  /**
   * @param args
   */
  public static void main(String[] args) {

    System.out.println("Hello this is Devcon!");

    new InputConsole(args).parse();
  }

}
