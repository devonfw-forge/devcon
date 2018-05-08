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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.exception.InvalidConfigurationStateException;

/**
 * Implementation of {@link CommandModuleInfo}
 *
 * @author ivanderk
 */
public class JsCmdModuleInfoImpl extends BaseCmdModuleInfoImpl {

  private File commandJson;

  public JsCmdModuleInfoImpl(String name, String description, int sortValue, boolean isVisible, File commandJson,
      JSONArray jsonCmds) {

    super();
    this.name = name;
    this.description = description;
    this.sortValue = sortValue;
    this.isVisible = isVisible;
    this.commandJson = commandJson;
    addCommands(name, jsonCmds);
  }

  /**
   * Add commands from within Module
   * 
   * @param moduleName module Name
   * @param jsonCmds Raw json array containing commands
   */
  void addCommands(String moduleName, JSONArray jsonCmds) {

    try {
      for (Object e : jsonCmds) {

        JSONObject cmdJson = (JSONObject) e;

        // name property is mandatory
        if (!(cmdJson.get("name") != null) && !cmdJson.get("name").toString().isEmpty()) {
          throw new InvalidConfigurationStateException(
              "missing or invalid 'name' property in Javascript module: " + moduleName);
        }
        // path property is mandatory
        if (!(cmdJson.get("path") != null) && !cmdJson.get("path").toString().isEmpty()) {
          throw new InvalidConfigurationStateException(
              "missing or invalid 'path' property in Javascript module: " + moduleName);
        }

        String name = cmdJson.get("name").toString();
        String path = cmdJson.get("path").toString();
        String description = getJsonString(cmdJson, "description", "");
        int sortValue = (int) getJsonLong(cmdJson, "sort", -1);
        boolean proxyParams = getJsonBoolean(cmdJson, "proxyParams", false);
        ContextType context = ContextType.valueOf(getJsonString(cmdJson, "context", "NONE"));

        JSONArray paramsJson = getJSONArray(cmdJson, "parameters");

        File script = this.commandJson.toPath().getParent().resolve(path).toFile();
        if (!script.exists()) {
          throw new InvalidConfigurationStateException("Script doesn not exist: " + script.getName());
        }
        Command cmd = new JsCmdImpl(name, script, description, sortValue, context, proxyParams, moduleName, paramsJson);
        this.commands.put(name, cmd);

      }
    } catch (Exception e) {

      throw new InvalidConfigurationStateException(e);
    }
  }

}
