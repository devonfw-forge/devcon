package com.devonfw.devcon.common.utils;

import java.awt.Desktop;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.DevconOption;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.output.ConsoleOutput;
import com.devonfw.devcon.output.Output;
import com.google.common.base.Optional;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class DevconUtils {

  Reflections reflections = new Reflections(ClasspathHelper.forPackage(Constants.MODULES_PACKAGE),
      new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner());

  private static final String DEVON_JSON = "devon.json";

  private static final String OPTIONAL = "optionalParameters";

  public String promptForMissingParameter(String missingParameter, Output output) {

    String result = "";
    String value = output.promptForArgument(missingParameter);
    if (!value.isEmpty()) {
      result = value;
    }
    return result;
  }

  public Sentence obtainValueForMissingParameters(List<CommandParameter> missingParameters, Sentence sentence,
      Output output) throws FileNotFoundException, IOException, ParseException {

    for (CommandParameter parameter : missingParameters) {
      String value = "";
      if (parameter.isOptional()) {
        value = getOptionalValueFromFile(parameter.getName());
        if (value == "" && !sentence.isNoPrompt()) {
          value = promptForMissingParameter(parameter.getName(), output);
        }
        if (value != "")
          sentence.getParams().add(Pair.of(parameter.getName(), value));
      } else {
        if (!sentence.isNoPrompt()) {
          value = promptForMissingParameter(parameter.getName(), output);
          sentence.getParams().add(Pair.of(parameter.getName(), value));
        }
      }
    }

    return sentence;
  }

  public String getOptionalValueFromFile(String parameterName)
      throws FileNotFoundException, IOException, ParseException {

    String paramValue = "";
    try {
      ContextPathInfo contextPathInfo = new ContextPathInfo();
      Optional<ProjectInfo> info = contextPathInfo.getCombinedProjectRoot();

      if (info.isPresent()) {
        Path jsonPath = info.get().getPath().resolve(DEVON_JSON);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(jsonPath.toFile()));

        JSONObject json = (JSONObject) obj;
        JSONObject optParams = (JSONObject) json.get(OPTIONAL);

        if (optParams != null) {
          try {
            paramValue = optParams.get(parameterName).toString();
          } catch (Exception e) {

          }
        }

      }
      return paramValue;

    } catch (FileNotFoundException e) {
      // TODO implement logs
      // System.out.println("[LOG] The config file for optional parameters could not be found.");
      return "";
    } catch (Exception e) {
      // TODO implement logs
      System.out.println("[LOG] " + e.getMessage());
      return "";
    }

  }

  public void endAndShowMissingParameters(List<CommandParameter> missingParameters) throws Exception {

    Output output = new ConsoleOutput();
    StringBuilder sb = new StringBuilder();
    for (CommandParameter missingParameter : missingParameters) {
      sb.append("[-");
      sb.append(missingParameter.getName());
      sb.append("] ");
    }
    throw new Exception("You need to specify the following parameter/s: " + sb.toString());
  }

  public List<String> getParamsKeys(List<Pair<String, String>> params) {

    List<String> keysList = new ArrayList<String>();

    for (Pair<String, String> param : params) {

      keysList.add(param.getLeft());
    }

    return keysList;
  }

  public List<String> getParamsValues(List<Pair<String, String>> params) {

    List<String> valuesList = new ArrayList<String>();

    for (Pair<String, String> param : params) {

      valuesList.add(param.getRight());
    }
    return valuesList;
  }

  public List<String> orderParameters(List<Pair<String, String>> sentenceParams, List<CommandParameter> commandParams) {

    List<String> orderedParameters = new ArrayList<String>();
    for (CommandParameter commandParam : commandParams) {
      for (Pair<String, String> sentenceParam : sentenceParams) {
        if (sentenceParam.getLeft().equals(commandParam.getName())) {
          orderedParameters.add(sentenceParam.getRight());
          break;
        }
      }
    }
    return orderedParameters;
  }

  public List<DevconOption> getGlobalOptions() {

    List<DevconOption> globalOptions = new ArrayList<DevconOption>();

    try {
      ClassLoader classLoader = getClass().getClassLoader();
      URL globalParamsFileURL = classLoader.getResource(Constants.GLOBAL_PARAMS_FILE);

      if (globalParamsFileURL != null) {

        globalOptions = getGlobalOptionsFromFile(globalParamsFileURL);

      } else {
        // TODO implement logs
        System.out.println("Getting the default global options...");
        globalOptions = getDefaultGlobalOptions();
      }

      return globalOptions;
    } catch (Exception e) {
      System.out.println("ERROR: " + e.getMessage());
      return globalOptions;
    }

  }

  private List<DevconOption> getDefaultGlobalOptions() {

    List<DevconOption> defaultGlobalOptions = new ArrayList<DevconOption>();

    DevconOption h = new DevconOption("h", "help", "show help info for each module/command");
    DevconOption np = new DevconOption("np", "noprompt", "the process will not ask for user input");
    DevconOption v = new DevconOption("v", "version", "show devcon version");

    defaultGlobalOptions.add(h);
    defaultGlobalOptions.add(np);
    defaultGlobalOptions.add(v);

    return defaultGlobalOptions;
  }

  private List<DevconOption> getGlobalOptionsFromFile(URL fileURL)
      throws FileNotFoundException, IOException, ParseException {

    JSONParser parser = new JSONParser();
    List<DevconOption> globalOptions = new ArrayList<>();

    String jsonPath = fileURL.getPath();
    Object obj = parser.parse(new FileReader(jsonPath));
    JSONArray json = (JSONArray) obj;

    Iterator<Object> it = json.iterator();

    while (it.hasNext()) {
      try {
        JSONObject j = (JSONObject) it.next();

        String opt = j.get("opt") != null ? j.get("opt").toString() : " ";
        String longOpt = j.get("longOpt") != null ? j.get("longOpt").toString() : " ";
        String description = j.get("description") != null ? j.get("description").toString() : " ";

        globalOptions.add(new DevconOption(opt, longOpt, description));

      } catch (Exception e) {
        // TODO implement logs
        System.out.println("Error reading a global option. Please check the global options file.");
      }

    }

    return globalOptions;
  }

  /**
   *
   * @param url Web page to open in Desktop browser
   * @return indication whether on Desktop i.e. whether web browser could be accessed
   *
   */
  public boolean openUri(String url) {

    if (Desktop.isDesktopSupported()) {
      try {
        Desktop.getDesktop().browse(new URI(url));
        return true;
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
    } else {
      return false;
    }
  }
}
