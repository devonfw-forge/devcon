package com.devonfw.devcon;

import java.io.IOException;
import java.nio.file.Path;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.json.simple.parser.ParseException;

import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.impl.CommandManagerImpl;
import com.devonfw.devcon.common.impl.CommandRegistryImpl;
import com.devonfw.devcon.common.impl.JavaScriptsCmdRegistryImpl;
import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.common.utils.Utils;
import com.devonfw.devcon.input.ConsoleInput;
import com.devonfw.devcon.input.ConsoleInputManager;
import com.devonfw.devcon.input.Input;
import com.devonfw.devcon.output.ConsoleOutput;
import com.devonfw.devcon.output.Output;
import com.github.zafarkhaja.semver.Version;
import com.google.common.base.Optional;

/**
 * Main class of DevCon
 *
 * @author pparrado
 */
public class Devcon {

  /**
   * current version of the tool
   */
  public static final String VERSION = "1.3.0";

  /**
   * url where the versions configuration file is located
   */
  public static final String VERSION_URL = "http://devonfw.github.io/download/devcon/version.json";

  /**
   *
   */
  public static final Version VERSION_ = Version.valueOf(VERSION);

  /**
   * message to be shown as version of the tool
   */
  public static final String DEVCON_VERSION = "devcon v." + VERSION;

  /**
   * to be used as version of devon.json files
   */
  public static final String DEVON_DEFAULT_VERSION = "2.2.0";

  /**
   * devcon's starter message
   */
  public static final String DEVCON_BANNER = "Hello, this is Devcon!\n" + "Copyright (c) 2016 Capgemini";

  /**
   * Determine whether app is inside an "executable jar" or not (made with Eclipse: has an "resource" folder"
   */
  public static final boolean IN_EXEC_JAR =
      ClassLoader.getSystemClassLoader().getResource("resources/execjar.txt") != null;

  /**
   * Obtain script engine; only on Java 1.8+ is Javascript supported (version "Nashorn")
   */
  public static final Optional<ScriptEngine> scriptEngine =
      Optional.fromNullable(new ScriptEngineManager().getEngineByName(Constants.SCRIPT_ENGINE_NAME));

  /**
   * Show stack-trace when errors are thrown by the command
   */
  public static boolean SHOW_STACK_TRACE = false;

  /**
   * @param args command line arguments
   */
  public static void main(String[] args) {

    System.out.println(DEVCON_BANNER);

    Input input = new ConsoleInput(System.in, System.out);
    Output output = new ConsoleOutput(System.out);
    CommandRegistry registry = new CommandRegistryImpl(Constants.MODULES_LOCATION);

    Path scriptDir = Utils.getScriptDir();

    if (scriptEngine.isPresent() && scriptDir.toFile().exists()) {
      try {
        JavaScriptsCmdRegistryImpl jsregistry = new JavaScriptsCmdRegistryImpl(scriptDir);
        registry.add(jsregistry);

      } catch (ParseException | IOException e) {
        e.printStackTrace();
        System.exit(-1);
      }
    }

    ConsoleInputManager inputmanager =
        new ConsoleInputManager(registry, input, output, new CommandManagerImpl(registry, input, output));
    inputmanager.parse(args);
  }

}
