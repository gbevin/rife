/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InstallTaskoptionsErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;

public class InstallTaskoptionsErrorException extends TaskoptionManagerException
{
	private static final long serialVersionUID = 3383700445767477585L;

	public InstallTaskoptionsErrorException()
	{
		this(null);
	}

	public InstallTaskoptionsErrorException(DatabaseException cause)
	{
		super("Can't install the taskoption database structure.", cause);
	}
}
