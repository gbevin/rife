/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteImage.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.loader.image;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteImage extends TestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("CMF image loader test suite");

		suite.addTestSuite(com.uwyn.rife.cmf.loader.image.TestImageIOLoader.class);
		suite.addTestSuite(com.uwyn.rife.cmf.loader.image.TestJMagickLoader.class);
		suite.addTestSuite(com.uwyn.rife.cmf.loader.image.TestImageJLoader.class);
		suite.addTestSuite(com.uwyn.rife.cmf.loader.image.TestImageroReaderLoader.class);
		suite.addTestSuite(com.uwyn.rife.cmf.loader.image.TestJaiLoader.class);
		suite.addTestSuite(com.uwyn.rife.cmf.loader.image.TestJimiLoader.class);

		return suite;
	}
}
