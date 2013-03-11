/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnableToRetrieveTasksToProcessException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.exceptions;

public class UnableToRetrieveTasksToProcessException extends SchedulerExecutionException
{
	static final long serialVersionUID = -2233605418085075672L;

	public UnableToRetrieveTasksToProcessException(TaskManagerException e)
	{
		super("Unable to retrieve the tasks to process.", e);
	}
}
