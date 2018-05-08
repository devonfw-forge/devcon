/*******************************************************************************
 * Copyright 2015-2018 Capgemini SE.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package com.devonfw.devcon.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import com.devonfw.devcon.common.CommandResult;
import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandManager;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.impl.CommandManagerImpl;
import com.devonfw.devcon.common.impl.CommandRegistryImpl;
import com.devonfw.devcon.input.ConsoleInput;
import com.devonfw.devcon.output.ConsoleOutput;

/**
 * Tests basic application functionalities.
 *
 * @author pparrado
 */
public class BasicTest {
  Reflections reflections;

  CommandRegistry registry;

  CommandManager cmdMgr;

  @SuppressWarnings("javadoc")
  @Before
  public void setup() {

    // This way only works for classes
    // this.reflections = new Reflections("com.devonfw.devcon.modules");

    this.reflections = new Reflections(ClasspathHelper.forPackage("com.devonfw.devcon.modules"), new SubTypesScanner(),
        new TypeAnnotationsScanner(), new MethodAnnotationsScanner());

    this.registry = new CommandRegistryImpl("com.devonfw.devcon.modules.*");
    this.cmdMgr = new CommandManagerImpl(this.registry, new ConsoleInput(), new ConsoleOutput());

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

    Set<Method> annotatedMethods =
        this.reflections.getMethodsAnnotatedWith(com.devonfw.devcon.common.api.annotations.Command.class);
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

    Class<?> obj = Class.forName("com.devonfw.devcon.modules.foo.Foo");
    boolean result = false;
    if (obj.isAnnotationPresent(CmdModuleRegistry.class)) {
      Annotation annotation = obj.getAnnotation(CmdModuleRegistry.class);
      CmdModuleRegistry module = (CmdModuleRegistry) annotation;
      if (module.name().equals("foo") /* && module.context().equals("fooContext") */) {
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
    Class<?> obj = Class.forName("com.devonfw.devcon.modules.foo.Foo");
    Method method = obj.getMethod("farewell");
    if (method != null) {
      if (method.isAnnotationPresent(com.devonfw.devcon.common.api.annotations.Command.class)) {
        Annotation methodAnnotation = method.getAnnotation(com.devonfw.devcon.common.api.annotations.Command.class);
        com.devonfw.devcon.common.api.annotations.Command com =
            (com.devonfw.devcon.common.api.annotations.Command) methodAnnotation;
        if (!com.description().isEmpty())
          result = true;
      }
    }

    assertTrue(result);

  }

  ///////////////////////////
  //// After refactoring
  //////////////////////////

  @Test
  public void testCommandRegistry() {

    // given setup registry with Foo module

    // then
    assertTrue(this.registry.getCommandModule("foo").isPresent());
    assertFalse(this.registry.getCommandModule("fooNotPresent").isPresent());

    // given
    CommandModuleInfo module = this.registry.getCommandModule("foo").get();

    // then
    assertEquals("foo", module.getName());
    assertTrue(module.getCommand("greeting").isPresent());
    assertFalse(module.getCommand("greetingNotPresent").isPresent());

    // given
    Command cmd = module.getCommand("largeCustomFarewell").get();

    // then
    assertEquals("largeCustomFarewell", cmd.getName());
    assertEquals(2, cmd.getDefinedParameters().size());

    // given
    List<CommandParameter> params_ = new ArrayList<>(cmd.getDefinedParameters());
    assertEquals("surname", params_.get(1).getName());

  }

  @Test
  public void testExceptionHandling() {

    // given
    Pair<CommandResult, Object> result = this.cmdMgr.execCommand("foo", "generateError");

    // then
    Assert.assertNotNull(result.getRight());
    Assert.assertEquals(CommandResult.FAILURE, result.getLeft());
    Assert.assertEquals("BOOM!!!", ((Error) result.getRight()).getMessage());

  }
}
