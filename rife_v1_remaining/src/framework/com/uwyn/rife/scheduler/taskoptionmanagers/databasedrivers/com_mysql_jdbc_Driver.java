/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: com_mysql_jdbc_Driver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers.databasedrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.scheduler.Taskoption;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;
import com.uwyn.rife.scheduler.taskoptionmanagers.exceptions.AddTaskoptionErrorException;
import com.uwyn.rife.scheduler.taskoptionmanagers.exceptions.DuplicateTaskoptionException;
import com.uwyn.rife.scheduler.taskoptionmanagers.exceptions.InexistentTaskIdException;
import com.uwyn.rife.scheduler.taskoptionmanagers.exceptions.UpdateTaskoptionErrorException;

public class com_mysql_jdbc_Driver extends generic
{
	protected Select	mTaskIdExists = null;

	public com_mysql_jdbc_Driver(Datasource datasource)
	{
		super(datasource);

		mCreateTableTaskoption = new CreateTable(getDatasource())
			.table(RifeConfig.Scheduler.getTableTaskoption())
			.column("task_id", Integer.class, CreateTable.NOTNULL)
			.column("name", String.class, RifeConfig.Scheduler.getTaskoptionNameMaximumLength(), CreateTable.NOTNULL)
			.column("value", String.class, RifeConfig.Scheduler.getTaskoptionValueMaximumLength(), CreateTable.NOTNULL)
			.primaryKey(RifeConfig.Scheduler.getTableTaskoption().toUpperCase()+"_PK", new String[] {"task_id", "name"});

		mTaskIdExists = new Select(getDatasource())
			.from(RifeConfig.Scheduler.getTableTask())
			.whereParameter("id", "=");
	}
	
	public boolean addTaskoption(final Taskoption taskoption)
	throws TaskoptionManagerException
	{
		if (null == taskoption)	throw new IllegalArgumentException("taskoption can't be null.");
		
		// simulate TaskID foreign key
		try
		{
			if (!executeHasResultRows(mTaskIdExists,  new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("id", taskoption.getTaskId());
					}
				}))
			{
				throw new InexistentTaskIdException(taskoption.getTaskId());
			}
		}
		catch (DatabaseException e)
		{
			throw new AddTaskoptionErrorException(taskoption, e);
		}

		try
		{
			return _addTaskoption(mAddTaskoption, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("task_id", taskoption.getTaskId())
							.setString("name", taskoption.getName())
							.setString("value", taskoption.getValue());
					}
				}, taskoption);
		}
		catch (TaskoptionManagerException e)
		{
			if (null != e.getCause() &&
				null != e.getCause().getCause())
			{
				if (-1 != e.getCause().getCause().getMessage().toLowerCase().indexOf("duplicate"))
				{
					throw new DuplicateTaskoptionException(taskoption.getTaskId(), taskoption.getName());
				}
			}
			
			throw e;
		}
	}

	public boolean updateTaskoption(final Taskoption taskoption)
	throws TaskoptionManagerException
	{
		if (null == taskoption)	throw new IllegalArgumentException("taskoption can't be null.");
		
		// simulate TaskID foreign key
		try
		{
			if (!executeHasResultRows(mTaskIdExists,  new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("id", taskoption.getTaskId());
					}
				}))
			{
				throw new InexistentTaskIdException(taskoption.getTaskId());
			}
		}
		catch (DatabaseException e)
		{
			throw new UpdateTaskoptionErrorException(taskoption, e);
		}

		try
		{
			return _updateTaskoption(mUpdateTaskoption, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("task_id", taskoption.getTaskId())
							.setString("name", taskoption.getName())
							.setString("value", taskoption.getValue());
					}
				}, taskoption);
		}
		catch (TaskoptionManagerException e)
		{
			if (null != e.getCause() &&
				null != e.getCause().getCause())
			{
				if (-1 != e.getCause().getCause().getMessage().toLowerCase().indexOf("duplicate"))
				{
					throw new DuplicateTaskoptionException(taskoption.getTaskId(), taskoption.getName());
				}
			}
			
			throw e;
		}
	}
}
