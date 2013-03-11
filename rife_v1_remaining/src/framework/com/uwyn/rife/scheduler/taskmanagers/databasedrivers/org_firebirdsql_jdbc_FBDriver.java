/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_firebirdsql_jdbc_FBDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskmanagers.databasedrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Update;
import com.uwyn.rife.scheduler.Task;
import com.uwyn.rife.scheduler.exceptions.FrequencyException;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class org_firebirdsql_jdbc_FBDriver extends generic
{
	public org_firebirdsql_jdbc_FBDriver(Datasource datasource)
	{
		super(datasource);

		mCreateTableTask = new CreateTable(getDatasource())
			.table(RifeConfig.Scheduler.getTableTask())
			.column("id", int.class, CreateTable.NOTNULL)
			.column("tasktype", String.class, RifeConfig.Scheduler.getTaskTypeMaximumLength(), CreateTable.NOTNULL)
			.column("planned", long.class, CreateTable.NOTNULL)
			.column("frequency", String.class, RifeConfig.Scheduler.getTaskFrequencyMaximumLength(), CreateTable.NULL)
			.column("busy", boolean.class)
			.defaultValue("busy", false)
			.primaryKey(RifeConfig.Scheduler.getTableTask().toUpperCase()+"_PK", "id");

		mAddTask = new Insert(getDatasource())
			.into(mCreateTableTask.getTable())
			.fieldParameter("id")
			.fieldParameter("tasktype")
			.fieldParameter("planned")
			.fieldParameter("frequency")
			.fieldParameter("busy");
	
		mUpdateTask = new Update(getDatasource())
			.table(mCreateTableTask.getTable())
			.fieldParameter("tasktype")
			.fieldParameter("planned")
			.fieldParameter("frequency")
			.fieldParameter("busy")
			.whereParameter("id", "=");
	}

	public int addTask(final Task task)
	throws TaskManagerException
	{
		return _addTask(mGetTaskId, mAddTask, new DbPreparedStatementHandler() {
			public void setParameters(DbPreparedStatement statement)
			{
				statement
					.setBean(task)
					.setString("tasktype", task.getType());
			}
		}, task);
	}

	public boolean updateTask(final Task task)
	throws TaskManagerException
	{
		return _updateTask(mUpdateTask, new DbPreparedStatementHandler() {
			public void setParameters(DbPreparedStatement statement)
			{
				statement
					.setBean(task)
					.setString("tasktype", task.getType());
			}
		}, task);
	}

	public Task getTask(int id)
	throws TaskManagerException
	{
		return _getTask(mGetTask, new FirebirdProcessTask(), id);
	}

	public Collection<Task> getTasksToProcess()
	throws TaskManagerException
	{
		return _getTasksToProcess(mGetTasksToProcess, new FirebirdProcessTask());
	}

	public Collection<Task> getScheduledTasks()
	throws TaskManagerException
	{
		return _getScheduledTasks(mGetScheduledTasks, new FirebirdProcessTask());
	}

	protected class FirebirdProcessTask extends ProcessTask
	{
		public boolean processRow(ResultSet resultSet)
		throws SQLException
		{
			assert resultSet != null;
			
			mTask = new Task();

			mTask.setId(resultSet.getInt("id"));
			mTask.setType(resultSet.getString("tasktype"));
			mTask.setPlanned(resultSet.getLong("planned"));
			try
			{
				mTask.setFrequency(resultSet.getString("frequency"));
			}
			catch (FrequencyException e)
			{
				throw new SQLException(e.getMessage());
			}
			mTask.setBusy(resultSet.getBoolean("busy"));
			mTask.setTaskManager(org_firebirdsql_jdbc_FBDriver.this);
			
			if (mCollection != null)
			{
				mCollection.add(mTask);
			}

			return true;
		}
	}
}
