/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExecutorThread.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler;

import com.uwyn.rife.scheduler.exceptions.*;

public class ExecutorThread implements Runnable
{
	private Executor	mExecutor = null;
	private Task		mTask = null;
	
	public ExecutorThread(Executor executor, Task task)
	{
		mExecutor = executor;
		mTask = task;
	}

	public void run()
	{
		assert mTask != null;
		
		boolean		successful_execution = false;
		TaskManager	manager = mExecutor.getScheduler().getTaskManager();
		
		try
		{
			manager.activateTask(mTask.getId());
			successful_execution = mExecutor.executeTask(mTask);
		}
		catch (TaskManagerException e)
		{
			successful_execution = false;
		}
		finally
		{
			try
			{
				if (!successful_execution)
				{
					manager.rescheduleTask(mTask, mExecutor.getRescheduleDelay(), null);
				}
				manager.concludeTask(mTask);
			}
			catch (TaskManagerException e)
			{
				throw new FatalTaskExecutionException(mTask, e);
			}
		}
	}
}
