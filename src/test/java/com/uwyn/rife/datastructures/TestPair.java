/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.datastructures;

import junit.framework.TestCase;

public class TestPair extends TestCase
{
    public TestPair(String name)
    {
        super(name);
    }

    public void testInstantiation()
    {
        Pair<String, Long> pair = new Pair<>("first", (long)1434);
        assertNotNull(pair);
        assertEquals(pair.getFirst(), "first");
        assertEquals(pair.getSecond(), new Long(1434));
    }

    public void testAccessors()
    {
        Pair<String, Long> pair = new Pair<>("first", (long)1434);
        assertEquals(pair.getFirst(), "first");
        assertEquals(pair.getSecond(), new Long(1434));
        pair.setFirst("first2");
        pair.setSecond((long)3433);
        assertEquals(pair.getFirst(), "first2");
        assertEquals(pair.getSecond(), new Long(3433));
    }

    public void testEquals()
    {
        Pair<String, Long> pair1 = new Pair<>("first1", (long)1434);
        Pair<String, Long> pair2 = new Pair<>("first1", (long)1434);
        Pair<String, Long> pair3 = new Pair<>("first2", (long)83488);
        Pair<String, Long> pair4 = new Pair<>("first1", null);
        Pair<String, Long> pair5 = new Pair<>("first2", null);
        Pair<String, Long> pair6 = new Pair<>("first2", null);
        Pair<String, Long> pair7 = new Pair<>(null, (long)1434);
        Pair<String, Long> pair8 = new Pair<>(null, (long)83488);
        Pair<String, Long> pair9 = new Pair<>(null, (long)83488);
        Pair<String, Long> pair10 = new Pair<>(null, null);
        Pair<String, Long> pair11 = new Pair<>(null, null);

        Pair<String, Integer> pair12 = new Pair<>("first1", 1434);
        Pair<Long, Integer> pair13 = new Pair<>((long)1434, 1434);
        Pair<Long, String> pair14 = new Pair<>((long)1434, "second1");

        assertFalse(pair1.equals("first1"));
        assertFalse(pair1.equals(new Long(1434)));

        assertFalse(pair1.equals(null));

        assertTrue(pair1.equals(pair1));
        assertTrue(pair1.equals(pair2));
        assertFalse(pair1.equals(pair3));
        assertFalse(pair1.equals(pair4));
        assertFalse(pair1.equals(pair5));
        assertFalse(pair1.equals(pair6));
        assertFalse(pair1.equals(pair7));
        assertFalse(pair1.equals(pair8));
        assertFalse(pair1.equals(pair9));
        assertFalse(pair1.equals(pair10));
        assertFalse(pair1.equals(pair11));
        assertFalse(pair1.equals(pair12));
        assertFalse(pair1.equals(pair13));
        assertFalse(pair1.equals(pair14));

        assertTrue(pair2.equals(pair2));
        assertFalse(pair2.equals(pair3));
        assertFalse(pair2.equals(pair4));
        assertFalse(pair2.equals(pair5));
        assertFalse(pair2.equals(pair6));
        assertFalse(pair2.equals(pair7));
        assertFalse(pair2.equals(pair8));
        assertFalse(pair2.equals(pair9));
        assertFalse(pair2.equals(pair10));
        assertFalse(pair2.equals(pair11));
        assertFalse(pair2.equals(pair12));
        assertFalse(pair2.equals(pair13));
        assertFalse(pair2.equals(pair14));

        assertTrue(pair3.equals(pair3));
        assertFalse(pair3.equals(pair4));
        assertFalse(pair3.equals(pair5));
        assertFalse(pair3.equals(pair6));
        assertFalse(pair3.equals(pair7));
        assertFalse(pair3.equals(pair8));
        assertFalse(pair3.equals(pair9));
        assertFalse(pair3.equals(pair10));
        assertFalse(pair3.equals(pair11));
        assertFalse(pair3.equals(pair12));
        assertFalse(pair3.equals(pair13));
        assertFalse(pair3.equals(pair14));

        assertTrue(pair4.equals(pair4));
        assertFalse(pair4.equals(pair5));
        assertFalse(pair4.equals(pair6));
        assertFalse(pair4.equals(pair7));
        assertFalse(pair4.equals(pair8));
        assertFalse(pair4.equals(pair9));
        assertFalse(pair4.equals(pair10));
        assertFalse(pair4.equals(pair11));
        assertFalse(pair4.equals(pair12));
        assertFalse(pair4.equals(pair13));
        assertFalse(pair4.equals(pair14));

        assertTrue(pair5.equals(pair5));
        assertTrue(pair5.equals(pair6));
        assertFalse(pair5.equals(pair7));
        assertFalse(pair5.equals(pair8));
        assertFalse(pair5.equals(pair9));
        assertFalse(pair5.equals(pair10));
        assertFalse(pair5.equals(pair11));
        assertFalse(pair5.equals(pair12));
        assertFalse(pair5.equals(pair13));
        assertFalse(pair5.equals(pair14));

        assertTrue(pair6.equals(pair6));
        assertFalse(pair6.equals(pair7));
        assertFalse(pair6.equals(pair8));
        assertFalse(pair6.equals(pair9));
        assertFalse(pair6.equals(pair10));
        assertFalse(pair6.equals(pair11));
        assertFalse(pair6.equals(pair12));
        assertFalse(pair6.equals(pair13));
        assertFalse(pair6.equals(pair14));

        assertTrue(pair7.equals(pair7));
        assertFalse(pair7.equals(pair8));
        assertFalse(pair7.equals(pair9));
        assertFalse(pair7.equals(pair10));
        assertFalse(pair7.equals(pair11));
        assertFalse(pair7.equals(pair12));
        assertFalse(pair7.equals(pair13));
        assertFalse(pair7.equals(pair14));

        assertTrue(pair8.equals(pair8));
        assertTrue(pair8.equals(pair9));
        assertFalse(pair8.equals(pair10));
        assertFalse(pair8.equals(pair11));
        assertFalse(pair8.equals(pair12));
        assertFalse(pair8.equals(pair13));
        assertFalse(pair8.equals(pair14));

        assertTrue(pair9.equals(pair9));
        assertFalse(pair9.equals(pair10));
        assertFalse(pair9.equals(pair11));
        assertFalse(pair9.equals(pair12));
        assertFalse(pair9.equals(pair13));
        assertFalse(pair9.equals(pair14));

        assertTrue(pair10.equals(pair10));
        assertTrue(pair10.equals(pair11));
        assertFalse(pair10.equals(pair12));
        assertFalse(pair10.equals(pair13));
        assertFalse(pair10.equals(pair14));

        assertTrue(pair11.equals(pair11));
        assertFalse(pair11.equals(pair12));
        assertFalse(pair11.equals(pair13));
        assertFalse(pair11.equals(pair14));

        assertTrue(pair12.equals(pair12));
        assertFalse(pair12.equals(pair13));
        assertFalse(pair12.equals(pair14));

        assertTrue(pair13.equals(pair13));
        assertFalse(pair13.equals(pair14));

        assertTrue(pair14.equals(pair14));
    }

    public void testHashCode()
    {
        Pair<String, Long> pair1 = new Pair<>("first", (long)53433);
        Pair<String, Long> pair2 = new Pair<>("first", (long)53433);
        Pair<String, Long> pair3 = new Pair<>("first2", (long)83488);
        assertEquals(pair1.hashCode(), pair1.hashCode());
        assertEquals(pair2.hashCode(), pair2.hashCode());
        assertTrue(pair1.hashCode() == pair2.hashCode() || pair1.equals(pair2));
        assertTrue(pair1.hashCode() != pair3.hashCode() || !pair1.equals(pair3));
        assertTrue(pair2.hashCode() != pair3.hashCode() || !pair2.equals(pair3));
    }
}
