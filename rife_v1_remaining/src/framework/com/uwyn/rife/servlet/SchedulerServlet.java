/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SchedulerServlet.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.servlet;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.scheduler.Scheduler;
import com.uwyn.rife.scheduler.exceptions.FatalTaskExecutionException;
import com.uwyn.rife.scheduler.exceptions.NoExecutorForTasktypeException;
import com.uwyn.rife.scheduler.exceptions.UnableToRetrieveTasksToProcessException;
import com.uwyn.rife.scheduler.taskmanagers.DatabaseTasksFactory;
import com.uwyn.rife.scheduler.taskoptionmanagers.DatabaseTaskoptionsFactory;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class SchedulerServlet extends HttpServlet
{
	private static final long serialVersionUID = 6728613469081145182L;

	public void init()
	throws ServletException
	{
		String		sleep_time = getInitParameter("sleeptime");
		String		datasource_name = getInitParameter("datasource");
		Datasource	datasource = Datasources.getRepInstance().getDatasource(datasource_name);
		Scheduler	scheduler = new Scheduler(DatabaseTasksFactory.getInstance(datasource), DatabaseTaskoptionsFactory.getInstance(datasource));

		if (null != sleep_time)
		{
			try
			{
				scheduler.setSleepTime(Integer.parseInt(sleep_time));
			}
			catch (NumberFormatException e)
			{
				// just use the default value for the sleep time
			}
		}

		try
		{
			scheduler.start();
		}
		catch (NoExecutorForTasktypeException e)
		{
			Logger.getLogger("com.uwyn.rife.servlet").severe(e.getMessage());
		}
		catch (UnableToRetrieveTasksToProcessException e)
		{
			Logger.getLogger("com.uwyn.rife.servlet").severe(e.getMessage());
		}
		catch (FatalTaskExecutionException e)
		{
			Logger.getLogger("com.uwyn.rife.servlet").severe(e.getMessage());
		}
	}
}
