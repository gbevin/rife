/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UpdateTaskErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskmanagers.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.scheduler.Task;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;

public class UpdateTaskErrorException extends TaskManagerException
{
	private static final long serialVersionUID = 7613535991016387602L;
	
	private Task	mTask = null;
	
	public UpdateTaskErrorException(Task task)
	{
		this(task, null);
	}
	
	public UpdateTaskErrorException(Task task, DatabaseException cause)
	{
		super("Error while updating task with id '"+task.getId()+"'.", cause);
		
		mTask = task;
	}

	public Task getTask()
	{
		return mTask;
	}
}
