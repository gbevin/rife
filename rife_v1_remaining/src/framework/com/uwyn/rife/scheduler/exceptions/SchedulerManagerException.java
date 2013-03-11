/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SchedulerManagerException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.exceptions;

public class SchedulerManagerException extends SchedulerException
{
	private static final long serialVersionUID = -4381666648963934294L;

	public SchedulerManagerException(String message)
	{
		super(message);
	}
	public SchedulerManagerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SchedulerManagerException(Throwable cause)
	{
		super(cause);
	}
}
