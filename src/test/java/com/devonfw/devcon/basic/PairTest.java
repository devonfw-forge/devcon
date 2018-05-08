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

import static com.devonfw.devcon.common.utils.Utils.mapToPairs;
import static com.devonfw.devcon.common.utils.Utils.pairsToMap;
import static com.devonfw.devcon.common.utils.Utils.unzipList;
import static com.devonfw.devcon.common.utils.Utils.zipLists;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 */
public class PairTest {

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

  @Test
  public void testZip() {

    // given
    List<String> left = new ArrayList<>(
        Arrays.asList(new String[] { "Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine" }));
    List<Integer> right = new ArrayList<>(Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }));

    // when
    List<Pair<String, Integer>> zipped = zipLists(left, right);

    // then
    assertEquals(zipped.get(3).getLeft(), "Three");
    assertEquals(zipped.get(1).getRight(), (Integer) 1);
    assertEquals(zipped.size(), 10);

    // when
    Pair<List<String>, List<Integer>> unzipped = unzipList(zipped);

    // then
    assertEquals(unzipped.getLeft().get(3), "Three");
    assertEquals(unzipped.getRight().get(1), (Integer) 1);
    assertEquals(unzipped.getLeft().size(), 10);

  }

  @Test
  public void testMap() {

    // given
    List<String> left = new ArrayList<>(
        Arrays.asList(new String[] { "Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine" }));
    List<Integer> right = new ArrayList<>(Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }));

    // when
    List<Pair<String, Integer>> zipped = zipLists(left, right);
    Map<String, Integer> map = pairsToMap(zipped);

    // then
    assertEquals((Integer) 3, map.get("Three"));
    assertEquals((Integer) 1, map.get("One"));

    // when
    zipped = mapToPairs(map);

    // then
    assertEquals(zipped.get(3).getLeft(), "Three");
    assertEquals(zipped.get(1).getRight(), (Integer) 1);
    assertEquals(zipped.size(), 10);

  }

}
