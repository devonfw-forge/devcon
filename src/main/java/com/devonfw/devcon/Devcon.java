package com.devonfw.devcon;

import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.impl.CommandManagerImpl;
import com.devonfw.devcon.common.impl.CommandRegistryImpl;
import com.devonfw.devcon.input.ConsoleInput;
import com.devonfw.devcon.input.ConsoleInputManager;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.output.ConsoleOutput;
import com.devonfw.devcon.output.Output;
import com.github.zafarkhaja.semver.Version;

/**
 * Main class of DevCon
 *
 * @author pparrado
 */
public class Devcon {

  public static final String VERSION = "1.0.0";

  // Mock url. Can be used with node.js http-server, for example. See public/version.json for data
  // public static final String VERSION_URL = "http://localhost:8080/version.json";
  public static final String VERSION_URL = "http://devonfw.github.io/download/devcon/version.json";

  public static final Version VERSION_ = Version.valueOf(VERSION);

  public static final String DEVCON_VERSION = "devcon v." + VERSION;

  /**
   * to be used as version of devon.json files
   */
  public static final String DEVON_DEFAULT_VERSION = "2.0.1";

  public static final String DEVCON_BANNER = "Hello, this is Devcon!\n" + "Copyright (c) 2016 Capgemini";

  // Determine whether app is inside an "executable jar" or not (made with Eclipse: has an "resource" folder"
  public static final boolean IN_EXEC_JAR =
      ClassLoader.getSystemClassLoader().getResource("resources/execjar.txt") != null;

  /**
   * @param args command line arguments
   */
  public static void main(String[] args) {

    System.out.println(DEVCON_BANNER);

    Input input = new ConsoleInput(System.in, System.out);
    Output output = new ConsoleOutput(System.out);
    CommandRegistry registry = new CommandRegistryImpl("com.devonfw.devcon.modules.*");

    ConsoleInputManager inputmanager =
        new ConsoleInputManager(registry, input, output, new CommandManagerImpl(registry, input, output));
    inputmanager.parse(args);
  }

}
