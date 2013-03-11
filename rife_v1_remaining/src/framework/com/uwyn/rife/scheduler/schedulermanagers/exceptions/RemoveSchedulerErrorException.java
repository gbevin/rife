/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RemoveSchedulerErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.schedulermanagers.exceptions;

import com.uwyn.rife.scheduler.exceptions.SchedulerManagerException;

public class RemoveSchedulerErrorException extends SchedulerManagerException
{
	private static final long serialVersionUID = -3277083283936069418L;

	public RemoveSchedulerErrorException()
	{
		this(null);
	}

	public RemoveSchedulerErrorException(Throwable cause)
	{
		super("Can't remove the scheduler database structure.", cause);
	}
}
