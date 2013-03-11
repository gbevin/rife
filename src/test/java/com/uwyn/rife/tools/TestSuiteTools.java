/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteTools extends TestSuite
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite("Tools API test suite");

        suite.addTestSuite(com.uwyn.rife.tools.TestAbstractPropertyChangeSupport.class);
// TODO
//        suite.addTestSuite(com.uwyn.rife.tools.TestArrayUtils.class);
//        suite.addTestSuite(com.uwyn.rife.tools.TestBeanUtils.class);
        suite.addTestSuite(com.uwyn.rife.tools.TestClassUtils.class);
        suite.addTestSuite(com.uwyn.rife.tools.TestConvert.class);
        suite.addTestSuite(com.uwyn.rife.tools.TestIntegerUtils.class);
        suite.addTestSuite(com.uwyn.rife.tools.TestLocalizationUtils.class);
        suite.addTestSuite(com.uwyn.rife.tools.TestObjectUtils.class);
        suite.addTestSuite(com.uwyn.rife.tools.TestPasswordGenerator.class);
        suite.addTestSuite(com.uwyn.rife.tools.TestResourceFinderClasspath.class);
        suite.addTestSuite(com.uwyn.rife.tools.TestStringEncryptor.class);
        suite.addTestSuite(com.uwyn.rife.tools.TestStringUtils.class);
        suite.addTestSuite(com.uwyn.rife.tools.TestUniqueIDGenerator.class);

        return suite;
    }
}

