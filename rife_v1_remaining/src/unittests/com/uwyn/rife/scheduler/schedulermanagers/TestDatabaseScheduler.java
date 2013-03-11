/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseScheduler.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.schedulermanagers;

import com.uwyn.rife.scheduler.exceptions.*;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.scheduler.Executor;
import com.uwyn.rife.scheduler.Scheduler;
import com.uwyn.rife.scheduler.Task;
import com.uwyn.rife.scheduler.TaskManager;
import com.uwyn.rife.scheduler.TestTasktypes;
import com.uwyn.rife.scheduler.schedulermanagers.DatabaseScheduler;
import com.uwyn.rife.scheduler.schedulermanagers.DatabaseSchedulerFactory;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.Localization;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import junit.framework.TestCase;

public class TestDatabaseScheduler extends TestCase
{
	private Datasource  mDatasource = null;
    
	public TestDatabaseScheduler(Datasource datasource, String datasourceName, String name)
	{
		super(name);
		mDatasource = datasource;
	}

	protected void setUp()
	throws Exception
	{
		DatabaseScheduler schedulermanager = DatabaseSchedulerFactory.getInstance(mDatasource);
		try
		{
			schedulermanager.install();
		}
		catch (SchedulerManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	protected void tearDown()
	throws Exception
	{
		DatabaseScheduler schedulermanager = DatabaseSchedulerFactory.getInstance(mDatasource);
		try
		{
			schedulermanager.remove();
		}
		catch (SchedulerManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testInstantiateScheduler()
	{
		Scheduler scheduler = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler();
		assertNotNull(scheduler);
	}

	public void testStartStopScheduler()
	{
		Scheduler scheduler = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler();
		try
		{
			scheduler.start();
			synchronized (scheduler)
			{
				scheduler.interrupt();

				try
				{
					scheduler.wait();
				}
				catch (InterruptedException e)
				{
		            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
				}
			}
		}
		catch (NoExecutorForTasktypeException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (UnableToRetrieveTasksToProcessException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testAddExecutor()
	{
		Scheduler	scheduler = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler();
		Executor	executor = new TestExecutor();

		assertNull(scheduler.getExecutor(executor.getHandledTasktype()));
		try
		{
			scheduler.addExecutor(executor);
		}
		catch (SchedulerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(executor, scheduler.getExecutor(executor.getHandledTasktype()));
		assertTrue(scheduler.removeExecutor(executor));
	}

	public void testOneshotTaskExecution()
	{
		int				sleeptime = 60*1000;
		Scheduler		scheduler = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler();
		TestExecutor	executor = new TestExecutor();
		TaskManager		taskmanager = scheduler.getTaskManager();
		Task			task = new Task();
		
		try
		{
			task.setType(TestTasktypes.UPLOAD_GROUPS);
			task.setPlanned(System.currentTimeMillis());
			task.setFrequency(null);
			task.setBusy(false);
		}
		catch (FrequencyException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			scheduler.addExecutor(executor);
		}
		catch (SchedulerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		scheduler.setSleepTime(sleeptime);
		try
		{
			task.setId(taskmanager.addTask(task));
			task = taskmanager.getTask(task.getId());
		}
		catch (TaskManagerException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			scheduler.start();
			try
			{
				Thread.sleep(sleeptime*2);
			}
			catch (InterruptedException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
			synchronized (scheduler)
			{
				scheduler.interrupt();

				try
				{
					scheduler.wait();
				}
				catch (InterruptedException e)
				{
					assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
				}
			}

			Collection<Task> executed_tasks = executor.getExecutedTasks();
			assertEquals(1, executed_tasks.size());
			Task executed_task = executed_tasks.iterator().next();
			assertTrue(task.equals(executed_task));
			assertSame(executed_task.getTaskManager(), taskmanager);
		}
		catch (NoExecutorForTasktypeException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (UnableToRetrieveTasksToProcessException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRepeatingTaskExecution()
	{
		int				scheduler_sleeptime = 30*1000;				// 30 seconds
		int				task_frequency = 60*1000;					// 1 minute
		int				thread_sleeptime = scheduler_sleeptime*6;	// 3 minutes
		Scheduler		scheduler = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler();
		TestExecutor	executor = new TestExecutor();
		TaskManager		taskmanager = scheduler.getTaskManager();
		Task			task = new Task();
		
		try
		{
			task.setType(TestTasktypes.UPLOAD_GROUPS);
			// set back a while in the past to test the catch up rescheduling
			task.setPlanned(System.currentTimeMillis()-(scheduler_sleeptime*10));
			task.setFrequency("* * * * *");
			task.setBusy(false);
		}
		catch (FrequencyException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		scheduler.setSleepTime(scheduler_sleeptime);

		try
		{
			scheduler.addExecutor(executor);
		}
		catch (SchedulerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			task.setId(taskmanager.addTask(task));
			task = taskmanager.getTask(task.getId());
		}
		catch (TaskManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		Collection<Task>	executed_tasks = null;
		int				executed_tasks_size = -1;
		try
		{
			scheduler.start();
			try
			{
				Thread.sleep(thread_sleeptime);
				executed_tasks = executor.getExecutedTasks();
				executed_tasks_size = executed_tasks.size();
			}
			catch (InterruptedException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
			synchronized (scheduler)
			{
				scheduler.interrupt();

				try
				{
					scheduler.wait();
				}
				catch (InterruptedException e)
				{
					assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
				}
			}
			
			// task frequency fits in the thread sleep time
			long	number_of_executions = (thread_sleeptime/task_frequency)+1;

//			System.out.println("\n"+mDatasource.getDriver()+"\n"+executor.getFirstExecution().getTime().getTime()+" : "+executor.getFirstExecution().getTime().toGMTString()+"\n"+now.getTime()+" : "+now.toGMTString()+"\ntask_frequency = "+task_frequency+"\nnumber_of_executions = "+number_of_executions+"\nexecuted_tasks_size = "+executed_tasks_size);
			Date now = new Date();
			assertTrue("\nFAILED "+mDatasource.getDriver()+" \n"+executor.getFirstExecution().getTime().getTime()+" : "+executor.getFirstExecution().getTime().toGMTString()+"\n"+now.getTime()+" : "+now.toGMTString()+"\ntask_frequency = "+task_frequency+"\nnumber_of_executions = "+number_of_executions+"\nexecuted_tasks_size = "+executed_tasks_size, number_of_executions == executed_tasks_size || number_of_executions == executed_tasks_size+1);
			for (Task executed_task : executed_tasks)
			{
				assertEquals(task.getId(), executed_task.getId());
				assertEquals(task.getType(), executed_task.getType());
				assertEquals(task.getFrequency(), executed_task.getFrequency());
				assertTrue(task.getPlanned() <= executed_task.getPlanned());
				assertSame(executed_task.getTaskManager(), taskmanager);
			}

			try
			{
				taskmanager.removeTask(task.getId());
			}
			catch (TaskManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
		catch (NoExecutorForTasktypeException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (UnableToRetrieveTasksToProcessException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	class TestExecutor extends Executor
	{
		private Calendar		mFirstExecution = null;
		private ArrayList<Task>	mExecutedTasks = null;

		public TestExecutor()
		{
			mExecutedTasks = new ArrayList<Task>();
		}

		public boolean executeTask(Task task)
		{
			synchronized (this)
			{
				if (null == mFirstExecution)
				{
					mFirstExecution = Calendar.getInstance(RifeConfig.Tools.getDefaultTimeZone(), Localization.getLocale());
					mFirstExecution.setTimeInMillis(System.currentTimeMillis());
				}
				mExecutedTasks.add(task);
			}

			return true;
		}

		public Collection<Task> getExecutedTasks()
		{
			synchronized (this)
			{
				return mExecutedTasks;
			}
		}
		
		public Calendar getFirstExecution()
		{
			synchronized (this)
			{
				return mFirstExecution;
			}
		}

		public String getHandledTasktype()
		{
			return TestTasktypes.UPLOAD_GROUPS;
		}

		protected long getRescheduleDelay()
		{
			return 100;
		}
	}
}
