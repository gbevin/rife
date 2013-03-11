/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RescheduleTaskErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskmanagers.exceptions;

import com.uwyn.rife.scheduler.exceptions.TaskManagerException;
import java.util.Date;

public class RescheduleTaskErrorException extends TaskManagerException
{
	private static final long serialVersionUID = -5612314711614259305L;
	
	private int		mId = -1;
	private long	mNewPlanned = -1;
	private String	mFrequency = null;
	
	public RescheduleTaskErrorException(int id, long newPlanned, String frequency, Throwable cause)
	{
		super("Error while trying to reschedule the task with id '"+id+"', planned at '"+new Date(newPlanned).toString()+"' with frequency '"+frequency+"'.", cause);
		
		mId = id;
		mNewPlanned = newPlanned;
		mFrequency = frequency;
	}
	
	public RescheduleTaskErrorException(int id, long newPlanned, Throwable cause)
	{
		super("Error while trying to reschedule the task with id '"+id+"', planned at '"+new Date(newPlanned).toString()+"' with no frequency.", cause);
		
		mId = id;
		mNewPlanned = newPlanned;
	}
	
	public int getTaskId()
	{
		return mId;
	}
	
	public long getNewPlanned()
	{
		return mNewPlanned;
	}
	
	public String getFrequency()
	{
		return mFrequency;
	}
}
