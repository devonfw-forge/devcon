package com.devonfw.devcon.module.foo;

import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.modules.Foo;

/**
 * Class for prototype tests
 *
 * @author pparrado
 */
public class FooTest {

  Reflections reflections;

  Reflections reflections2;

  @SuppressWarnings("javadoc")
  @Before
  public void init() {

    // This way only works for classes
    // this.reflections = new Reflections("com.devonfw.devcon.modules");

    this.reflections =
        new Reflections(ClasspathHelper.forPackage("com.devonfw.devcon.modules"), new SubTypesScanner(),
            new TypeAnnotationsScanner(), new MethodAnnotationsScanner());

  }

  @SuppressWarnings({ "javadoc" })
  @Test
  public void oneCommandAtLeast() throws SecurityException, ClassNotFoundException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, InstantiationException {

    int commandCounter = 0;

    Class<?> obj = Class.forName("com.devonfw.devcon.modules.Foo");

    if (obj.isAnnotationPresent(CmdModuleRegistry.class)) {
      System.out.println("class recognized... " + obj.getName());
      Annotation ann = obj.getAnnotation(CmdModuleRegistry.class);
      CmdModuleRegistry module = (CmdModuleRegistry) ann;
      System.out.println("ModuleName: " + module.name());
      System.out.println("ModuleContext: " + module.context());
      System.out.println("Deprecated: " + module.deprecated());

      for (Method m : obj.getMethods()) {

        if (m.isAnnotationPresent(Command.class)) {
          commandCounter++;
          System.out.println(commandCounter + " - This method is a @Command: " + m.getName());
          System.out.println("The @Command " + m.getName() + " says " + m.invoke(obj.newInstance()));
        }

      }
    }

    assertTrue(commandCounter > 0);

    // TODO implement AssertJ
    // assertThat(commandCounter).isGreaterThan(0);
  }

  /**
   * Tests if the scanner for annotated classes is working.
   */
  @Test
  public void classScanner() {

    Set<Class<?>> annotatedClasses = this.reflections.getTypesAnnotatedWith(CmdModuleRegistry.class);

    System.out.println(annotatedClasses.size());
    Object[] a = annotatedClasses.toArray();

    for (int i = 0; i < a.length; i++) {
      System.out.println(a.toString());
      Class<?> currentClass = a.getClass();
      System.out.println(currentClass.getName());
    }

    assertTrue(annotatedClasses.size() > 0);
  }

  /**
   * Tests if the scanner for annotated methods is working.
   */
  @Test
  public void methodScanner() {

    Set<Method> annotatedMethods = this.reflections.getMethodsAnnotatedWith(Command.class);
    System.out.println(annotatedMethods.size());

    Iterator<Method> iterator = annotatedMethods.iterator();
    while (iterator.hasNext()) {
      System.out.println(iterator.next().getName());
    }

    assertTrue(annotatedMethods.size() > 0);

  }

  /**
   * Tests if specific annotated parameters can be obtained from a specific annotated class
   *
   * @throws ClassNotFoundException if the referenced class is not found.
   */
  @Test
  public void moduleAnnotationParameters() throws ClassNotFoundException {

    Class<?> obj = Class.forName("com.devonfw.devcon.modules.Foo");
    boolean result = false;
    if (obj.isAnnotationPresent(CmdModuleRegistry.class)) {
      Annotation annotation = obj.getAnnotation(CmdModuleRegistry.class);
      CmdModuleRegistry module = (CmdModuleRegistry) annotation;
      if (module.name().equals("MyNameIsModuleFoo") && module.context().equals("MyContextIsNotGlobal")
          && module.deprecated() == false) {
        result = true;
      }
    }

    assertTrue(result);
  }

  /**
   * Tests if a specific annotation parameter can be obtained from a specific annotated method
   *
   * @throws ClassNotFoundException if the referenced class is not found.
   * @throws SecurityException
   * @throws NoSuchMethodException if the referenced method is not found.
   */
  @Test
  public void methodAnnotationParameters() throws ClassNotFoundException, NoSuchMethodException, SecurityException {

    boolean result = false;
    Class<?> obj = Class.forName("com.devonfw.devcon.modules.Foo");
    Method method = obj.getMethod("farewell");
    if (method != null) {
      if (method.isAnnotationPresent(Command.class)) {
        Annotation methodAnnotation = method.getAnnotation(Command.class);
        Command com = (Command) methodAnnotation;
        if (!com.help().isEmpty())
          result = true;
      }
    }

    assertTrue(result);

  }

  @Test
  public void toughImplementation() {

    Foo f = new Foo();
    List<Command> commands = f.getCommands();
    for (Command command : commands) {
      System.out.println(command.help());
    }
  }
}
