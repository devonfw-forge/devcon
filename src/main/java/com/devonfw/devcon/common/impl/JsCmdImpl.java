package com.devonfw.devcon.common.impl;

import static com.devonfw.devcon.common.utils.JsonValues.getJsonBoolean;
import static com.devonfw.devcon.common.utils.JsonValues.getJsonLong;
import static com.devonfw.devcon.common.utils.JsonValues.getJsonString;
import static com.devonfw.devcon.common.utils.JsonValues.getTypedArray;
import static com.devonfw.devcon.common.utils.JsonValues.getTypedObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

  private File script;

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

    return null;
  }

  @Override
  public Object exec(List<String> arguments)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    // Object module = this.module.newInstance();
    // injectEnvIfCommandModule(module);

    return null;
  }

  @Override
  public Object exec()
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    // Object module = this.module.newInstance();
    // injectEnvIfCommandModule(module);

    return null;

  }

}
