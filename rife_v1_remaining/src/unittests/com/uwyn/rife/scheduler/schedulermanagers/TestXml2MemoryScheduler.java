/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestXml2MemoryScheduler.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.schedulermanagers;

import com.uwyn.rife.scheduler.*;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.scheduler.exceptions.SchedulerManagerException;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;
import com.uwyn.rife.scheduler.schedulermanagers.exceptions.SchedulerNotFoundException;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.Localization;
import java.util.Calendar;
import java.util.Collection;
import junit.framework.TestCase;

public class TestXml2MemoryScheduler extends TestCase
{
	public TestXml2MemoryScheduler(String name)
	{
		super(name);
	}
	
	public void testInstantiation()
	{
		Xml2MemoryScheduler xml2scheduler = new Xml2MemoryScheduler();
		
		assertNotNull(xml2scheduler);
	}
	
	public void testParse()
	{
		Scheduler scheduler = null;
		
		try
		{
			scheduler = new MemoryScheduler("xml/test_xml2scheduler.xml", ResourceFinderClasspath.getInstance()).getScheduler();
	
			TaskManager			task_manager = scheduler.getTaskManager();
			TaskoptionManager	taskoption_manager = scheduler.getTaskoptionManager();
			Collection<Task>	tasks = task_manager.getTasksToProcess();
			TestExecutor		executor1 = new TestExecutor();
			TestExecutor2		executor2 = new TestExecutor2();

			assertEquals(tasks.size(), 4);
			
			for (Task task : tasks)
			{
				Calendar cal = Calendar.getInstance(RifeConfig.Tools.getDefaultTimeZone(), Localization.getLocale());
				
				switch (task.getId())
				{
					case 0:
						assertNotNull(task);
						assertEquals(executor1.getHandledTasktype(), task.getType());
						assertTrue(task.getPlanned() <= Calendar.getInstance().getTimeInMillis());
						assertNull(task.getFrequency());
						break;
					case 1:
						assertNotNull(task);
						assertEquals(executor2.getHandledTasktype(), task.getType());
						assertTrue(task.getPlanned() <= Calendar.getInstance().getTimeInMillis());
						assertNull(task.getFrequency());
						break;
					case 2:
						assertNotNull(task);
						assertEquals(executor1.getHandledTasktype(), task.getType());
						cal.set(2002, Calendar.SEPTEMBER, 10, 17, 10, 0);
						cal.set(Calendar.MILLISECOND, 0);
						assertEquals(task.getPlanned(), cal.getTimeInMillis());
						assertNull(task.getFrequency());
						break;
					case 4:
						assertNotNull(task);
						assertEquals(executor2.getHandledTasktype(), task.getType());
						cal.set(2002, Calendar.JULY, 7, 10, 12, 0);
						cal.set(Calendar.MILLISECOND, 0);
						assertEquals(task.getPlanned(), cal.getTimeInMillis());
						assertEquals("*/10 9 15 * *", task.getFrequency());
						break;
					default:
						fail();
						break;
				}
			}
			
			tasks = task_manager.getScheduledTasks();
			
			assertEquals(tasks.size(), 1);
			
			for (Task task : tasks)
			{
				switch (task.getId())
				{
					case 3:
						assertNotNull(task);
						assertEquals(executor1.getHandledTasktype(), task.getType());
						assertTrue(task.getPlanned() > Calendar.getInstance().getTimeInMillis());
						assertEquals("0 9 * * 1", task.getFrequency());
						break;
					default:
						fail();
						break;
				}
			}
			
			Collection<Taskoption>	taskoptions = taskoption_manager.getTaskoptions(2);
			
			assertEquals(taskoptions.size(), 2);
			
			for (Taskoption taskoption : taskoptions)
			{
				assertNotNull(taskoption);
				
				if (taskoption.getName().equals("option1"))
				{
					assertEquals("value1", taskoption.getValue());
				}
				else if (taskoption.getName().equals("option2"))
				{
					assertEquals("value2", taskoption.getValue());
				}
				else
				{
					fail();
				}
			}
			
			Collection<Executor>	executors = scheduler.getExecutors();
			
			assertEquals(executors.size(), 2);
			
			for (Executor executor : executors)
			{
				assertNotNull(executor);
				
				if (executor.getHandledTasktype().equals("test_executor"))
				{
					assertTrue(executor instanceof com.uwyn.rife.scheduler.TestExecutor);
				}
				else if (executor.getHandledTasktype().equals("test_executor2"))
				{
					assertTrue(executor instanceof com.uwyn.rife.scheduler.TestExecutor2);
				}
				else
				{
					fail();
				}
			}
		}
		catch (TaskManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (TaskoptionManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (SchedulerManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testSelectedShortClassname()
	{
		MemoryScheduler scheduler = null;
		
		try
		{
			scheduler = new MemoryScheduler("TestSelectorScheduler", ResourceFinderClasspath.getInstance());
		}
		catch (SchedulerManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertTrue(null != scheduler.getScheduler());
	}
	
	public void testSelectedFullClassname()
	{
		MemoryScheduler scheduler = null;
		
		try
		{
			scheduler = new MemoryScheduler("com.uwyn.rife.selector.TestSelectorScheduler", ResourceFinderClasspath.getInstance());
		}
		catch (SchedulerManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertTrue(null != scheduler.getScheduler());
	}
	
	public void testUnavailableXmlFile()
	{
		Scheduler scheduler = null;
		
		try
		{
			scheduler = new MemoryScheduler("xml/this_file_is_not_there.xml", ResourceFinderClasspath.getInstance()).getScheduler();
			fail();
			assertNotNull(scheduler);
		}
		catch (SchedulerManagerException e)
		{
			assertTrue(e instanceof SchedulerNotFoundException);
		}
	}
}
