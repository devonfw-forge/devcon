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
package com.devonfw.devcon.common.utils;

import java.nio.file.Path;

import com.devonfw.devcon.common.api.utils.FolderProcessor;

/**
 * Class which offers a single method which allows for a filesystem path to be climbed "upward" from a leave node to the
 * Root node. Instead of a recursive downward "walker", this implementation climbed vertically without traversing
 * siblings.
 *
 * @author ivanderk
 */
public class TreeClimber {

  /**
   * calls {@link FolderProcessor} for each folder encounters while "climbing" upward from {@link path} to the Root node
   *
   * @param path Path to climb from
   * @param processor delegate to processor which is called for each folder
   */
  public static void climb(Path path, FolderProcessor processor) {

    Path _path = path.toAbsolutePath();
    Path root = path.getRoot();

    while (processor.onFolder(_path)) {
      if (_path.equals(root)) {
        break;
      }
      _path = _path.getParent();
    }

  }
}
