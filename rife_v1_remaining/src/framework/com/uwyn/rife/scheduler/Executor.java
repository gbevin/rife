/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Executor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler;

import com.uwyn.rife.scheduler.exceptions.SchedulerExecutionException;

public abstract class Executor
{
	private Scheduler	mScheduler = null;

	public Scheduler getScheduler()
	{
		return mScheduler;
	}

	final void startTaskExecution(Task task)
	throws SchedulerExecutionException
	{
		assert task != null;
		
		ExecutorThread executor_thread = new ExecutorThread(this, task);
		Thread thread = new Thread(executor_thread, getHandledTasktype());
		thread.start();
	}

	void setScheduler(Scheduler scheduler)
	{
		mScheduler = scheduler;
	}

	protected long getRescheduleDelay()
	{
		return 1000;
	}
	
	public abstract boolean executeTask(Task task);
	public abstract String getHandledTasktype();
}
