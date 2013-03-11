/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UpdateTaskoptionErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers.exceptions;

import com.uwyn.rife.scheduler.Taskoption;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;

public class UpdateTaskoptionErrorException extends TaskoptionManagerException
{
	private static final long serialVersionUID = 4032049075661263762L;
	
	private Taskoption	mTaskoption = null;
	
	public UpdateTaskoptionErrorException(Taskoption taskoption)
	{
		this(taskoption, null);
	}
	
	public UpdateTaskoptionErrorException(Taskoption taskoption, Throwable cause)
	{
		super("Error while updating taskoption with task id '"+taskoption.getTaskId()+"', name '"+taskoption.getName()+"' and value '"+taskoption.getValue()+"'.", cause);
		
		mTaskoption = taskoption;
	}
	
	public Taskoption getTaskoption()
	{
		return mTaskoption;
	}
}
