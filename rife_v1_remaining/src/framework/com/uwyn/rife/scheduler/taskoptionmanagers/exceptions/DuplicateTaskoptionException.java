/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DuplicateTaskoptionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers.exceptions;

import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;

public class DuplicateTaskoptionException extends TaskoptionManagerException
{
	private static final long serialVersionUID = 1952213015727655475L;
	
	private int mTaskID = 0;
	private String mTaskoptionName = null;

	public DuplicateTaskoptionException(int taskid, String taskoptionName)
	{
		super("The task option with task id '"+taskid+"' and name '"+taskoptionName+"' already exists.");

		mTaskID = taskid;
		mTaskoptionName = taskoptionName;
	}

	public int getTaskID()
	{
		return mTaskID;
	}

	public String getTaskoptionName()
	{
		return mTaskoptionName;
	}
}
