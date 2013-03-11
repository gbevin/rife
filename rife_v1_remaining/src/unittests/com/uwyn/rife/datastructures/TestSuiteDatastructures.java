/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteDatastructures.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.datastructures;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteDatastructures extends TestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Datastructures API test suite");

		suite.addTestSuite(com.uwyn.rife.datastructures.TestKeyValue.class);
		suite.addTestSuite(com.uwyn.rife.datastructures.TestPair.class);

		return suite;
	}
}

