/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import junit.framework.TestCase;

public class TestIntegerUtils extends TestCase
{
    public TestIntegerUtils(String name)
    {
        super(name);
    }

    public void testIntToBytes()
    {
        assertEquals(265325803, IntegerUtils.bytesToInt(IntegerUtils.intToBytes(265325803)));
    }

}
