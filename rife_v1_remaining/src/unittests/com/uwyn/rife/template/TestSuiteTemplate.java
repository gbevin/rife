/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteTemplate.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteTemplate extends TestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Template engine test suite");

		suite.addTestSuite(com.uwyn.rife.template.TestTemplate.class);
		suite.addTestSuite(com.uwyn.rife.template.TestParsed.class);
		suite.addTestSuite(com.uwyn.rife.template.TestParser.class);
		suite.addTestSuite(com.uwyn.rife.template.TestTemplateFactory.class);

		return suite;
	}
}