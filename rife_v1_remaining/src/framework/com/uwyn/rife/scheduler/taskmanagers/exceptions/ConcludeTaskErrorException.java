/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ConcludeTaskErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskmanagers.exceptions;

import com.uwyn.rife.scheduler.exceptions.FrequencyException;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;

public class ConcludeTaskErrorException extends TaskManagerException
{
	private static final long serialVersionUID = -7412671423693693166L;
	
	private int		mId = -1;
	
	public ConcludeTaskErrorException(int id)
	{
		this(id, null);
	}
	
	public ConcludeTaskErrorException(int id, FrequencyException cause)
	{
		super("Error while trying to conclude the task with id '"+id+"'.", cause);
		
		mId = id;
	}
	
	public int getTaskId()
	{
		return mId;
	}
}
