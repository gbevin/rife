/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SchedulerExecutionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.exceptions;

public class SchedulerExecutionException extends RuntimeException
{
	private static final long serialVersionUID = 779737627952928528L;

	public SchedulerExecutionException(String message)
	{
		super(message);
	}

	public SchedulerExecutionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SchedulerExecutionException(Throwable cause)
	{
		super(cause);
	}
}
