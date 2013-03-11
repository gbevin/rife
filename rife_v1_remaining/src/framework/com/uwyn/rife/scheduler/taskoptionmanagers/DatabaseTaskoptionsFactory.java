/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseTaskoptionsFactory.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbQueryManagerCache;
import com.uwyn.rife.database.DbQueryManagerFactory;

public abstract class DatabaseTaskoptionsFactory extends DbQueryManagerFactory
{
	private static final String	MANAGER_PACKAGE_NAME = DatabaseTaskoptionsFactory.class.getPackage().getName()+".databasedrivers.";
	
	private static DbQueryManagerCache	mCache = new DbQueryManagerCache();
	
	public static DatabaseTaskoptions getInstance(Datasource datasource)
	{
		return (DatabaseTaskoptions)getInstance(MANAGER_PACKAGE_NAME, mCache, datasource);
	}
}
