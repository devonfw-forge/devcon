package com.devonfw.devcon.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import com.devonfw.devcon.common.api.utils.Pair;
import com.devonfw.devcon.common.utils.BasicPair;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 */
public class PairTests {

  @Test
  public void testPairs() {

    Pair<String, Integer> p1 = new BasicPair<>("One hundred", 100);
    Pair<String, Integer> p2 = new BasicPair<>("One hundred", 100);

    assertEquals(p1, p2);
    assertTrue(p1.equals(p2));
    assertEquals(p1.hashCode(), p2.hashCode());
    assertEquals("(One hundred, 100)", p1.toString());

    ////////////////////////////////////////////////////////////

    HashMap<String, Integer> somedata1 = new HashMap<>();
    somedata1.put("Two hundred", 200);

    HashMap<String, Integer> somedata2 = new HashMap<>();
    somedata2.put("Two hundred", 200);

    Pair<String, HashMap<String, Integer>> p3 = new BasicPair<>("Two hundred", somedata1);
    Pair<String, HashMap<String, Integer>> p4 = new BasicPair<>("Two hundred", somedata2);

    assertEquals(p3, p4);
    assertTrue(p3.equals(p4));
    assertEquals(p3.hashCode(), p4.hashCode());

    assertEquals("(Two hundred, {Two hundred=200})", p3.toString());

  }
}
