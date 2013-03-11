/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDbQueryManagerFactoryImpl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbQueryManagerCache;
import com.uwyn.rife.database.DbQueryManagerFactory;

public abstract class TestDbQueryManagerFactoryImpl extends DbQueryManagerFactory
{
	public static final String	MANAGER_PACKAGE_NAME = TestDbQueryManagerFactoryImpl.class.getPackage().getName()+".testdatabasedrivers.";
	
	private static DbQueryManagerCache	mCache = new DbQueryManagerCache();
	
	public static TestDbQueryManagerImpl getInstance(Datasource datasource)
	{
		return (TestDbQueryManagerImpl)getInstance(MANAGER_PACKAGE_NAME, mCache, datasource);
	}
	
	public static TestDbQueryManagerImpl getInstance(Datasource datasource, String identifier)
	{
		return (TestDbQueryManagerImpl)getInstance(MANAGER_PACKAGE_NAME, mCache, datasource,identifier);
	}
}
