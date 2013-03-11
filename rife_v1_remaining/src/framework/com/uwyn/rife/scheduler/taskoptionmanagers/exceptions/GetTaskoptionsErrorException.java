/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GetTaskoptionsErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;

public class GetTaskoptionsErrorException extends TaskoptionManagerException
{
	private static final long serialVersionUID = -4348602870066135917L;
	
	private int		mTaskId = -1;
	
	public GetTaskoptionsErrorException(int taskId)
	{
		this(taskId, null);
	}
	
	public GetTaskoptionsErrorException(int taskId, DatabaseException cause)
	{
		super("Error while getting the taskoptions for task id '"+taskId+"'.", cause);
		
		mTaskId = taskId;
	}
	
	public int getTaskId()
	{
		return mTaskId;
	}
}
