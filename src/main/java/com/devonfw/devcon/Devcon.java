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
  public static final String VERSION = "1.4.2";

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
  public static final String DEVON_DEFAULT_VERSION = "2.3.1";

  /**
   * devcon's starter message
   */
  public static final String DEVCON_BANNER = "Hello, this is Devcon!\n" + "Copyright (c) 2016 Capgemini";

  /**
   * Determine whether app is inside an "executable jar" or not (made with Eclipse: has an "resource" folder"
   */
  public static final boolean IN_EXEC_JAR = ClassLoader.getSystemClassLoader()
      .getResource("resources/execjar.txt") != null;

  /**
   * Obtain script engine; only on Java 1.8+ is Javascript supported (version "Nashorn")
   */
  public static final Optional<ScriptEngine> scriptEngine = Optional
      .fromNullable(new ScriptEngineManager().getEngineByName(Constants.SCRIPT_ENGINE_NAME));

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

    ConsoleInputManager inputmanager = new ConsoleInputManager(registry, input, output,
        new CommandManagerImpl(registry, input, output));
    inputmanager.parse(args);
  }

}
