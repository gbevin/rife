/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MemoryScheduler.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.schedulermanagers;

import java.net.URL;

import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.scheduler.Scheduler;
import com.uwyn.rife.scheduler.SchedulerFactory;
import com.uwyn.rife.scheduler.exceptions.SchedulerManagerException;
import com.uwyn.rife.scheduler.schedulermanagers.exceptions.InitializationErrorException;
import com.uwyn.rife.scheduler.schedulermanagers.exceptions.SchedulerNotFoundException;
import com.uwyn.rife.scheduler.taskmanagers.MemoryTasks;
import com.uwyn.rife.scheduler.taskoptionmanagers.MemoryTaskoptions;
import com.uwyn.rife.selector.XmlSelectorResolver;
import com.uwyn.rife.xml.exceptions.XmlErrorException;

public class MemoryScheduler implements SchedulerFactory
{
	private Scheduler				mScheduler = null;
	private String					mXmlPath = null;
	private ResourceFinder			mResourceFinder = null;
	private Xml2MemoryScheduler		mXmlMemoryScheduler = null;

	public MemoryScheduler()
	{
	}

	public MemoryScheduler(String xmlPath, ResourceFinder resourceFinder)
	throws SchedulerManagerException
	{
		if (null == xmlPath)		throw new IllegalArgumentException("xmlPath can't be null.");
		if (0 == xmlPath.length())	throw new IllegalArgumentException("xmlPath can't be empty.");
		if (null == resourceFinder)	throw new IllegalArgumentException("resourceFinder can't be null.");

		mResourceFinder = resourceFinder;
		
		String datasource_resolved = XmlSelectorResolver.resolve(xmlPath, mResourceFinder, "rep/scheduler-");
		if( null == datasource_resolved )
		{
			throw new SchedulerNotFoundException(xmlPath);
		}
		
		URL datasource_resource = mResourceFinder.getResource(datasource_resolved);
		if( null == datasource_resource )
		{
			throw new SchedulerNotFoundException(xmlPath, datasource_resolved);
		}

		mXmlPath = datasource_resolved;

		mXmlMemoryScheduler = new Xml2MemoryScheduler();
		initialize();
		mXmlMemoryScheduler = null;
	}
	
	public Scheduler getScheduler()
	{
		if (null == mScheduler)
		{
			mScheduler = new Scheduler(new MemoryTasks(), new MemoryTaskoptions());
		}
		return mScheduler;
	}

	private void initialize()
	throws SchedulerManagerException
	{
		try
		{
			mXmlMemoryScheduler.processXml(mXmlPath, mResourceFinder);
			mScheduler = mXmlMemoryScheduler.getScheduler();
		}
		catch (XmlErrorException e)
		{
			throw new InitializationErrorException(mXmlPath, e);
		}
	}
	
	/**
	 * Retrieves the path of the XML document that populated this
	 * <code>MemoryScheduler</code> instance
	 *
	 * @return the path of the XML document that populated this
	 * <code>MemoryScheduler</code> instance
	 *
	 * @since 1.0
	 */
	public String getXmlPath()
	{
		return mXmlPath;
	}
}
