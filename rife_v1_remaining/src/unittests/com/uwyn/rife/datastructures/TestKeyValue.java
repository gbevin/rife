/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.datastructures;

import junit.framework.TestCase;

public class TestKeyValue extends TestCase
{
    public TestKeyValue(String name)
    {
        super(name);
    }

    public void testInstantiation()
    {
        KeyValue keyvalue = new KeyValue("key", "value");
        assertNotNull(keyvalue);
        assertEquals(keyvalue.getKey(), "key");
        assertEquals(keyvalue.getValue(), "value");
    }

    public void testAccessors()
    {
        KeyValue keyvalue = new KeyValue("key", "value");
        assertEquals(keyvalue.getKey(), "key");
        assertEquals(keyvalue.getValue(), "value");
        keyvalue.setKey("key2");
        keyvalue.setValue("value2");
        assertEquals(keyvalue.getKey(), "key2");
        assertEquals(keyvalue.getValue(), "value2");
    }

    public void testEquals()
    {
        KeyValue keyvalue1 = new KeyValue("key1", "value1");
        KeyValue keyvalue2 = new KeyValue("key1", "value1");
        KeyValue keyvalue3 = new KeyValue("key2", "value2");
        KeyValue keyvalue4 = new KeyValue("key1", null);
        KeyValue keyvalue5 = new KeyValue("key2", null);
        KeyValue keyvalue6 = new KeyValue("key2", null);
        KeyValue keyvalue7 = new KeyValue(null, "value1");
        KeyValue keyvalue8 = new KeyValue(null, "value2");
        KeyValue keyvalue9 = new KeyValue(null, "value2");
        KeyValue keyvalue10 = new KeyValue(null, null);
        KeyValue keyvalue11 = new KeyValue(null, null);

        assertFalse(keyvalue1.equals("key1"));
        assertFalse(keyvalue1.equals("value1"));

        assertFalse(keyvalue1.equals(null));

        assertTrue(keyvalue1.equals(keyvalue1));
        assertTrue(keyvalue1.equals(keyvalue2));
        assertFalse(keyvalue1.equals(keyvalue3));
        assertFalse(keyvalue1.equals(keyvalue4));
        assertFalse(keyvalue1.equals(keyvalue5));
        assertFalse(keyvalue1.equals(keyvalue6));
        assertFalse(keyvalue1.equals(keyvalue7));
        assertFalse(keyvalue1.equals(keyvalue8));
        assertFalse(keyvalue1.equals(keyvalue9));
        assertFalse(keyvalue1.equals(keyvalue10));
        assertFalse(keyvalue1.equals(keyvalue11));

        assertTrue(keyvalue2.equals(keyvalue2));
        assertFalse(keyvalue2.equals(keyvalue3));
        assertFalse(keyvalue2.equals(keyvalue4));
        assertFalse(keyvalue2.equals(keyvalue5));
        assertFalse(keyvalue2.equals(keyvalue6));
        assertFalse(keyvalue2.equals(keyvalue7));
        assertFalse(keyvalue2.equals(keyvalue8));
        assertFalse(keyvalue2.equals(keyvalue9));
        assertFalse(keyvalue2.equals(keyvalue10));
        assertFalse(keyvalue2.equals(keyvalue11));

        assertTrue(keyvalue3.equals(keyvalue3));
        assertFalse(keyvalue3.equals(keyvalue4));
        assertFalse(keyvalue3.equals(keyvalue5));
        assertFalse(keyvalue3.equals(keyvalue6));
        assertFalse(keyvalue3.equals(keyvalue7));
        assertFalse(keyvalue3.equals(keyvalue8));
        assertFalse(keyvalue3.equals(keyvalue9));
        assertFalse(keyvalue3.equals(keyvalue10));
        assertFalse(keyvalue3.equals(keyvalue11));

        assertTrue(keyvalue4.equals(keyvalue4));
        assertFalse(keyvalue4.equals(keyvalue5));
        assertFalse(keyvalue4.equals(keyvalue6));
        assertFalse(keyvalue4.equals(keyvalue7));
        assertFalse(keyvalue4.equals(keyvalue8));
        assertFalse(keyvalue4.equals(keyvalue9));
        assertFalse(keyvalue4.equals(keyvalue10));
        assertFalse(keyvalue4.equals(keyvalue11));

        assertTrue(keyvalue5.equals(keyvalue5));
        assertTrue(keyvalue5.equals(keyvalue6));
        assertFalse(keyvalue5.equals(keyvalue7));
        assertFalse(keyvalue5.equals(keyvalue8));
        assertFalse(keyvalue5.equals(keyvalue9));
        assertFalse(keyvalue5.equals(keyvalue10));
        assertFalse(keyvalue5.equals(keyvalue11));

        assertTrue(keyvalue6.equals(keyvalue6));
        assertFalse(keyvalue6.equals(keyvalue7));
        assertFalse(keyvalue6.equals(keyvalue8));
        assertFalse(keyvalue6.equals(keyvalue9));
        assertFalse(keyvalue6.equals(keyvalue10));
        assertFalse(keyvalue6.equals(keyvalue11));

        assertTrue(keyvalue7.equals(keyvalue7));
        assertFalse(keyvalue7.equals(keyvalue8));
        assertFalse(keyvalue7.equals(keyvalue9));
        assertFalse(keyvalue7.equals(keyvalue10));
        assertFalse(keyvalue7.equals(keyvalue11));

        assertTrue(keyvalue8.equals(keyvalue8));
        assertTrue(keyvalue8.equals(keyvalue9));
        assertFalse(keyvalue8.equals(keyvalue10));
        assertFalse(keyvalue8.equals(keyvalue11));

        assertTrue(keyvalue9.equals(keyvalue9));
        assertFalse(keyvalue9.equals(keyvalue10));
        assertFalse(keyvalue9.equals(keyvalue11));

        assertTrue(keyvalue10.equals(keyvalue10));
        assertTrue(keyvalue10.equals(keyvalue11));

        assertTrue(keyvalue11.equals(keyvalue11));
    }

