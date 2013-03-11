/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteResources.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources;

import com.uwyn.rife.RifeTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteResources extends TestSuite
{
	public static Test suite()
	{
		RifeTestSuite suite = new RifeTestSuite("Resources test suite");

		suite.addTestSuite(com.uwyn.rife.resources.TestResourceFinderClasspath.class);
		suite.addTestSuite(com.uwyn.rife.resources.TestResourceFinderDirectories.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.resources.TestDatabaseResources.class);

		return suite;
	}
}

