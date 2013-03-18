/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.resources;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteResources extends TestSuite
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite("Resources test suite");

        suite.addTestSuite(com.uwyn.rife.resources.TestResourceFinderClasspath.class);
//        TODO
//        suite.addTestSuite(com.uwyn.rife.resources.TestResourceFinderDirectories.class);
//        suite.addDatasourcedTestSuite(com.uwyn.rife.resources.TestDatabaseResources.class);

        return suite;
    }
}

