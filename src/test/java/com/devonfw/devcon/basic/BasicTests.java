package com.devonfw.devcon.basic;

import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
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
import com.devonfw.devcon.common.impl.AbstractCommandHolder;
import com.devonfw.devcon.modules.Foo;

/**
 * Tests basic application functionalities.
 *
 * @author pparrado
 */
public class BasicTests {
  Reflections reflections;

  @SuppressWarnings("javadoc")
  @Before
  public void init() {

    // This way only works for classes
    // this.reflections = new Reflections("com.devonfw.devcon.modules");

    this.reflections =
        new Reflections(ClasspathHelper.forPackage("com.devonfw.devcon.modules"), new SubTypesScanner(),
            new TypeAnnotationsScanner(), new MethodAnnotationsScanner());

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

    // TODO implement AssertJ
    // assertThat(annotatedClasses.size()).isGreaterThan(0);
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
      if (module.name().equals("foo") && module.context().equals("MyContextIsNotGlobal")
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

  /**
   * Tests the getCommands method of the {@link AbstractCommandHolder} class
   */
  @Test
  public void getCommandsBySuperClass() {

    Foo f = new Foo();
    List<Command> commands = f.getCommands();
    assertTrue(commands.size() > 0);
  }

  /**
   * Tests the capability to get an annotation using the {@link AbstractCommandHolder} class
   */
  @Test
  public void getCommandsAnnotationBySuperClass() {

    boolean result = true;
    Foo f = new Foo();
    List<Command> commands = f.getCommands();
    for (Command command : commands) {
      if (command.help().isEmpty() || command.help() == null) {
        result = false;
        break;
      }
    }

    assertTrue(result);
  }
}
