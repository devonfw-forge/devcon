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
