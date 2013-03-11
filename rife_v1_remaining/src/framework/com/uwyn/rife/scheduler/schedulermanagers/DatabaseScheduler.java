/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseScheduler.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.schedulermanagers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.scheduler.Scheduler;
import com.uwyn.rife.scheduler.SchedulerFactory;
import com.uwyn.rife.scheduler.exceptions.SchedulerException;
import com.uwyn.rife.scheduler.exceptions.SchedulerManagerException;
import com.uwyn.rife.scheduler.schedulermanagers.exceptions.InstallSchedulerErrorException;
import com.uwyn.rife.scheduler.schedulermanagers.exceptions.RemoveSchedulerErrorException;
import com.uwyn.rife.scheduler.taskmanagers.DatabaseTasks;
import com.uwyn.rife.scheduler.taskmanagers.DatabaseTasksFactory;
import com.uwyn.rife.scheduler.taskoptionmanagers.DatabaseTaskoptions;
import com.uwyn.rife.scheduler.taskoptionmanagers.DatabaseTaskoptionsFactory;

public abstract class DatabaseScheduler extends DbQueryManager implements SchedulerFactory
{
	protected DatabaseScheduler(Datasource datasource)
	{
		super(datasource);
	}
	
	public Scheduler getScheduler()
	{
		return new Scheduler(DatabaseTasksFactory.getInstance(getDatasource()), DatabaseTaskoptionsFactory.getInstance(getDatasource()));
	}
	
	public abstract boolean install()
	throws SchedulerManagerException;
	
	public abstract boolean remove()
	throws SchedulerManagerException;

	protected boolean _install()
	throws SchedulerManagerException
	{
		try
		{
			DatabaseTasks		tasks_manager = DatabaseTasksFactory.getInstance(getDatasource());
			DatabaseTaskoptions	taskoptions_manager = DatabaseTaskoptionsFactory.getInstance(getDatasource());
			
			tasks_manager.install();
			taskoptions_manager.install();
		}
		catch (SchedulerException e)
		{
			throw new InstallSchedulerErrorException(e);
		}
		
		return true;
	}

	protected boolean _remove()
	throws SchedulerManagerException
	{
		try
		{
			DatabaseTasks		tasks_manager = DatabaseTasksFactory.getInstance(getDatasource());
			DatabaseTaskoptions	taskoptions_manager = DatabaseTaskoptionsFactory.getInstance(getDatasource());
			
			taskoptions_manager.remove();
			tasks_manager.remove();
		}
		catch (SchedulerException e)
		{
			throw new RemoveSchedulerErrorException(e);
		}
		
		return true;
	}
}
