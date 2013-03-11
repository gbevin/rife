/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Scheduler.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler;

import com.uwyn.rife.scheduler.exceptions.*;

import java.util.Collection;
import java.util.HashMap;

public class Scheduler extends Thread
{
	private TaskManager					mTaskManager = null;
	private TaskoptionManager			mTaskoptionManager = null;
	private int							mSleepTime = 500;
	private HashMap<Object, Executor>	mExecutors = null;

	public Scheduler(TaskManager taskManager, TaskoptionManager taskoptionManager)
	{
		super("SCHEDULER_DEAMON");

		setDaemon(true);
		setTaskManager(taskManager);
		setTaskoptionManager(taskoptionManager);
		mExecutors = new HashMap<Object, Executor>();
	}

	public void setTaskManager(TaskManager taskManager)
	{
		if (null == taskManager)	throw new IllegalArgumentException("taskManager can't be null.");

		mTaskManager = taskManager;
		taskManager.setScheduler(this);
	}

	public TaskManager getTaskManager()
	{
		return mTaskManager;
	}

	public void setTaskoptionManager(TaskoptionManager taskoptionManager)
	{
		if (null == taskoptionManager)	throw new IllegalArgumentException("taskoptionManager can't be null.");

		mTaskoptionManager = taskoptionManager;
		taskoptionManager.setScheduler(this);
	}

	public TaskoptionManager getTaskoptionManager()
	{
		return mTaskoptionManager;
	}

	public boolean addExecutor(Executor executor)
	throws SchedulerException
	{
		if (null == executor)	throw new IllegalArgumentException("executor can't be null.");
		
		if (null == executor.getScheduler())
		{
			mExecutors.put(executor.getHandledTasktype(), executor);
			executor.setScheduler(this);
		}
		else if (this == executor.getScheduler())
		{
			return false;
		}
		else
		{
			throw new ExecutorAlreadyRegisteredException(executor);
		}
	
		assert mExecutors.containsKey(executor.getHandledTasktype());
		assert executor == mExecutors.get(executor.getHandledTasktype());
		assert this == executor.getScheduler();
			
		return true;
	}

	public boolean removeExecutor(Executor executor)
	{
		if (null == executor)	throw new IllegalArgumentException("executor can't be null.");

		if (null == mExecutors.remove(executor.getHandledTasktype()))
		{
			return false;
		}

		executor.setScheduler(null);
		
		assert !mExecutors.containsKey(executor.getHandledTasktype());
		assert null == executor.getScheduler();
		
		return true;
	}

	public Executor getExecutor(String tasktype)
	{
		if (null == tasktype)	throw new IllegalArgumentException("tasktype can't be null.");

		return mExecutors.get(tasktype);
	}

	public Collection<Executor> getExecutors()
	{
		return mExecutors.values();
	}

	public void setSleepTime(int sleeptime)
	{
		if (sleeptime <= 0)	throw new IllegalArgumentException("sleeptime has to be bigger than 0.");

		mSleepTime = sleeptime;
	}

	public void run()
	{
		while (true)
		{
			try
			{
				if (!isInterrupted())
				{
					scheduleStep();
					// Ensure that the wake up is always on an even multiplier of the
					// sleep time, this to ensure that no drift occurs.
					long now = System.currentTimeMillis();
					long projected = ((System.currentTimeMillis()+mSleepTime)/mSleepTime)*mSleepTime;
					long difference = projected-now;
					
					Thread.sleep(difference);
				}
				else
				{
					break;
				}
			}
			catch (InterruptedException e)
			{
				break;
			}
		}

		synchronized (this)
		{
			notifyAll();
		}
	}

	private void scheduleStep()
	throws SchedulerExecutionException
	{
		assert mTaskManager != null;
		
		try
		{
			Executor	executor = null;
			for (Task task : mTaskManager.getTasksToProcess())
			{
				executor = mExecutors.get(task.getType());
				if (null != executor)
				{
					executor.startTaskExecution(task);
				}
				else
				{
					throw new NoExecutorForTasktypeException(task.getType());
				}
			}
		}
		catch (TaskManagerException e)
		{
			throw new UnableToRetrieveTasksToProcessException(e);
		}
	}
}
