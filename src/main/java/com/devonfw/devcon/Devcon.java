package com.devonfw.devcon;

import com.devonfw.devcon.input.InputConsole;

/**
 * Main class of DevCon
 *
 * @author pparrado
 */
public class Devcon {

  /**
   *
   */
  private static final String banner = "Hello, this is Devcon!\n" + "Copyright (c) 2016 Capgemini";

  /**
   * @param args command line arguments
   */
  public static void main(String[] args) {

    System.out.println(banner);

    new InputConsole(args).parse();
  }

}
