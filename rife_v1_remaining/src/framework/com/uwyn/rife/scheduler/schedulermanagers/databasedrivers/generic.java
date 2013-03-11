/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: generic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.schedulermanagers.databasedrivers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.scheduler.exceptions.SchedulerManagerException;
import com.uwyn.rife.scheduler.schedulermanagers.DatabaseScheduler;

public class generic extends DatabaseScheduler
{
	public generic(Datasource datasource)
	{
		super(datasource);
	}

	public boolean install()
	throws SchedulerManagerException
	{
		return _install();
	}

	public boolean remove()
	throws SchedulerManagerException
	{
		return _remove();
	}
}
