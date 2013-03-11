/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RemoveTasksErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskmanagers.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;

public class RemoveTasksErrorException extends TaskManagerException
{
	private static final long serialVersionUID = 8498777641623518926L;

	public RemoveTasksErrorException()
	{
		this(null);
	}

	public RemoveTasksErrorException(DatabaseException cause)
	{
		super("Can't remove the task database structure.", cause);
	}
}
