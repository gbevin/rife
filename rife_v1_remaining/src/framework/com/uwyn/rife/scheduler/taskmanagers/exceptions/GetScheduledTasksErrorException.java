/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GetScheduledTasksErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskmanagers.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;

public class GetScheduledTasksErrorException extends TaskManagerException
{
	private static final long serialVersionUID = -4854656738595719247L;

	public GetScheduledTasksErrorException()
	{
		this(null);
	}

	public GetScheduledTasksErrorException(DatabaseException cause)
	{
		super("Unable to get the scheduled tasks.", cause);
	}
}
