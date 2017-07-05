package com.devonfw.devcon.common.impl;

import java.lang.annotation.Annotation;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.utils.Constants;

/**
 * Implementation of {@linkplain CommandRegistry}
 *
 * @author ivanderk
 */
public class CommandRegistryImpl extends BaseCommandRegistryImpl {

  private Reflections reflections = new Reflections(ClasspathHelper.forPackage(Constants.MODULES_PACKAGE),
      new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner());

  public CommandRegistryImpl(String pkgName) {
    super();
    registerModules(pkgName);
  }

  public void registerModules(String pkgName) {

    for (Class<?> moduleClass : this.reflections.getTypesAnnotatedWith(CmdModuleRegistry.class)) {

      Annotation annotation = moduleClass.getAnnotation(CmdModuleRegistry.class);
      CmdModuleRegistry moduleAnnotation = (CmdModuleRegistry) annotation;

      CommandModuleInfo cmdmodule = new CommandModuleInfoImpl(moduleAnnotation.name(), moduleAnnotation.description(),
          moduleAnnotation.sort(), moduleAnnotation.visible(), moduleClass);
      getModules().put(cmdmodule.getName(), cmdmodule);
    }
  }
}
