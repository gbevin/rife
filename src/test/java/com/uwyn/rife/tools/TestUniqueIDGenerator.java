/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import junit.framework.TestCase;

import java.util.Arrays;

public class TestUniqueIDGenerator extends TestCase
{
    public TestUniqueIDGenerator(String name)
    {
        super(name);
    }

    public void testGenerationWithImplicitSeed()
    {
        UniqueID uid = UniqueIDGenerator.generate();
        assertNotNull(uid);
        assertNotNull(uid.toString());
        assertTrue(uid.toString().length() > 0);
    }

    public void testGenerationWithProvidedSeed()
    {
        UniqueID uid = UniqueIDGenerator.generate("uwyn.com/127.0.0.1");
        assertNotNull(uid);
        assertNotNull(uid.toString());
        assertTrue(uid.toString().length() > 0);
    }

    public void testIDUnicity()
    {
        UniqueID uid1 = UniqueIDGenerator.generate();
        UniqueID uid2 = UniqueIDGenerator.generate();
        UniqueID uid3 = UniqueIDGenerator.generate();
        UniqueID uid4 = UniqueIDGenerator.generate();
        assertNotNull(uid1);
        assertNotNull(uid2);
        assertNotNull(uid3);
        assertNotNull(uid4);
        assertTrue(Arrays.equals(uid1.getID(), uid1.getID()));
        assertFalse(Arrays.equals(uid1.getID(), uid2.getID()));
        assertFalse(Arrays.equals(uid1.getID(), uid3.getID()));
        assertFalse(Arrays.equals(uid1.getID(), uid4.getID()));
        assertTrue(Arrays.equals(uid2.getID(), uid2.getID()));
        assertFalse(Arrays.equals(uid2.getID(), uid3.getID()));
        assertFalse(Arrays.equals(uid2.getID(), uid4.getID()));
        assertTrue(Arrays.equals(uid3.getID(), uid3.getID()));
        assertFalse(Arrays.equals(uid3.getID(), uid4.getID()));
        assertTrue(Arrays.equals(uid4.getID(), uid4.getID()));
        String uid1_string = uid1.toString();
        String uid2_string = uid2.toString();
        String uid3_string = uid3.toString();
        String uid4_string = uid4.toString();
        assertTrue(0 == uid1_string.compareTo(uid1_string));
        assertTrue(0 != uid1_string.compareTo(uid2_string));
        assertTrue(0 != uid1_string.compareTo(uid3_string));
        assertTrue(0 != uid1_string.compareTo(uid4_string));
        assertTrue(0 == uid2_string.compareTo(uid2_string));
        assertTrue(0 != uid2_string.compareTo(uid3_string));
        assertTrue(0 != uid2_string.compareTo(uid4_string));
        assertTrue(0 == uid3_string.compareTo(uid3_string));
        assertTrue(0 != uid3_string.compareTo(uid4_string));
        assertTrue(0 == uid4_string.compareTo(uid4_string));
    }
}
