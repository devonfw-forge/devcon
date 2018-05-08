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
package com.devonfw.devcon.common.impl;

import static com.devonfw.devcon.common.utils.JsonValues.getJSONArray;
import static com.devonfw.devcon.common.utils.JsonValues.getJsonBoolean;
import static com.devonfw.devcon.common.utils.JsonValues.getJsonLong;
import static com.devonfw.devcon.common.utils.JsonValues.getJsonString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModule;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.exception.InvalidConfigurationStateException;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * The central repository where all {@link CommandModule}s with their respective {@link Command}s are loaded and stored
 *
 * @author ivanderk
 */
public class JavaScriptsCmdRegistryImpl extends BaseCommandRegistryImpl {

  public JavaScriptsCmdRegistryImpl(Path scriptModules) throws ParseException, IOException {
    super();
    registerModules(scriptModules);
  }

  /**
   *
   * Read all modules from root directory
   *
   * @param scripts Path where to find scripts modules
   * @throws IOException
   * @throws ParseException
   */
  private void registerModules(Path scriptModules) throws ParseException, IOException {

    Collection<File> files = getCommandJsons(scriptModules);
    for (File commandJson : files) {
      processCommands(commandJson);
    }
  }

  /**
   *
   * Get all command.json files within base-path, scanned recursively
   *
   * @param JavaScript Modules
   * @return
   */
  private Collection<File> getCommandJsons(Path scriptModules) {

    Collection<File> fls = FileUtils.listFilesAndDirs(scriptModules.toFile(), new IOFileFilter() {

      @Override
      public boolean accept(File file) {

        // include commands.json
        return (file.isFile() && file.getName().equals("commands.json"));
      }

      @Override
      public boolean accept(File dir, String name) {

        System.out.println("Accept dir: " + dir.getName() + " | name: " + name);
        return false;
      }
    }, TrueFileFilter.TRUE);

    return Collections2.filter(fls, new Predicate<File>() {

      @Override
      public boolean apply(File input) {

        // only return files
        return input.isFile();
      }
    });
  }

  /**
   *
   * Process Commands in command.json file
   *
   * @param commandJson
   * @throws IOException
   * @throws ParseException
   */
  private void processCommands(File commandJson) throws ParseException, IOException {

    JSONParser parser = new JSONParser();

    Object obj = parser.parse(IOUtils.toString(commandJson.toURI(), "utf-8"));
    JSONObject root = (JSONObject) obj;

    // name property is mandatory
    if (!(root.get("name") != null) && !root.get("name").toString().isEmpty()) {
      throw new InvalidConfigurationStateException("missing or invalid 'name' property in " + commandJson.getPath());
    }

    String name = root.get("name").toString();
    String description = getJsonString(root, "description", "");
    int sortValue = (int) getJsonLong(root, "sort", -1);
    boolean visible = getJsonBoolean(root, "visible", true);
    JSONArray cmdsJson = getJSONArray(root, "commands");
    CommandModuleInfo jsmodule = new JsCmdModuleInfoImpl(name, description, sortValue, visible, commandJson, cmdsJson);

    this.modules.put(name, jsmodule);

  }

}
