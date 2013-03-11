/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: com_mysql_jdbc_Driver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskmanagers.databasedrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbConnection;
import com.uwyn.rife.database.DbConnectionUser;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.scheduler.Task;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;
import com.uwyn.rife.scheduler.taskmanagers.exceptions.AddTaskErrorException;
import com.uwyn.rife.scheduler.taskmanagers.exceptions.GetTaskIdErrorException;
import com.uwyn.rife.scheduler.taskmanagers.exceptions.InstallTasksErrorException;
import com.uwyn.rife.scheduler.taskmanagers.exceptions.RemoveTasksErrorException;

public class com_mysql_jdbc_Driver extends generic
{
	protected	Select	mGetInsertedTaskId = null;
	
	public com_mysql_jdbc_Driver(Datasource datasource)
	{
		super(datasource);

		mCreateTableTask = new CreateTable(getDatasource())
			.table(RifeConfig.Scheduler.getTableTask())
			.column("id", int.class)
			.column("type", String.class, RifeConfig.Scheduler.getTaskTypeMaximumLength(), CreateTable.NOTNULL)
			.column("planned", long.class, CreateTable.NOTNULL)
			.column("frequency", String.class, RifeConfig.Scheduler.getTaskFrequencyMaximumLength(), CreateTable.NULL)
			.column("busy", boolean.class)
			.customAttribute("id", "AUTO_INCREMENT")
			.defaultValue("busy", false)
			.primaryKey(RifeConfig.Scheduler.getTableTask().toUpperCase()+"_PK", "id");

		mGetInsertedTaskId = new Select(getDatasource())
			.field("LAST_INSERT_ID()");
	}


	public boolean install()
	throws TaskManagerException
	{
		try
		{
			executeUpdate(mCreateTableTask);
		}
		catch (DatabaseException e)
		{
			throw new InstallTasksErrorException(e);
		}
		
		return true;
	}

	public boolean remove()
	throws TaskManagerException
	{
		try
		{
			executeUpdate(mDropTableTask);
		}
		catch (DatabaseException e)
		{
			throw new RemoveTasksErrorException(e);
		}
		
		return true;
	}

	public int addTask(final Task task)
	throws TaskManagerException
	{
		if (null == task)	throw new IllegalArgumentException("task can't be null.");
		
		int result = 0;

		try
		{
			result = ((Integer)reserveConnection(new DbConnectionUser() {
					public Integer useConnection(DbConnection connection)
					{
						try
						{
							if (0 == executeUpdate(mAddTask, new DbPreparedStatementHandler() {
									public void setParameters(DbPreparedStatement statement)
									{
										statement
											.setBean(task)
											.setNull("id", java.sql.Types.INTEGER);
									}
								}))
							{
								throw new RuntimeException(new AddTaskErrorException(task));
							}
						}
						catch (DatabaseException e)
						{
							throw new RuntimeException(new AddTaskErrorException(task, e));
						}
						
						try
						{
							return new Integer(executeGetFirstInt(mGetInsertedTaskId));
						}
						catch (DatabaseException e)
						{
							throw new RuntimeException(new GetTaskIdErrorException(e));
						}
					}
				})).intValue();
		}
		catch (RuntimeException e)
		{
			if (e.getCause() instanceof TaskManagerException)
			{
				throw (TaskManagerException)e.getCause();
			}
		}

		assert result >= 0;

		return result;
	}
}
