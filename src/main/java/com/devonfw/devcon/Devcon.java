package com.devonfw.devcon;

import com.devonfw.devcon.common.CommandManager;
import com.devonfw.devcon.common.impl.CommandRegistryImpl;
import com.devonfw.devcon.input.ConsoleInput;
import com.devonfw.devcon.output.ConsoleOutput;

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

    ConsoleInput input = new ConsoleInput(
        new CommandManager(new CommandRegistryImpl("com.devonfw.devcon.modules.*"), new ConsoleOutput(System.out)));
    input.parse(args);
  }

}
