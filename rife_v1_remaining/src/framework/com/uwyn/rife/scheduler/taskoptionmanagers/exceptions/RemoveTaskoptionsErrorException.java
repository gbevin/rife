/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RemoveTaskoptionsErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;

public class RemoveTaskoptionsErrorException extends TaskoptionManagerException
{
	private static final long serialVersionUID = 2096333126507604963L;

	public RemoveTaskoptionsErrorException()
	{
		this(null);
	}

	public RemoveTaskoptionsErrorException(DatabaseException cause)
	{
		super("Can't remove the taskoption database structure.", cause);
	}
}
