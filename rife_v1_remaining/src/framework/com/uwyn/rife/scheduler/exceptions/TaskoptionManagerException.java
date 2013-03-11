/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TaskoptionManagerException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.exceptions;

public class TaskoptionManagerException extends SchedulerException
{
	private static final long serialVersionUID = 4109184135680666647L;

	public TaskoptionManagerException(String message)
	{
		super(message);
	}
	public TaskoptionManagerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TaskoptionManagerException(Throwable cause)
	{
		super(cause);
	}
}
