/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteFormat.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.format;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteFormat extends TestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("CMF format test suite");

		suite.addTestSuite(com.uwyn.rife.cmf.format.TestImageFormatter.class);
		suite.addTestSuite(com.uwyn.rife.cmf.format.TestXhtmlFormatter.class);
		suite.addTestSuite(com.uwyn.rife.cmf.format.TestRawFormatter.class);

		return suite;
	}
}
