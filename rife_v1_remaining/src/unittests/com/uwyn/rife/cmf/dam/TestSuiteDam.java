/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteDam.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam;

import com.uwyn.rife.RifeTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteDam extends TestSuite
{
	public static Test suite()
	{
		RifeTestSuite suite = new RifeTestSuite("DAM test suite");

		suite.addTestSuite(com.uwyn.rife.cmf.dam.TestDatabaseContentInfo.class);

		suite.addTest(com.uwyn.rife.cmf.dam.contentstores.TestSuiteContentStores.suite());

		suite.addTestSuite(com.uwyn.rife.cmf.dam.TestContentDataUser.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.cmf.dam.TestContentManager.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.cmf.dam.TestOrdinalManager.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.cmf.dam.TestContentQueryManager.class);

		return suite;
	}
}