    public void testClone()
    {
        KeyValue keyvalue1 = new KeyValue("key1", "value1");
        KeyValue keyvalue2 = new KeyValue("key1", null);
        KeyValue keyvalue3 = new KeyValue(null, "value1");
        KeyValue keyvalue4 = new KeyValue(null, null);

        KeyValue keyvalue1_clone = keyvalue1.clone();
        KeyValue keyvalue2_clone = keyvalue2.clone();
        KeyValue keyvalue3_clone = keyvalue3.clone();
        KeyValue keyvalue4_clone = keyvalue4.clone();

        assertTrue(keyvalue1.equals(keyvalue1_clone));
        assertTrue(keyvalue2.equals(keyvalue2_clone));
        assertTrue(keyvalue3.equals(keyvalue3_clone));
        assertTrue(keyvalue4.equals(keyvalue4_clone));
    }

    public void testHashCode()
    {
        KeyValue keyvalue1 = new KeyValue("key", "value");
        KeyValue keyvalue2 = new KeyValue("key", "value");
        KeyValue keyvalue3 = new KeyValue("key2", "value2");
        assertEquals(keyvalue1.hashCode(), keyvalue1.hashCode());
        assertEquals(keyvalue2.hashCode(), keyvalue2.hashCode());
        assertTrue(keyvalue1.hashCode() == keyvalue2.hashCode() || keyvalue1.equals(keyvalue2));
        assertTrue(keyvalue1.hashCode() != keyvalue3.hashCode() || !keyvalue1.equals(keyvalue3));
        assertTrue(keyvalue2.hashCode() != keyvalue3.hashCode() || !keyvalue2.equals(keyvalue3));
    }

    public void testToString()
    {
        KeyValue keyvalue = new KeyValue("key", "value");
        assertEquals("value", keyvalue.toString());
    }
}
