/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteConfig.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.config;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteConfig extends TestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Config API test suite");

		suite.addTestSuite(com.uwyn.rife.config.TestConfig.class);
		suite.addTestSuite(com.uwyn.rife.config.TestRifeConfig.class);
		suite.addTestSuite(com.uwyn.rife.config.TestXml2Config.class);

		return suite;
	}
}

