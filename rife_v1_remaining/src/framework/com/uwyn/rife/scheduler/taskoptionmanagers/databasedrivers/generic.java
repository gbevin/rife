/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: generic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers.databasedrivers;

import com.uwyn.rife.database.queries.*;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.scheduler.Taskoption;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;
import com.uwyn.rife.scheduler.taskoptionmanagers.DatabaseTaskoptions;
import com.uwyn.rife.scheduler.taskoptionmanagers.exceptions.DuplicateTaskoptionException;
import com.uwyn.rife.scheduler.taskoptionmanagers.exceptions.InexistentTaskIdException;
import java.util.Collection;

public class generic extends DatabaseTaskoptions
{
	protected CreateTable	mCreateTableTaskoption = null;
	protected DropTable		mDropTableTaskoption = null;
	protected Insert		mAddTaskoption = null;
	protected Select		mGetTaskoption = null;
	protected Select		mGetTaskoptions = null;
	protected Update		mUpdateTaskoption = null;
	protected Delete		mRemoveTaskoption = null;

	public generic(Datasource datasource)
	{
		super(datasource);

		mCreateTableTaskoption = new CreateTable(getDatasource())
			.table(RifeConfig.Scheduler.getTableTaskoption())
			.column("task_id", Integer.class, CreateTable.NOTNULL)
			.column("name", String.class, RifeConfig.Scheduler.getTaskoptionNameMaximumLength(), CreateTable.NOTNULL)
			.column("value", String.class, RifeConfig.Scheduler.getTaskoptionValueMaximumLength(), CreateTable.NOTNULL)
			.primaryKey(RifeConfig.Scheduler.getTableTaskoption().toUpperCase()+"_PK", new String[] {"task_id", "name"})
			.foreignKey(RifeConfig.Scheduler.getTableTaskoption().toUpperCase()+"_TASKID_FK", RifeConfig.Scheduler.getTableTask(), "task_id", "id", null, CreateTable.CASCADE);

		mDropTableTaskoption = new DropTable(getDatasource())
			.table(mCreateTableTaskoption.getTable());

		mAddTaskoption = new Insert(getDatasource())
			.into(mCreateTableTaskoption.getTable())
			.fieldParameter("task_id")
			.fieldParameter("name")
			.fieldParameter("value");
	
		mGetTaskoption = new Select(getDatasource())
			.from(mCreateTableTaskoption.getTable())
			.whereParameter("task_id", "=")
			.whereParameterAnd("name", "=");
	
		mGetTaskoptions = new Select(getDatasource())
			.from(mCreateTableTaskoption.getTable())
			.whereParameter("task_id", "=");
			
		mUpdateTaskoption = new Update(getDatasource())
			.table(mCreateTableTaskoption.getTable())
			.fieldParameter("value")
			.whereParameter("task_id", "=")
			.whereParameterAnd("name", "=");
	
		mRemoveTaskoption = new Delete(getDatasource())
			.from(mCreateTableTaskoption.getTable())
			.whereParameter("task_id", "=")
			.whereParameterAnd("name", "=");
	}

	public boolean install()
	throws TaskoptionManagerException
	{
		return _install(mCreateTableTaskoption);
	}

	public boolean remove()
	throws TaskoptionManagerException
	{
		return _remove(mDropTableTaskoption);
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
							.setString("value", taskoption.getValue());
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
							.setString("value", taskoption.getValue());
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
		return _getTaskoption(mGetTaskoption, new ProcessTaskoption(), taskId, name);
	}
	
	public Collection<Taskoption> getTaskoptions(int taskId)
	throws TaskoptionManagerException
	{
		return _getTaskoptions(mGetTaskoptions, new ProcessTaskoption(), taskId);
	}
	
	public boolean removeTaskoption(Taskoption taskoption)
	throws TaskoptionManagerException
	{
		return _removeTaskoption(mRemoveTaskoption, taskoption);
	}
	
	public boolean removeTaskoption(int taskId, String name)
	throws TaskoptionManagerException
	{
		return _removeTaskoption(mRemoveTaskoption, taskId, name);
	}
}
