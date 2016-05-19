package com.devonfw.devcon.module.foo;

import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;

/**
 * Class for prototype tests
 *
 * @author pparrado
 */
public class FooTest {

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
    // assertThat(commandCounter).isGreateThan(0);
  }

  @Test
  public void annotationsScanner() {

    Reflections reflections = new Reflections("com.devonfw.devcon.modules");
    Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(CmdModuleRegistry.class);
    System.out.println(annotated.size());
  }
}
