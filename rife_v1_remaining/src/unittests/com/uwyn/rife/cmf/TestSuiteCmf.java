/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteCmf.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteCmf extends TestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("CMF test suite");

		suite.addTestSuite(com.uwyn.rife.cmf.TestCmfProperty.class);
		suite.addTestSuite(com.uwyn.rife.cmf.TestCmfValidation.class);
		suite.addTestSuite(com.uwyn.rife.cmf.TestContent.class);
		suite.addTestSuite(com.uwyn.rife.cmf.TestContentInfo.class);
		suite.addTestSuite(com.uwyn.rife.cmf.TestContentRepository.class);
		suite.addTestSuite(com.uwyn.rife.cmf.TestMimeType.class);

		suite.addTest(com.uwyn.rife.cmf.dam.TestSuiteDam.suite());
		suite.addTest(com.uwyn.rife.cmf.loader.TestSuiteLoader.suite());
		suite.addTest(com.uwyn.rife.cmf.validation.TestSuiteValidation.suite());
		suite.addTest(com.uwyn.rife.cmf.format.TestSuiteFormat.suite());

		suite.addTest(com.uwyn.rife.cmf.elements.TestSuiteElements.suite());

		return suite;
	}
}
