package com.devonfw.devcon.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 */
public class PairTests {

  @Test
  public void testPairs() {

    Pair<String, Integer> p1 = Pair.of("One hundred", 100);
    Pair<String, Integer> p2 = Pair.of("One hundred", 100);

    assertEquals(p1, p2);
    assertTrue(p1.equals(p2));
    assertEquals(p1.hashCode(), p2.hashCode());
    System.out.println(p1.toString());
    assertEquals("(One hundred,100)", p1.toString());

    ////////////////////////////////////////////////////////////

    HashMap<String, Integer> somedata1 = new HashMap<>();
    somedata1.put("Two hundred", 200);

    HashMap<String, Integer> somedata2 = new HashMap<>();
    somedata2.put("Two hundred", 200);

    Pair<String, HashMap<String, Integer>> p3 = Pair.of("Two hundred", somedata1);
    Pair<String, HashMap<String, Integer>> p4 = Pair.of("Two hundred", somedata2);

    assertEquals(p3, p4);
    assertTrue(p3.equals(p4));
    assertEquals(p3.hashCode(), p4.hashCode());

    assertEquals("(Two hundred,{Two hundred=200})", p3.toString());

  }
}
