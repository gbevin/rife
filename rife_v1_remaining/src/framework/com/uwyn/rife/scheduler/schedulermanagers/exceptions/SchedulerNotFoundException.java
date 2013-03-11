/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SchedulerNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.schedulermanagers.exceptions;

import com.uwyn.rife.scheduler.exceptions.SchedulerManagerException;

public class SchedulerNotFoundException extends SchedulerManagerException
{
	private static final long serialVersionUID = 1851064951670900576L;
	
	private String	mScheduler = null;
	private String	mXmlPath = null;

	public SchedulerNotFoundException(String scheduler)
	{
		super("Couldn't find a valid resource for the scheduler '"+scheduler+"'.");
		
		mScheduler = scheduler;
	}
	
	public SchedulerNotFoundException(String scheduler, String xmlPath)
	{
		super("Couldn't find a valid resource for the scheduler '"+scheduler+"', tried xml path '"+xmlPath+"'.");
		
		mScheduler = scheduler;
		mXmlPath = xmlPath;
	}
	
	public String getScheduler()
	{
		return mScheduler;
	}
	
	public String getXmlPath()
	{
		return mXmlPath;
	}
}

