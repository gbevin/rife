/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteElements.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.elements;

import com.uwyn.rife.RifeTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteElements extends TestSuite
{
	public static Test suite()
	{
		RifeTestSuite suite = new RifeTestSuite("CMF elements test suite");

		suite.addDatasourcedServersideTestSuite(TestElements.class);

		return suite;
	}
}

