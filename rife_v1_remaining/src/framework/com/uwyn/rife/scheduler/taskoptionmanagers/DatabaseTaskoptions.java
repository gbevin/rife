/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseTaskoptions.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers;

import com.uwyn.rife.database.queries.*;
import com.uwyn.rife.scheduler.taskoptionmanagers.exceptions.*;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.DbRowProcessor;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.scheduler.Scheduler;
import com.uwyn.rife.scheduler.Taskoption;
import com.uwyn.rife.scheduler.TaskoptionManager;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public abstract class DatabaseTaskoptions extends DbQueryManager implements TaskoptionManager
{
	private Scheduler	mScheduler = null;
	
	protected DatabaseTaskoptions(Datasource datasource)
	{
		super(datasource);
	}
	
	public void setScheduler(Scheduler scheduler)
	{
		mScheduler = scheduler;
	}
	
	public Scheduler getScheduler()
	{
		return mScheduler;
	}
	
	public abstract boolean install()
	throws TaskoptionManagerException;
	
	public abstract boolean remove()
	throws TaskoptionManagerException;

	protected boolean _install(CreateTable createTableTaskoption)
	throws TaskoptionManagerException
	{
		assert createTableTaskoption != null;
		
		try
		{
			executeUpdate(createTableTaskoption);
		}
		catch (DatabaseException e)
		{
			throw new InstallTaskoptionsErrorException(e);
		}
		
		return true;
	}

	protected boolean _remove(DropTable dropTableTaskoption)
	throws TaskoptionManagerException
	{
		assert dropTableTaskoption != null;
		
		try
		{
			executeUpdate(dropTableTaskoption);
		}
		catch (DatabaseException e)
		{
			throw new RemoveTaskoptionsErrorException(e);
		}
		
		return true;
	}
	
	protected boolean _addTaskoption(Insert addTaskoption, DbPreparedStatementHandler handler, final Taskoption taskoption)
	throws TaskoptionManagerException
	{
		assert addTaskoption != null;
		
		if (null == taskoption)	throw new IllegalArgumentException("taskoption can't be null.");
		
		boolean result = false;

		try
		{
			if (0 == executeUpdate(addTaskoption, handler))
			{
				throw new AddTaskoptionErrorException(taskoption);
			}
			result = true;
		}
		catch (DatabaseException e)
		{
			throw new AddTaskoptionErrorException(taskoption, e);
		}

		return result;
	}

	protected boolean _updateTaskoption(Update updateTaskoption, DbPreparedStatementHandler handler, final Taskoption taskoption)
	throws TaskoptionManagerException
	{
		assert updateTaskoption != null;
		
		if (null == taskoption)	throw new IllegalArgumentException("taskoption can't be null.");
		
		boolean result = false;
		try
		{
			if (0 == executeUpdate(updateTaskoption, handler))
			{
				throw new UpdateTaskoptionErrorException(taskoption);
			}
			result = true;
		}
		catch (DatabaseException e)
		{
			throw new UpdateTaskoptionErrorException(taskoption, e);
		}

		return result;
	}

	protected Taskoption _getTaskoption(Select getTaskoption, ProcessTaskoption processTaskoption, final int taskId, final String name)
	throws TaskoptionManagerException
	{
		assert getTaskoption != null;
		
		if (taskId < 0)			throw new IllegalArgumentException("taskid can't be negative.");
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");
		
		Taskoption taskoption = null;

		try
		{
			executeFetchFirst(getTaskoption, processTaskoption, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("task_id", taskId)
							.setString("name", name);
					}
				});
			taskoption = processTaskoption.getTaskoption();
		}
		catch (DatabaseException e)
		{
			throw new GetTaskoptionErrorException(taskId, name, e);
		}

		return taskoption;
	}

	protected Collection<Taskoption> _getTaskoptions(Select getTaskoptions, ProcessTaskoption processTaskoption, final int taskId)
	throws TaskoptionManagerException
	{
		assert getTaskoptions != null;
		
		if (taskId < 0)	throw new IllegalArgumentException("taskid can't be negative.");

		ArrayList<Taskoption> taskoptions = new ArrayList<Taskoption>();
		processTaskoption.setCollection(taskoptions);

		try
		{
			executeFetchAll(getTaskoptions, processTaskoption, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("task_id", taskId);
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new GetTaskoptionsErrorException(taskId, e);
		}

		assert taskoptions != null;

		return taskoptions;
	}

	protected boolean _removeTaskoption(Delete removeTaskoption, Taskoption taskoption)
	throws TaskoptionManagerException
	{
		if (null == taskoption)	throw new IllegalArgumentException("taskoption can't be null.");
		
		return _removeTaskoption(removeTaskoption, taskoption.getTaskId(), taskoption.getName());
	}

	protected boolean _removeTaskoption(Delete removeTaskoption, final int taskId, final String name)
	throws TaskoptionManagerException
	{
		assert removeTaskoption != null;
		
		if (taskId < 0)			throw new IllegalArgumentException("taskid can't be negative.");
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");
		
		boolean result = false;
		
		try
		{
			if (0 != executeUpdate(removeTaskoption, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("task_id", taskId)
							.setString("name", name);
					}
				}))
			{
				result = true;
			}
		}
		catch (DatabaseException e)
		{
			throw new RemoveTaskoptionErrorException(taskId, name, e);
		}

		return result;
	}

	protected class ProcessTaskoption extends DbRowProcessor
	{
		protected Collection<Taskoption>	mCollection = null;
		protected Taskoption 				mTaskoption = null;

		public ProcessTaskoption()
		{
		}
		
		public void setCollection(Collection<Taskoption> collection)
		{
			mCollection = collection;
		}
		
		public boolean processRow(ResultSet resultSet)
		throws SQLException
		{
			assert resultSet != null;
			
			mTaskoption = new Taskoption();

			mTaskoption.setTaskId(resultSet.getInt("task_id"));
			mTaskoption.setName(resultSet.getString("name"));
			mTaskoption.setValue(resultSet.getString("value"));
			
			if (mCollection != null)
			{
				mCollection.add(mTaskoption);
			}

			return true;
		}

		public Taskoption getTaskoption()
		{
			return mTaskoption;
		}
	}
}

