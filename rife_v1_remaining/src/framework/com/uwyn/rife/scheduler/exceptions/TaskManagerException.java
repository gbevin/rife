/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TaskManagerException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.exceptions;

public class TaskManagerException extends SchedulerException
{
	private static final long serialVersionUID = -1250407186493859593L;

	public TaskManagerException(String message)
	{
		super(message);
	}
	public TaskManagerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TaskManagerException(Throwable cause)
	{
		super(cause);
	}
}
