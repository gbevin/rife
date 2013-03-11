/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestsuiteDatabaseContentStores.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores;

import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.config.Config;
import com.uwyn.rife.database.Datasources;

public abstract class TestsuiteDatabaseContentStores extends TestCaseServerside
{
	private String		mDatasourceName = null;

	public TestsuiteDatabaseContentStores(String datasourceName, int siteType, String name)
	{
		super(siteType, name);

		mDatasourceName = datasourceName;
	}

	public void setUp()
	throws Exception
	{
		super.setUp();

		Config.getRepInstance().setParameter("unittestsdatasource", mDatasourceName);
	}

	public void tearDown()
	throws Exception
	{
		// reset all the connections since some databases don't support meta data
		// updates when other connections are active
		Datasources.getRepInstance().getDatasource(Config.getRepInstance().getString("unittestsdatasource")).cleanup();

		Config.getRepInstance().removeParameter("unittestsdatasource");

		super.tearDown();
	}
}

