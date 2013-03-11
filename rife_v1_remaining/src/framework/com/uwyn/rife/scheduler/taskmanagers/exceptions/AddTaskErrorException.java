/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AddTaskErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskmanagers.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.scheduler.Task;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;

public class AddTaskErrorException extends TaskManagerException
{
	private static final long serialVersionUID = -7352228624354115586L;
	
	private Task	mTask = null;
	
	public AddTaskErrorException(Task task)
	{
		this(task, null);
	}
	
	public AddTaskErrorException(Task task, DatabaseException cause)
	{
		super("Error while adding task with id '"+task.getId()+"', type '"+ task.getType() +"', planned '"+task.getPlanned()+"' and frequency '"+task.getFrequency()+"'.", cause);
		
		mTask = task;
	}
	
	public Task getTask()
	{
		return mTask;
	}
}
