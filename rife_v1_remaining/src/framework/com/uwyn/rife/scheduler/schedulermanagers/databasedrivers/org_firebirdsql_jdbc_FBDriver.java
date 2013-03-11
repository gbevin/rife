/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_firebirdsql_jdbc_FBDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.schedulermanagers.databasedrivers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.scheduler.exceptions.SchedulerManagerException;

public class org_firebirdsql_jdbc_FBDriver extends generic
{
	public org_firebirdsql_jdbc_FBDriver(Datasource datasource)
	{
		super(datasource);
	}
	
	public boolean install()
	throws SchedulerManagerException
	{
		int poolsize = getDatasource().getPoolsize();
		
		getDatasource().setPoolsize(0);
		try
		{
			super.install();
		}
		finally
		{
			getDatasource().setPoolsize(poolsize);
		}
	
		return true;
	}
	
	public boolean remove()
	throws SchedulerManagerException
	{
		int poolsize = getDatasource().getPoolsize();
		
		getDatasource().setPoolsize(0);
		try
		{
			super.remove();
		}
		finally
		{
			getDatasource().setPoolsize(poolsize);
		}
		
		return true;
	}
}
