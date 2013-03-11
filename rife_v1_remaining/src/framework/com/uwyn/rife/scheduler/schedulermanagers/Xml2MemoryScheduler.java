/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Xml2MemoryScheduler.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.schedulermanagers;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.scheduler.Executor;
import com.uwyn.rife.scheduler.Scheduler;
import com.uwyn.rife.scheduler.Task;
import com.uwyn.rife.scheduler.Taskoption;
import com.uwyn.rife.scheduler.exceptions.FrequencyException;
import com.uwyn.rife.scheduler.exceptions.SchedulerException;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;
import com.uwyn.rife.scheduler.taskmanagers.MemoryTasks;
import com.uwyn.rife.scheduler.taskoptionmanagers.MemoryTaskoptions;
import com.uwyn.rife.xml.Xml2Data;
import com.uwyn.rife.xml.exceptions.XmlErrorException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.xml.sax.Attributes;

public class Xml2MemoryScheduler extends Xml2Data
{
	private StringBuilder		mCharacterData = null;
	private Scheduler			mScheduler = null;
	private MemoryTasks			mTasks = null;
	private MemoryTaskoptions	mTaskoptions = null;
	private int					mLastTaskId = -1;
	private Taskoption			mLastTaskoption = null;
	
	public Scheduler getScheduler()
	{
		return mScheduler;
	}
	
	public void startDocument()
	{
		mCharacterData = new StringBuilder();
		mTasks = new MemoryTasks();
		mTaskoptions = new MemoryTaskoptions();
		mScheduler = new Scheduler(mTasks, mTaskoptions);
		mLastTaskId = -1;
		mLastTaskoption = null;
	}
	
	public void endDocument()
	{
		mCharacterData = null;
		mTasks = null;
		mTaskoptions = null;
		mLastTaskId = -1;
		mLastTaskoption = null;
	}
	
	private String registerExecutor(String classname)
	throws XmlErrorException
	{
		try
		{
			Class<Executor>	executor_class = (Class<Executor>)Class.forName(classname);
			Executor		executor = executor_class.newInstance();
			if (!mScheduler.addExecutor(executor))
			{
				throw new XmlErrorException("Couldn't add the executor with class '"+classname+"' to the scheduler.");
			}
			
			return executor.getHandledTasktype();
		}
		catch (ClassNotFoundException e)
		{
			throw new XmlErrorException("Error while retrieving the executor's class '"+classname+"'.", e);
		}
		catch (InstantiationException e)
		{
			throw new XmlErrorException("Error while instantiating the executor with class '"+classname+"'.", e);
		}
		catch (IllegalAccessException e)
		{
			throw new XmlErrorException("Error while instantiating the executor with class '"+classname+"'.", e);
		}
		catch (SchedulerException e)
		{
			throw new XmlErrorException("Error while adding the executor with class '"+classname+"' to the scheduler.", e);
		}
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
	{
		if (qName.equals("scheduler"))
		{
			// do nothing
		}
		else if (qName.equals("task"))
		{
			String classname = atts.getValue("classname");
			String planned = atts.getValue("planned");
			String frequency = atts.getValue("frequency");
			String type = atts.getValue("type");
			
			if ((null == classname || 0 == classname.length()))
			{
				if (null == type || 0 == type.length())
				{
					throw new XmlErrorException("Either the executor's classname or the task type have to be specified.");
				}
			}
			else
			{
				if (type != null && type.length() > 0)
				{
					throw new XmlErrorException("A task type can't be specified if the executor's class has been specified.");
				}
			}
			
			// if an executor's classname has been specified, try to instantiate the executor and
			// register it with the scheduler
			if (classname != null &&
				classname.length() > 0)
			{
				type = registerExecutor(classname);
			}
			
			// if no planned date has been specified, set it to the current time
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			dateformat.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
			Date planned_date = null;
			if (null == planned)
			{
				// no explicit planned date was provided
				// set it to the current time
				planned_date = new Date();
			}
			else
			{
				try
				{
					planned_date = dateformat.parse(planned);
				}
				catch (ParseException e)
				{
					throw new XmlErrorException("Invalid planned '"+planned+"'.", e);
				}
			}
			
			// create the new task and add it to the scheduler
			Task task = new Task();
			task.setType(type);
			task.setPlanned(planned_date);
			try
			{
				task.setFrequency(frequency);
			}
			catch (FrequencyException e)
			{
				throw new XmlErrorException("Invalid frequency '"+frequency+"'.", e);
			}
			try
			{
				mLastTaskId = mTasks.addTask(task);
			}
			catch (TaskManagerException e)
			{
				throw new XmlErrorException("Error while adding the task to the scheduler.", e);
			}
			
			// if no explicit planned date has been given, and a frequency has
			// been provided, rearrange the planned date to ensure that the
			// first execution happens on a valid frequency moment
			if (null == planned &&
				frequency != null)
			{
				try
				{
					long one_minute_earlier = task.getPlanned()-60000;
					long first_valid_planned = task.getNextDate(one_minute_earlier);
					task.setPlanned(first_valid_planned);
				}
				catch (FrequencyException e)
				{
					// do nothing, the planned date will remain at the curren time
				}
			}
		}
		else if (qName.equals("option"))
		{
			mCharacterData = new StringBuilder();
			mLastTaskoption = new Taskoption();
			mLastTaskoption.setTaskId(mLastTaskId);
			mLastTaskoption.setName(atts.getValue("name"));
		}
		else if (qName.equals("config"))
		{
			if (mCharacterData != null &&
				Config.hasRepInstance())
			{
				mCharacterData.append(Config.getRepInstance().getString(atts.getValue("param"), ""));
			}
		}
		else if (qName.equals("executor"))
		{
			String classname = atts.getValue("classname");
			registerExecutor(classname);
		}
		else
		{
			throw new XmlErrorException("Unsupport element name '"+qName+"'.");
		}
	}
	
	public void endElement(String namespaceURI, String localName, String qName)
	{
		if (qName.equals("option"))
		{
			mLastTaskoption.setValue(mCharacterData.toString());
			try
			{
				mTaskoptions.addTaskoption(mLastTaskoption);
			}
			catch (TaskoptionManagerException e)
			{
				throw new XmlErrorException("Error while adding the taskoption with name '"+mLastTaskoption.getName()+"', value '"+mLastTaskoption.getValue()+"' and task id '"+mLastTaskoption.getTaskId()+"'.", e);
			}
			mCharacterData = new StringBuilder();
			mLastTaskoption = null;
		}
	}
	
	public void characters(char[] ch, int start, int length)
	{
		if (length > 0)
		{
			mCharacterData.append(String.copyValueOf(ch, start, length));
		}
	}
}

