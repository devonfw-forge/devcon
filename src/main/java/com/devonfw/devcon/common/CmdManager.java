package com.devonfw.devcon.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.entity.Response;
import com.devonfw.devcon.common.api.entity.Sentence;
import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.output.OutputConsole;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class CmdManager {

  public Sentence sentence;

  Reflections reflections = new Reflections(ClasspathHelper.forPackage(Constants.MODULES_PACKAGE),
      new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner());

  public CmdManager() {

  }

  public CmdManager(Sentence sentence) {

    this.sentence = sentence;
  }

  public void evaluate() throws Exception {

    boolean moduleExists = false;
    OutputConsole output = new OutputConsole();
    Response response = new Response();
    List<String> argsList = getParamsValues(this.sentence.params);
    List<String> nameArgsList = getParamsKeys(this.sentence.params);
    List<String> commandParamsList = new ArrayList<String>();

    List<Class> modules = getModulesAsClasses();

    for (Class<?> module : modules) {

      Annotation annotation = module.getAnnotation(CmdModuleRegistry.class);
      CmdModuleRegistry cmdModule = (CmdModuleRegistry) annotation;
      if (cmdModule.name().equals(this.sentence.cmdModuleName)) {
        moduleExists = true;

        if (this.sentence.helpRequested) {
          showHelp(module, this.sentence);
          break;
        }

        commandParamsList = getCommandParameters(module, this.sentence.cmd);
        List<String> missingArguments = getMissingArguments(nameArgsList, commandParamsList);

        if (missingArguments.size() > 0 && !this.sentence.noPrompt) {
          promptForMissingArguments(missingArguments, output);
          argsList = getParamsValues(this.sentence.params);
        }

        // Method instance
        Method method = getMethod(module, this.sentence.cmd, argsList);

        if (method != null && method.isAnnotationPresent(Command.class)) {

          method.invoke(module.newInstance(), argsList.toArray());

        } else {
          throw new Exception("The command " + this.sentence.cmd + " with " + argsList.size()
              + " arguments is not recognized as a valid command for " + this.sentence.cmdModuleName
              + " module. Please check the command name and the arguments passed.");
        }
        break;
      }

    }

    if (!moduleExists)
      throw new Exception("The module " + this.sentence.cmdModuleName + " is not recognized as available module");

  }

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
      System.out.println("ERROR: An error occurred trying to obtain the available modules. Message: " + e.getMessage());
      return null;
    }

  }

  public List<Class> getModulesAsClasses() {

    List<Class> modules = new ArrayList<Class>();

    try {
      Set<Class<?>> annotatedClasses = this.reflections.getTypesAnnotatedWith(CmdModuleRegistry.class);

      Iterator<Class<?>> iterator = annotatedClasses.iterator();
      while (iterator.hasNext()) {
        Class<?> currentClass = iterator.next();
        modules.add(currentClass);
      }
      return modules;

    } catch (Exception e) {
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
      Annotation annotation = m.getAnnotation(Command.class);
      Command command = (Command) annotation;
      for (String param : command.parameters()) {
        availableCommandParams.add(param);
      }
    }

    return availableCommandParams;
  }

  private Method getMethod(Class<?> c, String methodName, List<String> argList) {

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

  private Response getCommandInfo(Class<?> c, String commandName) throws Exception {

    Response response = new Response();
    try {
      for (Method m : c.getMethods()) {
        if (m.isAnnotationPresent(Command.class)) {
          if (m.getName().equals(commandName)) {
            Annotation methodAnnotation = m.getAnnotation(Command.class);
            Command com = (Command) methodAnnotation;
            response.commandParamsList = com.parameters();
            response.description = com.help();
            response.name = com.name();
          }
        }
      }

      return response;

    } catch (Exception e) {
      throw new Exception("The command " + this.sentence.cmd + " is not recognized as a valid command for "
          + this.sentence.cmdModuleName + " module.");
    }
  }

  private List<String> getCommandParameters(Class<?> c, String commandName) throws Exception {

    List<String> commandParams = null;
    try {
      for (Method m : c.getMethods()) {
        if (m.isAnnotationPresent(Command.class)) {
          if (m.getName().equals(commandName)) {
            Annotation methodAnnotation = m.getAnnotation(Command.class);
            Command com = (Command) methodAnnotation;
            commandParams = Arrays.asList(com.parameters());
            break;
          }
        }

      }

      return commandParams;
    } catch (Exception e) {
      System.out.println("[ERROR] at getCommandParameters. " + e.getMessage());
      throw e;
    }
  }

  private Response getModuleInfo(Class<?> c, String moduleName) throws Exception {

    Response response = new Response();
    try {
      Annotation classAnnotation = c.getAnnotation(CmdModuleRegistry.class);
      CmdModuleRegistry commandModule = (CmdModuleRegistry) classAnnotation;
      response.description = commandModule.description();
      response.name = commandModule.name();
      response.methodsList = getModuleCommands(c).toArray(new String[0]);

      return response;

    } catch (Exception e) {
      throw e;
    }
  }

  private void showHelp(Class<?> c, Sentence sentence) {

    Response response = new Response();
    OutputConsole output = new OutputConsole();

    try {

      // if command is empty then the module info will be shown
      if (sentence.cmd == null) {

        response = getModuleInfo(c, sentence.cmdModuleName);
        output.showModuleHelp(response);
        // else the command info will be shown.
      } else if (sentence.cmdModuleName != null && sentence.cmd != null) {
        response = getCommandInfo(c, sentence.cmd);
        output.showCommandHelp(response);
      }
    } catch (Exception e) {
      System.out.println("[ERROR] An error occurred trying to show help info. " + e.getMessage());
    }

  }

  private List<String> getModuleCommands(Class<?> c) {

    List<String> commandsList = new ArrayList<String>();
    try {
      for (Method m : c.getMethods()) {
        if (m.isAnnotationPresent(Command.class)) {
          commandsList.add(m.getName());
        }
      }
      return commandsList;
    } catch (Exception e) {
      System.out.println("[ERROR] at getModuleCommands: " + e.getMessage());
      return null;
    }

  }

  private List<String> getMissingArguments(List<String> sentenceArgs, List<String> commandArgs) {

    List<String> missingArguments = new ArrayList<String>();

    if (sentenceArgs.size() == commandArgs.size()) {
      return missingArguments;
    } else {
      for (String commandArg : commandArgs) {
        if (!sentenceArgs.contains(commandArg))
          missingArguments.add(commandArg);
      }
    }
    return missingArguments;
  }

  private void promptForMissingArguments(List<String> missingArguments, OutputConsole output) {

    for (String argument : missingArguments) {
      String value = output.promptForArgument(argument);
      if (!value.isEmpty())
        this.sentence.params.add(createParameterItem(argument, value));
    }
  }

  private List<String> getParamsKeys(List<HashMap<String, String>> params) {

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

  private List<String> getParamsValues(List<HashMap<String, String>> params) {

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

  public static HashMap<String, String> createParameterItem(String key, String value) {

    HashMap<String, String> hmap = new HashMap<String, String>();
    hmap.put(key, value);
    return hmap;
  }
}
