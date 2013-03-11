/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InitializationErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.schedulermanagers.exceptions;

import com.uwyn.rife.scheduler.exceptions.SchedulerManagerException;

public class InitializationErrorException extends SchedulerManagerException
{
	private static final long serialVersionUID = 8490581853145822038L;
	
	private String	mXmlPath = null;
	
	public InitializationErrorException(String xmlPath, Throwable cause)
	{
		super("Error while initializing the scheduler from xml document '"+xmlPath+"'.", cause);
		
		mXmlPath = xmlPath;
	}
	
	public String getXmlPath()
	{
		return mXmlPath;
	}
}
