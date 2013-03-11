/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteLoader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.loader;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteLoader extends TestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("CMF loader test suite");

		suite.addTest(com.uwyn.rife.cmf.loader.image.TestSuiteImage.suite());
		suite.addTest(com.uwyn.rife.cmf.loader.xhtml.TestSuiteXhtml.suite());

		suite.addTestSuite(com.uwyn.rife.cmf.loader.TestImageContentLoader.class);
		suite.addTestSuite(com.uwyn.rife.cmf.loader.TestXhtmlContentLoader.class);

		return suite;
	}
}
