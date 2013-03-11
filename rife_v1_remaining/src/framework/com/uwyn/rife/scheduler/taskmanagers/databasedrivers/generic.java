/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: generic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskmanagers.databasedrivers;

import com.uwyn.rife.database.queries.*;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.scheduler.Task;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;
import com.uwyn.rife.scheduler.taskmanagers.DatabaseTasks;
import java.util.Collection;

public class generic extends DatabaseTasks
{
	protected CreateSequence	mCreateSequenceTask = null;
	protected CreateTable		mCreateTableTask = null;
	protected DropSequence		mDropSequenceTask = null;
	protected DropTable			mDropTableTask = null;
	protected SequenceValue		mGetTaskId = null;
	protected Insert			mAddTask = null;
	protected Select			mGetTask = null;
	protected Select			mGetTasksToProcess = null;
	protected Select			mGetScheduledTasks = null;
	protected Update			mUpdateTask = null;
	protected Delete			mRemoveTask = null;
	protected Update			mActivateTask = null;
	protected Update			mDesactivateTask = null;

	public generic(Datasource datasource)
	{
		super(datasource);

		mCreateSequenceTask = new CreateSequence(getDatasource())
			.name(RifeConfig.Scheduler.getSequenceTask());
		
		mCreateTableTask = new CreateTable(getDatasource())
			.table(RifeConfig.Scheduler.getTableTask())
			.column("id", int.class, CreateTable.NOTNULL)
			.column("type", String.class, RifeConfig.Scheduler.getTaskTypeMaximumLength(), CreateTable.NOTNULL)
			.column("planned", long.class, CreateTable.NOTNULL)
			.column("frequency", String.class, RifeConfig.Scheduler.getTaskFrequencyMaximumLength(), CreateTable.NULL)
			.column("busy", boolean.class)
			.defaultValue("busy", false)
			.primaryKey(RifeConfig.Scheduler.getTableTask().toUpperCase()+"_PK", "id");

		mDropSequenceTask = new DropSequence(getDatasource())
			.name(mCreateSequenceTask.getName());

		mDropTableTask = new DropTable(getDatasource())
			.table(mCreateTableTask.getTable());

		mGetTaskId = new SequenceValue(getDatasource())
			.name(mCreateSequenceTask.getName())
			.next();

		mAddTask = new Insert(getDatasource())
			.into(mCreateTableTask.getTable())
			.fieldParameter("id")
			.fieldParameter("type")
			.fieldParameter("planned")
			.fieldParameter("frequency")
			.fieldParameter("busy");
	
		mGetTask = new Select(getDatasource())
			.from(mCreateTableTask.getTable())
			.whereParameter("id", "=");
	
		mGetTasksToProcess = new Select(getDatasource())
			.from(mCreateTableTask.getTable())
			.whereParameter("planned", "<")
			.whereAnd("busy", "=", false);
	
		mGetScheduledTasks = new Select(getDatasource())
			.from(mCreateTableTask.getTable())
			.whereParameter("planned", ">=")
			.whereAnd("busy", "=", false);
	
		mUpdateTask = new Update(getDatasource())
			.table(mCreateTableTask.getTable())
			.fieldParameter("type")
			.fieldParameter("planned")
			.fieldParameter("frequency")
			.fieldParameter("busy")
			.whereParameter("id", "=");
	
		mRemoveTask = new Delete(getDatasource())
			.from(mCreateTableTask.getTable())
			.whereParameter("id", "=");
	
		mActivateTask = new Update(getDatasource())
			.table(mCreateTableTask.getTable())
			.field("busy", true)
			.whereParameter("id", "=");
	
		mDesactivateTask = new Update(getDatasource())
			.table(mCreateTableTask.getTable())
			.field("busy", false)
			.whereParameter("id", "=");
	}

	public boolean install()
	throws TaskManagerException
	{
		return _install(mCreateSequenceTask, mCreateTableTask);
	}

	public boolean remove()
	throws TaskManagerException
	{
		return _remove(mDropSequenceTask, mDropTableTask);
	}

	public int addTask(final Task task)
	throws TaskManagerException
	{
		return _addTask(mGetTaskId, mAddTask, new DbPreparedStatementHandler() {
			public void setParameters(DbPreparedStatement statement)
			{
				statement
					.setBean(task);
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
					.setBean(task);
			}
		}, task);
	}

	public Task getTask(int id)
	throws TaskManagerException
	{
		return _getTask(mGetTask, new ProcessTask(), id);
	}

	public Collection<Task> getTasksToProcess()
	throws TaskManagerException
	{
		return _getTasksToProcess(mGetTasksToProcess, new ProcessTask());
	}

	public Collection<Task> getScheduledTasks()
	throws TaskManagerException
	{
		return _getScheduledTasks(mGetScheduledTasks, new ProcessTask());
	}

	public boolean removeTask(int id)
	throws TaskManagerException
	{
		return _removeTask(mRemoveTask, id);
	}

	public boolean rescheduleTask(Task task, long interval, String frequency)
	throws TaskManagerException
	{
		return _rescheduleTask(task, interval, frequency);
	}

	public boolean concludeTask(Task task)
	throws TaskManagerException
	{
		return _concludeTask(task);
	}

	public boolean activateTask(int id)
	throws TaskManagerException
	{
		return _activateTask(mActivateTask, id);
	}

	public boolean deactivateTask(int id)
	throws TaskManagerException
	{
		return _desactivateTask(mDesactivateTask, id);
	}
}
