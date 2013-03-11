/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestsuiteDatabaseAuthenticated.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;


import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.config.Config;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.ioc.HierarchicalProperties;

public abstract class TestsuiteDatabaseAuthenticated extends TestCaseServerside
{
	private String						mDatasourceName = null;
	protected HierarchicalProperties	mProperties = null;

	public TestsuiteDatabaseAuthenticated(String datasourceName, int siteType, String name)
	{
		super(siteType, name);
		
		mDatasourceName = datasourceName;
		mProperties = new HierarchicalProperties();
	}
	
	public void setUp()
	throws Exception
	{
		super.setUp();
		
		Config.getRepInstance().setParameter("unittestsdatasource", mDatasourceName);
		Config.getRepInstance().setParameter("sessiondurationunittestsdatasource", "sessionduration"+mDatasourceName);
		Config.getRepInstance().setParameter("purgingunittestsdatasource", "purging"+mDatasourceName);
	}
	
	public void tearDown()
	throws Exception
	{
		// reset all the connections since some databases don't support meta data
		// updates when other connections are active
		Datasources.getRepInstance().getDatasource(Config.getRepInstance().getString("purgingunittestsdatasource")).cleanup();
		Datasources.getRepInstance().getDatasource(Config.getRepInstance().getString("sessiondurationunittestsdatasource")).cleanup();
		Datasources.getRepInstance().getDatasource(Config.getRepInstance().getString("unittestsdatasource")).cleanup();
		
		Config.getRepInstance().removeParameter("purgingunittestsdatasource");
		Config.getRepInstance().removeParameter("sessiondurationunittestsdatasource");
		Config.getRepInstance().removeParameter("unittestsdatasource");

		super.tearDown();
	}
}

