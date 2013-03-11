/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteValidation.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.validation;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteValidation extends TestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("CMF validation test suite");

		suite.addTestSuite(com.uwyn.rife.cmf.validation.TestSupportedImage.class);
		suite.addTestSuite(com.uwyn.rife.cmf.validation.TestSupportedXhtml.class);

		return suite;
	}
}
