package com.devonfw.devcon.common.utils;

import java.awt.Desktop;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.DevconOption;
import com.devonfw.devcon.common.api.data.Info;
import com.devonfw.devcon.common.api.data.Response;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.exception.NotRecognizedCommandException;
import com.devonfw.devcon.output.OutputConsole;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class DevconUtils {

  Reflections reflections = new Reflections(ClasspathHelper.forPackage(Constants.MODULES_PACKAGE),
      new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner());

  public List<CmdModuleRegistry> getAvailableModules() {

    List<CmdModuleRegistry> modules = new ArrayList<CmdModuleRegistry>();

    try {
      Set<Class<?>> annotatedClasses = this.reflections.getTypesAnnotatedWith(CmdModuleRegistry.class);

      Iterator<Class<?>> iterator = annotatedClasses.iterator();
      while (iterator.hasNext()) {
        Class<?> currentClass = iterator.next();
        Annotation annotation = currentClass.getAnnotation(CmdModuleRegistry.class);
        CmdModuleRegistry module = (CmdModuleRegistry) annotation;
        modules.add(module);
      }

      return modules;

    } catch (Exception e) {
      // TODO implement logs
      System.out.println("ERROR: An error occurred trying to obtain the available modules. Message: " + e.getMessage());
      return null;
    }

  }

  public List<Class<?>> getModulesAsClasses() {

    List<Class<?>> modules = new ArrayList<Class<?>>();

    try {
      Set<Class<?>> annotatedClasses = this.reflections.getTypesAnnotatedWith(CmdModuleRegistry.class);

      Iterator<Class<?>> iterator = annotatedClasses.iterator();
      while (iterator.hasNext()) {
        Class<?> currentClass = iterator.next();
        modules.add(currentClass);
      }
      return modules;

    } catch (Exception e) {
      // TODO implement logs
      System.out.println("ERROR: An error occurred trying to obtain the available modules. Message: " + e.getMessage());
      return null;
    }
  }

  public List<String> getAvailableCommandParameters() {

    List<String> availableCommandParams = new ArrayList<String>();
    Set<Method> annotatedMethods = this.reflections.getMethodsAnnotatedWith(Command.class);

    Iterator<Method> iterator = annotatedMethods.iterator();
    while (iterator.hasNext()) {
      Method m = iterator.next();
      Annotation annotation = m.getAnnotation(Parameters.class);
      if (annotation != null) {
        Parameters params = (Parameters) annotation;
        List<Parameter> paramsList = Arrays.asList(params.values());
        for (Parameter param : paramsList) {
          availableCommandParams.add(param.name());
        }
      }
    }

    return availableCommandParams;
  }

  public Method getCommandInstance(Class<?> c, String methodName, List<String> argList) {

    try {

      Class<?> params[] = new Class[argList.size()];
      for (int i = 0; i < argList.size(); i++) {
        params[i] = String.class;
      }

      Method method = c.getMethod(methodName, params);

      return method;

    } catch (Exception e) {
      return null;
    }
  }

  public Response getCommandInfo(Class<?> c, String commandName) {

    Response response = new Response();
    for (Method m : c.getMethods()) {
      if (m.isAnnotationPresent(Command.class)) {
        if (m.getName().equals(commandName)) {
          Annotation commandAnnotation = m.getAnnotation(Command.class);
          Command com = (Command) commandAnnotation;
          Annotation paramsAnnotation = m.getAnnotation(Parameters.class);
          if (paramsAnnotation != null) {
            Parameters params = (Parameters) paramsAnnotation;
            response.setCommandParamsList(Arrays.asList(params.values()));
          } else {
            response.setCommandParamsList(new ArrayList<Parameter>());
          }
          response.setDescription(com.help());
          response.setName(com.name());
          break;
        }
      }
    }

    return response;
  }

  public List<String> getCommandParameters(Class<?> c, String commandName) throws Exception {

    List<String> commandParams = null;
    try {
      for (Method m : c.getMethods()) {
        if (m.isAnnotationPresent(Command.class)) {
          if (m.getName().equals(commandName)) {
            Annotation annotation = m.getAnnotation(Parameters.class);
            if (annotation != null) {
              Parameters params = (Parameters) annotation;
              commandParams = new ArrayList<String>();
              List<Parameter> paramsList = Arrays.asList(params.values());
              for (Parameter param : paramsList) {
                if (!param.name().equals(""))
                  commandParams.add(param.name());
              }
            }

            break;
          }
        }

      }

      return commandParams;
    } catch (Exception e) {
      // TODO implement logs
      System.out.println("[ERROR] at getCommandParameters. " + e.getMessage());
      throw e;
    }
  }

  public Response getModuleInfo(Class<?> c, String moduleName) throws Exception {

    Response response = new Response();
    try {
      Annotation classAnnotation = c.getAnnotation(CmdModuleRegistry.class);
      CmdModuleRegistry commandModule = (CmdModuleRegistry) classAnnotation;
      response.setDescription(commandModule.description());
      response.setName(commandModule.name());
      response.setCommandsList(getModuleCommands(c));

      return response;

    } catch (Exception e) {
      throw e;
    }
  }

  public void showHelp(Class<?> c, Sentence s) throws Exception {

    Response response = new Response();
    OutputConsole output = new OutputConsole();

    // if command is empty then the module info will be shown
    if (s.getCommandName() == null) {

      response = getModuleInfo(c, s.getModuleName());
      output.showModuleHelp(response);
      // else the command info will be shown.
    } else if (s.getModuleName() != null && s.getCommandName() != null) {
      Command com = getCommand(c, s.getCommandName());
      if (com == null)
        throw new NotRecognizedCommandException(s.getModuleName(), s.getCommandName());
      response = getCommandInfo(c, s.getCommandName());
      output.showCommandHelp(response);
    }

  }

  public List<Info> getModuleCommands(Class<?> c) {

    List<Info> commandsList = new ArrayList<Info>();
    try {
      for (Method m : c.getMethods()) {
        if (m.isAnnotationPresent(Command.class)) {
          Info info = new Info();

          Annotation annotation = m.getAnnotation(Command.class);
          Command comm = (Command) annotation;

          info.setName(comm.name() != null ? comm.name() : "");
          info.setDescription(comm.help() != null ? comm.help() : "");
          commandsList.add(info);
        }
      }
      return commandsList;
    } catch (Exception e) {
      // TODO implement logs
      System.out.println("[ERROR] at getModuleCommands: " + e.getMessage());
      return null;
    }

  }

  public List<String> getMissingParameters(List<String> sentenceParams, List<String> commandParams) {

    List<String> missingArguments = new ArrayList();

    for (String commandArg : commandParams) {
      if (!sentenceParams.contains(commandArg))
        missingArguments.add(commandArg);
    }

    return missingArguments;
  }

  public Sentence promptForMissingArguments(List<String> missingArguments, Sentence sentence, OutputConsole output) {

    for (String argument : missingArguments) {
      String value = output.promptForArgument(argument);
      if (!value.isEmpty())
        sentence.getParams().add(createParameterItem(argument, value));
    }
    return sentence;
  }

  public List<String> getParamsKeys(List<HashMap<String, String>> params) {

    List<String> keysList = new ArrayList<String>();

    for (HashMap<String, String> param : params) {
      Set<?> set = param.entrySet();
      Iterator<?> it = set.iterator();
      while (it.hasNext()) {
        Map.Entry m = (Map.Entry) it.next();
        keysList.add(m.getKey().toString());
      }
    }
    return keysList;
  }

  public List<String> getParamsValues(List<HashMap<String, String>> params) {

    List<String> valuesList = new ArrayList<String>();

    for (HashMap<String, String> param : params) {
      Set<?> set = param.entrySet();
      Iterator<?> it = set.iterator();
      while (it.hasNext()) {
        Map.Entry m = (Map.Entry) it.next();
        valuesList.add(m.getValue().toString());
      }
    }
    return valuesList;
  }

  public HashMap<String, String> createParameterItem(String key, String value) {

    HashMap<String, String> hmap = new HashMap<String, String>();
    hmap.put(key, value);
    return hmap;
  }

  public Class<?> getModule(String moduleName) {

    List<Class<?>> modules = getModulesAsClasses();
    Class<?> matchClass = null;

    for (Class<?> module : modules) {

      Annotation annotation = module.getAnnotation(CmdModuleRegistry.class);
      CmdModuleRegistry cmdModule = (CmdModuleRegistry) annotation;
      if (cmdModule.name().equals(moduleName)) {
        matchClass = module;
        break;
      }
    }
    return matchClass;
  }

  public Command getCommand(Class<?> module, String commandName) {

    Command command = null;

    for (Method m : module.getMethods()) {
      if (m.isAnnotationPresent(Command.class)) {
        if (m.getName().equals(commandName)) {
          Annotation methodAnnotation = m.getAnnotation(Command.class);
          command = (Command) methodAnnotation;
          break;
        }
      }
    }

    return command;
  }

  public List<String> orderParameters(List<HashMap<String, String>> sentenceParams, List<String> commandParams) {

    List<String> orderedParameters = new ArrayList<String>();
    for (String commandParam : commandParams) {
      for (HashMap<String, String> sentenceParam : sentenceParams) {
        if (sentenceParam.containsKey(commandParam)) {
          orderedParameters.add(sentenceParam.get(commandParam));
          break;
        }
      }

    }
    return orderedParameters;
  }

  public void LaunchCommand(Class<?> c, String commandName, List<String> parameters)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

    Method method = getCommandInstance(c, commandName, parameters);
    method.invoke(c.newInstance(), parameters.toArray());
  }

  public List<Info> getListOfAvailableModules() {

    List<Info> modules = new ArrayList();
    try {
      Set<Class<?>> annotatedClasses = this.reflections.getTypesAnnotatedWith(CmdModuleRegistry.class);

      Iterator<Class<?>> iterator = annotatedClasses.iterator();
      while (iterator.hasNext()) {
        Class<?> currentClass = iterator.next();

        Annotation annotation = currentClass.getAnnotation(CmdModuleRegistry.class);
        CmdModuleRegistry module = (CmdModuleRegistry) annotation;
        if (module.name() != null) {
          Info info = new Info();
          info.setName(module.name());
          info.setDescription(module.description() != null ? module.description() : "");
          modules.add(info);
        }
      }

      return modules;

    } catch (Exception e) {
      System.out.println("An error occurred trying to obtain the available modules. Message: " + e.getMessage());
      return modules;
    }
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
    List<DevconOption> globalOptions = new ArrayList<DevconOption>();

    String jsonPath = fileURL.getPath();
    Object obj = parser.parse(new FileReader(jsonPath));
    // ****************

    // InputStream in = DevconUtils.class.getResourceAsStream("/" + Constants.GLOBAL_PARAMS_FILE);
    // StringWriter writer = new StringWriter();
    // IOUtils.copy(in, writer, StandardCharsets.UTF_8);
    // String s = writer.toString();
    // System.out.println(s);
    // System.out.println(s.length());

    // URL url = DevconUtils.class.getResource("/" + Constants.GLOBAL_PARAMS_FILE);
    // Object obj = parser.parse(new FileReader(url.getPath()));

    // ***************
    // File f = new File(DevconUtils.class.getResource("/" + Constants.GLOBAL_PARAMS_FILE).toURI());
    // Object obj = parser.parse(new FileReader(f));
    // ***************

    // Object obj = null;
    // try {
    // obj = parser.parse(s);
    // } catch (Exception e) {
    // System.out.println(e.getMessage());
    // }

    // ------------------------------------------
    // WORKS WITH BOTH COMMAND LINE AND ECLIPSE
    // Object obj = parser.parse(new FileReader("C:\\Temp\\" + Constants.GLOBAL_PARAMS_FILE));
    // ------------------------------------------

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
