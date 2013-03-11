/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_firebirdsql_jdbc_FBDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers.databasedrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Update;
import com.uwyn.rife.scheduler.Taskoption;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;
import com.uwyn.rife.scheduler.taskoptionmanagers.exceptions.DuplicateTaskoptionException;
import com.uwyn.rife.scheduler.taskoptionmanagers.exceptions.InexistentTaskIdException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class org_firebirdsql_jdbc_FBDriver extends generic
{
	public org_firebirdsql_jdbc_FBDriver(Datasource datasource)
	{
		super(datasource);

		int name_length = RifeConfig.Scheduler.getTaskoptionNameMaximumLength();
		if (name_length > 64)
		{
			name_length = 64;
		}
		
		mCreateTableTaskoption = new CreateTable(getDatasource())
			.table(RifeConfig.Scheduler.getTableTaskoption())
			.column("task_id", Integer.class, CreateTable.NOTNULL)
			.column("name", String.class, name_length, CreateTable.NOTNULL)
			.column("optionvalue", String.class, RifeConfig.Scheduler.getTaskoptionValueMaximumLength(), CreateTable.NOTNULL)
			.primaryKey(RifeConfig.Scheduler.getTableTaskoption().toUpperCase()+"_PK", new String[] {"task_id", "name"})
			.foreignKey(RifeConfig.Scheduler.getTableTaskoption().toUpperCase()+"_TASKID_FK", RifeConfig.Scheduler.getTableTask(), "task_id", "id", null, CreateTable.CASCADE);

		mAddTaskoption = new Insert(getDatasource())
			.into(mCreateTableTaskoption.getTable())
			.fieldParameter("task_id")
			.fieldParameter("name")
			.fieldParameter("optionvalue");
			
		mUpdateTaskoption = new Update(getDatasource())
			.table(mCreateTableTaskoption.getTable())
			.fieldParameter("optionvalue")
			.whereParameter("task_id", "=")
			.whereParameterAnd("name", "=");
	}

	public boolean addTaskoption(final Taskoption taskoption)
	throws TaskoptionManagerException
	{
		try
		{
			return _addTaskoption(mAddTaskoption, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("task_id", taskoption.getTaskId())
							.setString("name", taskoption.getName())
							.setString("optionvalue", taskoption.getValue());
					}
				}, taskoption);
		}
		catch (TaskoptionManagerException e)
		{
			if (null != e.getCause() &&
				null != e.getCause().getCause())
			{
				String message = e.getCause().getCause().getMessage().toUpperCase();
				if (-1 != message.indexOf(mCreateTableTaskoption.getForeignKeys().get(0).getName()))
				{
					throw new InexistentTaskIdException(taskoption.getTaskId());
				}
				else if (-1 != message.indexOf(mCreateTableTaskoption.getPrimaryKeys().get(0).getName()))
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
		try
		{
			return _updateTaskoption(mUpdateTaskoption, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("task_id", taskoption.getTaskId())
							.setString("name", taskoption.getName())
							.setString("optionvalue", taskoption.getValue());
					}
				}, taskoption);
		}
		catch (TaskoptionManagerException e)
		{
			if (null != e.getCause() &&
				null != e.getCause().getCause())
			{
				String message = e.getCause().getCause().getMessage().toUpperCase();
				if (-1 != message.indexOf(mCreateTableTaskoption.getForeignKeys().get(0).getName()))
				{
					throw new InexistentTaskIdException(taskoption.getTaskId());
				}
				else if (-1 != message.indexOf(mCreateTableTaskoption.getPrimaryKeys().get(0).getName()))
				{
					throw new DuplicateTaskoptionException(taskoption.getTaskId(), taskoption.getName());
				}
			}
			
			throw e;
		}
	}
	
	public Taskoption getTaskoption(int taskId, String name)
	throws TaskoptionManagerException
	{
		return _getTaskoption(mGetTaskoption, new FirebirdProcessTaskoption(), taskId, name);
	}
	
	public Collection<Taskoption> getTaskoptions(int taskId)
	throws TaskoptionManagerException
	{
		return _getTaskoptions(mGetTaskoptions, new FirebirdProcessTaskoption(), taskId);
	}

	protected class FirebirdProcessTaskoption extends ProcessTaskoption
	{
		public boolean processRow(ResultSet resultSet)
		throws SQLException
		{
			assert resultSet != null;
			
			mTaskoption = new Taskoption();

			mTaskoption.setTaskId(resultSet.getInt("task_id"));
			mTaskoption.setName(resultSet.getString("name"));
			mTaskoption.setValue(resultSet.getString("optionvalue"));
			
			if (mCollection != null)
			{
				mCollection.add(mTaskoption);
			}

			return true;
		}
	}
}
