/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NoExecutorForTasktypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.exceptions;

public class NoExecutorForTasktypeException extends SchedulerExecutionException
{
	private static final long serialVersionUID = -9088897866704438084L;
	
	private String mTasktype = null;

	public NoExecutorForTasktypeException(String tasktype)
	{
		super("The scheduler didn't have an executor registered for the execution of a task with type '"+tasktype+"'.");

		mTasktype = tasktype;
	}

	public String getTasktype()
	{
		return mTasktype;
	}
}
