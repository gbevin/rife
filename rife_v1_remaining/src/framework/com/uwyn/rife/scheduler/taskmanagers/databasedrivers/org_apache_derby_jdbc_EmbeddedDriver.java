/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_apache_derby_jdbc_EmbeddedDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskmanagers.databasedrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbConnection;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Query;
import com.uwyn.rife.scheduler.Task;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;
import com.uwyn.rife.scheduler.taskmanagers.exceptions.AddTaskErrorException;
import com.uwyn.rife.scheduler.taskmanagers.exceptions.GetTaskIdErrorException;
import com.uwyn.rife.scheduler.taskmanagers.exceptions.InstallTasksErrorException;
import com.uwyn.rife.scheduler.taskmanagers.exceptions.RemoveTasksErrorException;
import java.sql.Statement;

public class org_apache_derby_jdbc_EmbeddedDriver extends generic
{
	public org_apache_derby_jdbc_EmbeddedDriver(Datasource datasource)
	{
		super(datasource);

		mCreateTableTask = new CreateTable(getDatasource())
			.table(RifeConfig.Scheduler.getTableTask())
			.column("id", int.class)
			.column("type", String.class, RifeConfig.Scheduler.getTaskTypeMaximumLength(), CreateTable.NOTNULL)
			.column("planned", long.class, CreateTable.NOTNULL)
			.column("frequency", String.class, RifeConfig.Scheduler.getTaskFrequencyMaximumLength(), CreateTable.NULL)
			.column("busy", boolean.class)
			.customAttribute("id", "GENERATED ALWAYS AS IDENTITY")
			.defaultValue("busy", false)
			.primaryKey(RifeConfig.Scheduler.getTableTask().toUpperCase()+"_PK", "id");

		mAddTask = new Insert(getDatasource())
			.into(mCreateTableTask.getTable())
			.fieldParameter("type")
			.fieldParameter("planned")
			.fieldParameter("frequency")
			.fieldParameter("busy");
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
		
		final int[] result = new int[] {-1};

		try
		{
			if (0 == executeUpdate(mAddTask, new DbPreparedStatementHandler() {
					public DbPreparedStatement getPreparedStatement(Query query, DbConnection connection)
					{
						return connection.getPreparedStatement(query, Statement.RETURN_GENERATED_KEYS);
					}
				
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setBean(task);
					}
				
					public int performUpdate(DbPreparedStatement statement)
					{
						setParameters(statement);
						int query_result = statement.executeUpdate();
						try
						{
							result[0] = statement.getFirstGeneratedIntKey();
						}
						catch (DatabaseException e)
						{
							throw new RuntimeException(new GetTaskIdErrorException(e));
						}
						return query_result;
					}
				}))
			{
				throw new AddTaskErrorException(task);
			}
		}
		catch (DatabaseException e)
		{
			throw new AddTaskErrorException(task, e);
		}

		assert result[0] >= 0;

		return result[0];
	}
}
