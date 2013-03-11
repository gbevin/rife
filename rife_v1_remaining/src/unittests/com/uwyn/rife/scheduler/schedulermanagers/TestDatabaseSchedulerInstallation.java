/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseSchedulerInstallation.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.schedulermanagers;

import junit.framework.TestCase;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.scheduler.exceptions.SchedulerManagerException;
import com.uwyn.rife.tools.ExceptionUtils;

public class TestDatabaseSchedulerInstallation extends TestCase
{
	private Datasource  mDatasource = null;
    
	public TestDatabaseSchedulerInstallation(Datasource datasource, String datasourceName, String name)
	{
		super(name);
		mDatasource = datasource;
	}

	public void testInstantiateSchedulerManager()
	{
		DatabaseScheduler manager = DatabaseSchedulerFactory.getInstance(mDatasource);
		assertNotNull(manager);
	}
	
	public void testInstall()
	{
		DatabaseScheduler manager = DatabaseSchedulerFactory.getInstance(mDatasource);

		try
		{
			assertTrue(true == manager.install());
		}
		catch (SchedulerManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testRemove()
	{
		DatabaseScheduler manager = DatabaseSchedulerFactory.getInstance(mDatasource);

		try
		{
			assertTrue(true == manager.remove());
		}
		catch (SchedulerManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	 }
}
