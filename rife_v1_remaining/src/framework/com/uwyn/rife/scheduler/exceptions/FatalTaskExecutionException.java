/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FatalTaskExecutionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.exceptions;

import com.uwyn.rife.scheduler.Task;
import java.util.Date;

public class FatalTaskExecutionException extends SchedulerExecutionException
{
	private static final long serialVersionUID = 8346061648025924402L;
	
	private Task mTask = null;

	public FatalTaskExecutionException(Task task, Throwable cause)
	{
		super("The task with id '"+task.getId()+"' and type '"+task.getType()+"' which had to execute at '"+new Date(task.getPlanned())+"' couldn't be executed.", cause);
		mTask = task;
	}

	public Task getTask()
	{
		return mTask;
	}
}
