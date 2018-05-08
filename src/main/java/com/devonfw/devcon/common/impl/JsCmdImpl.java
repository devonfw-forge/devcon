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

import static com.devonfw.devcon.common.utils.JsonValues.getJsonBoolean;
import static com.devonfw.devcon.common.utils.JsonValues.getJsonLong;
import static com.devonfw.devcon.common.utils.JsonValues.getJsonString;
import static com.devonfw.devcon.common.utils.JsonValues.getTypedArray;
import static com.devonfw.devcon.common.utils.JsonValues.getTypedObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.InputTypeNames;
import com.devonfw.devcon.common.api.data.ParameterInputType;
import com.devonfw.devcon.common.api.utils.JsonArrayConverter;
import com.devonfw.devcon.common.api.utils.JsonObjectConverter;
import com.devonfw.devcon.common.exception.InvalidConfigurationStateException;

/**
 * Implementation of {@link Command} for JavaScript Devcon Commands
 *
 * @author ivanderk
 */
public class JsCmdImpl extends BaseCmdImpl {

  private File script;

  /**
   * @return Returns Javascript template used to encapsulate Javascript Commands
   */
  private String loadSource() {

    String root = (Devcon.IN_EXEC_JAR) ? "resources/" : "";
    URL commandsJsonUrl = ClassLoader.getSystemClassLoader().getResource(root + "jstemplate.txt");

    try {
      return IOUtils.toString(commandsJsonUrl.toURI(), "utf-8");
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   *
   * The constructor.
   *
   * @param name Command name
   * @param script JavaScript source file
   * @param description Helpt text for Command
   * @param sortValue Order in which the command is shown in the help command listing
   * @param context Command execution context
   * @param proxyParams Possible proxy data
   * @param moduleName Name of the module containing the Command
   * @param paramsJson raw json data defining the command´s parameters
   */
  public JsCmdImpl(String name, File script, String description, int sortValue, ContextType context,
      boolean proxyParams, String moduleName, JSONArray paramsJson) {

    super();
    this.name = name;
    this.script = script;
    this.description = description;
    this.sortValue = sortValue;
    this.context = context;
    this.proxyParams = proxyParams;

    this.moduleName = moduleName;

    addParameters(paramsJson);

    patchParameters();
  }

  void addParameters(JSONArray paramsJson) {

    int pos = 0;

    for (Object e : paramsJson) {

      JSONObject obj = (JSONObject) e;

      // name property is mandatory
      if (!(obj.get("name") != null) && !obj.get("name").toString().isEmpty()) {
        throw new InvalidConfigurationStateException("missing or invalid 'name' property in command: " + this.name);
      }

      String _name = obj.get("name").toString();
      String _description = getJsonString(obj, "description", "");
      boolean _optional = getJsonBoolean(obj, "optional", false);
      int _sort = (int) getJsonLong(obj, "sort", -1);

      ParameterInputType _inputType = getTypedObject(obj, "inputType", new ParameterInputType(InputTypeNames.GENERIC),
          new JsonObjectConverter<ParameterInputType>() {
            @Override
            public ParameterInputType convert(JSONObject jsonobj) {

              Object _name = jsonobj.get("name");
              InputTypeNames name = InputTypeNames.valueOf(_name.toString().toUpperCase());
              String[] values = getTypedArray(jsonobj, "values", String.class, new JsonArrayConverter<String>() {

                @Override
                public String convertElement(Object obj) {

                  if (obj == null) {
                    return "";
                  } else {

                    return obj.toString();
                  }
                }
              });
              return new ParameterInputType(name, values);
            }
          });

      this.definedParameters.add(new CommandParameter(_name, _description, pos++, _optional, _inputType));
    }

  }

  @Override
  public Object exec(String... arguments)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    ScriptEngine engine = Devcon.scriptEngine.get();
    engine.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
    try {

      String fn = FileUtils.readFileToString(this.script, "UTF-8");

      JsCommandModule cm = (JsCommandModule) engine.eval(String.format(loadSource(), fn));
      injectEnvIfCommandModule(cm);
      return cm.exec(arguments);

    } catch (Exception e) {
      this.output.showError(e.getMessage());
      return null;
    }
  }

  @Override
  public Object exec(List<String> arguments)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    String[] args = new String[arguments.size()];
    for (int i = 0; i < arguments.size(); i++) {
      args[i] = arguments.get(i);
    }
    return exec(args);
  }

  @Override
  public Object exec()
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    return exec(new String[0]);
  }

}
