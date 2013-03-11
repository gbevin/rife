/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GetTaskoptionErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;

public class GetTaskoptionErrorException extends TaskoptionManagerException
{
	private static final long serialVersionUID = 3920950726058002527L;
	
	private int		mTaskId = -1;
	private String	mName = null;
	
	public GetTaskoptionErrorException(int taskId, String name)
	{
		this(taskId, name, null);
	}
	
	public GetTaskoptionErrorException(int taskId, String name, DatabaseException cause)
	{
		super("Error while getting taskoption with task id '"+taskId+"' and name '"+name+"'.", cause);
		
		mTaskId = taskId;
		mName = name;
	}
	
	public int getTaskId()
	{
		return mTaskId;
	}
	
	public String getName()
	{
		return mName;
	}
}
