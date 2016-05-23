package com.devonfw.devcon.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.entity.Sentence;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 * @since 0.0.1
 */
public class CmdManager {

  public Sentence sentence;

  public CmdManager(Sentence sentence) {

    this.sentence = sentence;
  }

  public void evaluate() throws Exception {

    System.out.println("gParam: " + this.sentence.gParam);
    System.out.println("cmdModuleName: " + this.sentence.cmdModuleName);
    System.out.println("cmd: " + this.sentence.cmd);
    for (String param : this.sentence.params) {
      System.out.println("params: " + param);
    }
    System.out.println("context: " + this.sentence.context);

    List<Class> modules = getModulesAsClasses();
    boolean moduleExists = false;
    for (Class module : modules) {

      Annotation annotation = module.getAnnotation(CmdModuleRegistry.class);
      CmdModuleRegistry cmdModule = (CmdModuleRegistry) annotation;
      if (cmdModule.name().equals(this.sentence.cmdModuleName)) {
        moduleExists = true;
        // Method method = module.getMethod(this.sentence.cmd);
        Method method = getMethod(module, this.sentence.cmd);
        if (method != null) {

          System.out.println(method.invoke(module.newInstance(), null));
        }
        break;
      }

    }

    if (!moduleExists)
      throw new Exception("The module " + this.sentence.cmdModuleName + " is not recognized as available module");

  }

  public static List<CmdModuleRegistry> getAvailableModules() {

    Reflections reflections =
        new Reflections(ClasspathHelper.forPackage("com.devonfw.devcon.modules"), new SubTypesScanner(),
            new TypeAnnotationsScanner(), new MethodAnnotationsScanner());
    List<CmdModuleRegistry> modules = new ArrayList<CmdModuleRegistry>();

    try {
      Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CmdModuleRegistry.class);

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

  public static List<Class> getModulesAsClasses() {

    Reflections reflections =
        new Reflections(ClasspathHelper.forPackage("com.devonfw.devcon.modules"), new SubTypesScanner(),
            new TypeAnnotationsScanner(), new MethodAnnotationsScanner());
    List<Class> modules = new ArrayList<Class>();

    try {
      Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CmdModuleRegistry.class);

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

  private Method getMethod(Class c, String methodName) {

    try {
      Method method = c.getMethod(methodName);
      return method;
    } catch (Exception e) {
      return null;
    }
  }
}
