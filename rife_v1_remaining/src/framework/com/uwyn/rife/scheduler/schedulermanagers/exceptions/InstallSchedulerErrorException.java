/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InstallSchedulerErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.schedulermanagers.exceptions;

import com.uwyn.rife.scheduler.exceptions.SchedulerManagerException;

public class InstallSchedulerErrorException extends SchedulerManagerException
{
	private static final long serialVersionUID = -3910470092652351542L;

	public InstallSchedulerErrorException()
	{
		this(null);
	}

	public InstallSchedulerErrorException(Throwable cause)
	{
		super("Can't install the scheduler database structure.", cause);
	}
}
