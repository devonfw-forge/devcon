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
 * Implementation of {@link Command}
 *
 * @author ivanderk
 */
public class JsCmdImpl extends BaseCmdImpl {

  private static final String source = JsCmdImpl.loadSource();

  private File script;

  public static String loadSource() {

    String root = (Devcon.IN_EXEC_JAR) ? "resources/" : "";
    URL commandsJsonUrl = ClassLoader.getSystemClassLoader().getResource(root + "jstemplate.txt");

    try {
      return FileUtils.readFileToString(new File(commandsJsonUrl.toURI()), "UTF-8");
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
      return null;
    }
  }

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
      JsCommandModule cm = (JsCommandModule) engine.eval(String.format(source, fn));
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
