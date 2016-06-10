package com.devonfw.devcon;

import com.devonfw.devcon.input.InputConsole;

/**
 * Main class of DevCon
 *
 * @author pparrado
 */
public class Devcon {

  public static String VERSION = "0.1.0";

  public static String DEVCON_VERSION = "devcon v." + VERSION;

  public static final String DEVCON_BANNER = "Hello, this is Devcon!\n" + "Copyright (c) 2016 Capgemini";

  /**
   * @param args command line arguments
   */
  public static void main(String[] args) {

    System.out.println(DEVCON_BANNER);

    new InputConsole(args).parse();
  }

}
