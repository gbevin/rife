/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SchedulerException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.exceptions;

public class SchedulerException extends Exception
{
	private static final long serialVersionUID = -400070728897368687L;

	public SchedulerException(String message)
	{
		super(message);
	}

	public SchedulerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SchedulerException(Throwable cause)
	{
		super(cause);
	}
}
