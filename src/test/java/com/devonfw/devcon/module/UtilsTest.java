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
package com.devonfw.devcon.module;

import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.common.utils.Utils;

/**
 * Tests the Utils module
 *
 * @author dsanchez
 */
public class UtilsTest {

  @SuppressWarnings("javadoc")
  @Before
  public void setup() {

  }

  @Test
  public void testGetTemplateVersion() throws URISyntaxException {

    String configPath = this.getClass().getResource("").getPath().replace("com/devonfw/devcon/module/", "")
        + Constants.VERSION_PARAMS_FILE_FULL_PATH;
    String templateVersion = Utils.getTemplateVersion(configPath);
    assertTrue(templateVersion.equals("3.0.0"));
  }

  @Test
  public void testRemoveEndingDot() throws URISyntaxException {

    String path = this.getClass().getResource("").getPath() + ".";
    path = Utils.removeEndingDot(path);
    assertTrue(path.equals(this.getClass().getResource("").getPath()));
    path = Utils.removeEndingDot(path);
    assertTrue(path.equals(this.getClass().getResource("").getPath()));
  }

  @After
  public void end() {

  }

}
