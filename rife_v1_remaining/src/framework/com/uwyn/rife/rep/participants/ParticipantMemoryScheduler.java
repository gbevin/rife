/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticipantMemoryScheduler.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.rep.participants;
import com.uwyn.rife.rep.BlockingParticipant;
import com.uwyn.rife.scheduler.Scheduler;
import com.uwyn.rife.scheduler.exceptions.SchedulerManagerException;
import com.uwyn.rife.scheduler.schedulermanagers.MemoryScheduler;

public class ParticipantMemoryScheduler extends BlockingParticipant
{
	private Scheduler	mScheduler = null;

	public ParticipantMemoryScheduler()
	{
		setInitializationMessage("Populating in-memory scheduler and starting the execution ...");
		setCleanupMessage("Stopping in-memory scheduler and cleaning it up ...");
	}
	
	protected void initialize()
	{
		try
		{
			MemoryScheduler scheduler_factory = new MemoryScheduler(this.getParameter(), getResourceFinder());
			mScheduler = scheduler_factory.getScheduler();
			mScheduler.start();
		}
		catch (SchedulerManagerException e)
		{
			throw new RuntimeException("Fatal error during the initialization while populating the in-memory scheduler.", e);
		}
	}
	
	protected void cleanup()
	{
		if (mScheduler  != null)
		{
			mScheduler.interrupt();
		}
	}

	protected Object _getObject(Object key)
	{
		return mScheduler;
	}
}

