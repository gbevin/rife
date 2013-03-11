/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteContentStores.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores;

import com.uwyn.rife.RifeTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteContentStores extends TestSuite
{
	public static Test suite()
	{
		RifeTestSuite suite = new RifeTestSuite("CMF content stores test suite");

		suite.addDatasourcedTestSuite(com.uwyn.rife.cmf.dam.contentstores.TestDatabaseImageStore.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.cmf.dam.contentstores.TestDatabaseTextStore.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.cmf.dam.contentstores.TestDatabaseRawStore.class);

		return suite;
	}
}

