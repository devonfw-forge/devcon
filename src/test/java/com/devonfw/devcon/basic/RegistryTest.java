package com.devonfw.devcon.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
