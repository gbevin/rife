/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteTest.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteTest extends TestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test API test suite");
		
		suite.addTestSuite(com.uwyn.rife.test.TestParsedHtml.class);
		suite.addTestSuite(com.uwyn.rife.test.TestEngineMocks.class);
		
		return suite;
	}
}

