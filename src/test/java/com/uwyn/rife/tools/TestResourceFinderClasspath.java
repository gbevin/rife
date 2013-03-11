/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import junit.framework.TestCase;

public class TestResourceFinderClasspath extends TestCase
{
    public TestResourceFinderClasspath(String name)
    {
        super(name);
    }

    public void testInstantiation()
    {
        ResourceFinderClasspath rf = ResourceFinderClasspath.getInstance();
        assertNotNull(rf);
    }

    public void testSingleton()
    {
        ResourceFinderClasspath rf1 = ResourceFinderClasspath.getInstance();
        assertNotNull(rf1);
        ResourceFinderClasspath rf2 = ResourceFinderClasspath.getInstance();
        assertNotNull(rf2);
        assertSame(rf1, rf2);
    }

    public void testModificationTime()
    {
        ResourceFinderClasspath rf = ResourceFinderClasspath.getInstance();
        try
        {
            assertTrue(rf.getModificationTime("java/lang/Class.class") > 0);
        }
        catch (ResourceFinderErrorException e)
        {
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
        }
    }
}
