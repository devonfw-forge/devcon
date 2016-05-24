package com.devonfw.devcon.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
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

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 * @since 0.0.1
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

    // System.out.println("--------------------------");
    // System.out.println("gParam: " + this.sentence.gParam);
    // System.out.println("cmdModuleName: " + this.sentence.cmdModuleName);
    // System.out.println("cmd: " + this.sentence.cmd);

    List<String> argsList = new ArrayList<String>();

    Set set = this.sentence.params.entrySet();
    Iterator it = set.iterator();
    while (it.hasNext()) {
      Map.Entry m = (Map.Entry) it.next();
      // System.out.println("Param: " + m.getKey() + " = " + m.getValue());
      argsList.add(m.getValue().toString());
    }

    // System.out.println("context: " + this.sentence.context);
    // System.out.println("noPrompt: " + this.sentence.noPrompt);
    // System.out.println("--------------------------");
    OutputConsole output = new OutputConsole();
    Response response = new Response();
    List<Class> modules = getModulesAsClasses();
    boolean moduleExists = false;
    for (Class module : modules) {

      Annotation annotation = module.getAnnotation(CmdModuleRegistry.class);
      CmdModuleRegistry cmdModule = (CmdModuleRegistry) annotation;
      if (cmdModule.name().equals(this.sentence.cmdModuleName)) {
        moduleExists = true;

        // method annotations (info only?)
        try {
          for (Method m : module.getMethods()) {
            if (m.isAnnotationPresent(Command.class)) {
              if (m.getName().equals(this.sentence.cmd)) {
                Annotation methodAnnotation = m.getAnnotation(Command.class);
                Command com = (Command) methodAnnotation;
                response.commandParamsList = com.parameters();
                response.description = com.help();
                response.name = com.name();
              }
            }
          }

          if (this.sentence.helpRequested)
            output.showCommandHelp(response);

        } catch (Exception e) {
          throw new Exception("The command " + this.sentence.cmd + " is not recognized as a valid command for "
              + this.sentence.cmdModuleName + " module.");
        }

        // Identification of method
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

    // Reflections reflections =
    // new Reflections(ClasspathHelper.forPackage(Constants.MODULES_PACKAGE), new SubTypesScanner(),
    // new TypeAnnotationsScanner(), new MethodAnnotationsScanner());
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

    // Reflections reflections =
    // new Reflections(ClasspathHelper.forPackage(Constants.MODULES_PACKAGE), new SubTypesScanner(),
    // new TypeAnnotationsScanner(), new MethodAnnotationsScanner());
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

    // Reflections reflections =
    // new Reflections(ClasspathHelper.forPackage(Constants.MODULES_PACKAGE), new SubTypesScanner(),
    // new TypeAnnotationsScanner(), new MethodAnnotationsScanner());
    List<String> availableCommandParams = new ArrayList<String>();
    Set<Method> annotatedMethods = this.reflections.getMethodsAnnotatedWith(Command.class);
    // System.out.println(annotatedMethods.size());

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

  private Method getMethod(Class c, String methodName, List<String> argList) {

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
}
