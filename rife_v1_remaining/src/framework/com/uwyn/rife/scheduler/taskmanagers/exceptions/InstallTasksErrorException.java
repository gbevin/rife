/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InstallTasksErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskmanagers.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;

public class InstallTasksErrorException extends TaskManagerException
{
	private static final long serialVersionUID = -8972198627863614260L;

	public InstallTasksErrorException()
	{
		this(null);
	}

	public InstallTasksErrorException(DatabaseException cause)
	{
		super("Can't install the task database structure.", cause);
	}
}
