/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InexistentTaskIdException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers.exceptions;

import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;

public class InexistentTaskIdException extends TaskoptionManagerException
{
	private static final long serialVersionUID = -8781897352662853904L;
	
	private int mTaskID = 0;

	public InexistentTaskIdException(int taskid)
	{
		super("The task id '"+taskid+"' doesn't exist.");
		mTaskID = taskid;
	}

	public int getTaskID()
	{
		return mTaskID;
	}
}
