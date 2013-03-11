/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExecutorAlreadyRegisteredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.exceptions;

import com.uwyn.rife.scheduler.Executor;

public class ExecutorAlreadyRegisteredException extends SchedulerException
{
	private static final long serialVersionUID = 7581141854929771532L;
	
	private Executor mExecutor = null;

	public ExecutorAlreadyRegisteredException(Executor executor)
	{
		super("The executor has already been registered to a scheduler.");

		mExecutor = executor;
	}

	public Executor getExecutor()
	{
		return mExecutor;
	}
}
