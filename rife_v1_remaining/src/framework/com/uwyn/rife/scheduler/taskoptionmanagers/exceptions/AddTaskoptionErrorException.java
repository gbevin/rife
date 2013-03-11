/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AddTaskoptionErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers.exceptions;

import com.uwyn.rife.scheduler.Taskoption;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;

public class AddTaskoptionErrorException extends TaskoptionManagerException
{
	private static final long serialVersionUID = 4905743175691118664L;
	
	private Taskoption	mTaskoption = null;
	
	public AddTaskoptionErrorException(Taskoption taskoption)
	{
		this(taskoption, null);
	}
	
	public AddTaskoptionErrorException(Taskoption taskoption, Throwable cause)
	{
		super("Error while adding taskoption with task id '"+taskoption.getTaskId()+"', name '"+taskoption.getName()+"' and value '"+taskoption.getValue()+"'.", cause);
		
		mTaskoption = taskoption;
	}
	
	public Taskoption getTaskoption()
	{
		return mTaskoption;
	}
}
