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
import static org.junit.Assert.assertNull;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import com.devonfw.devcon.common.impl.utils.WindowsReqistry;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public class RegistryTest {

  @Test
  public void testRegistry() {

    if (!SystemUtils.IS_OS_WINDOWS){
      return;
    }

    // given
    // set reg value
    WindowsReqistry.writeRegistry("HKCU\\Environment", "RoadRunner", "MeepMeep");

    // check
    String value = WindowsReqistry.readRegistry("HKCU\\Environment", "RoadRunner");
    assertEquals("MeepMeep", value);

    // given
    // Cleanup
    WindowsReqistry.deleteRegistry("HKCU\\Environment", "RoadRunner");
    // check
    value = WindowsReqistry.readRegistry("HKCU\\Environment", "RoadRunner");
    assertNull(null, value);

  }
}
