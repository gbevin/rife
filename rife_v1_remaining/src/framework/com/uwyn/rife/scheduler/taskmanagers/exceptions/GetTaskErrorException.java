/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GetTaskErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskmanagers.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;

public class GetTaskErrorException extends TaskManagerException
{
	private static final long serialVersionUID = 2834724601081802023L;
	
	private int		mId = -1;
	
	public GetTaskErrorException(int id)
	{
		this(id, null);
	}
	
	public GetTaskErrorException(int id, DatabaseException cause)
	{
		super("Error while trying to obtain the task with id '"+id+"'.", cause);
		
		mId = id;
	}
	
	public int getTaskId()
	{
		return mId;
	}
}
