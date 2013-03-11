/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Task.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.scheduler.exceptions.FrequencyException;
import com.uwyn.rife.scheduler.exceptions.SchedulerException;
import com.uwyn.rife.site.Validation;
import com.uwyn.rife.site.ValidationError;
import com.uwyn.rife.site.ValidationRule;
import com.uwyn.rife.site.ValidationRuleNotEmpty;
import com.uwyn.rife.site.ValidationRuleNotNull;
import com.uwyn.rife.tools.Localization;
import java.util.Calendar;
import java.util.Date;

public class Task extends Validation implements Cloneable
{
	private int			mId = -1;
	private String		mType = null;
    private long		mPlanned = 0;
    private Frequency	mFrequency = null;
	private boolean		mBusy = false;
	
	private TaskManager	mTaskManager = null;

	public Task()
	{
	}
	
	protected void activateValidation()
	{
		addRule(new ValidationRuleNotNull("type"));
		addRule(new ValidationRuleNotEmpty("planned"));
		addRule(new InvalidPlanned());
	}

	public void setTaskManager(TaskManager taskManager)
	{
		mTaskManager = taskManager;
	}
	
	public TaskManager getTaskManager()
	{
		return mTaskManager;
	}
	
	public String getTaskoptionValue(String name)
	throws SchedulerException
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		if (null == mTaskManager)
		{
			return null;
		}
		
		Scheduler scheduler = mTaskManager.getScheduler();
		if (null == scheduler)
		{
			return null;
		}
		
		TaskoptionManager taskoption_manager = scheduler.getTaskoptionManager();
		if (null == taskoption_manager)
		{
			return null;
		}
		
		Taskoption	taskoption = taskoption_manager.getTaskoption(getId(), name);
		if (null == taskoption)
		{
			return null;
		}
		
		return taskoption.getValue();
	}
	
	public long getNextDate()
	throws FrequencyException
	{
		// lower towards the minute, remove seconds and milliseconds
		Calendar current_calendar = Calendar.getInstance(RifeConfig.Tools.getDefaultTimeZone(), Localization.getLocale());
		current_calendar.set(Calendar.SECOND, 0);
		current_calendar.set(Calendar.MILLISECOND, 0);
		long current_time = current_calendar.getTimeInMillis();
		if (mPlanned <= current_time)
		{
			return getNextDate(current_time);
		}

		return -1;
	}
	
	public long getNextDate(long start)
	throws FrequencyException
	{
		if (null == mFrequency)
		{
			return -1;
		}
		else
		{
			return mFrequency.getNextDate(start);
		}
	}

	public void setId(int id)
	{
		mId = id;
	}

	public int getId()
	{
		return mId;
	}

	public void setType(String type)
	{
		mType = type;
	}

	public String getType()
	{
		return mType;
	}

	public void setPlanned(Date planned)
	{
		setPlanned(planned.getTime());
	}

	public void setPlanned(long planned)
	{
		// lower towards the minute, remove seconds and milliseconds
		Calendar planned_calendar = Calendar.getInstance(RifeConfig.Tools.getDefaultTimeZone(), Localization.getLocale());
		planned_calendar.setTimeInMillis(planned);
		planned_calendar.set(Calendar.SECOND, 0);
		planned_calendar.set(Calendar.MILLISECOND, 0);
		
		mPlanned = planned_calendar.getTimeInMillis();
	}

	public long getPlanned()
	{
		return mPlanned;
	}

	public void setFrequency(String frequency)
	throws FrequencyException
	{
		if (null == frequency)
		{
			mFrequency = null;
		}
		else
		{
			mFrequency = new Frequency(frequency);
		}
	}

	public String getFrequency()
	{
		if (null == mFrequency)
		{
			return null;
		}
		return mFrequency.getFrequency();
	}

	public void setBusy(boolean busy)
	{
		mBusy = busy;
	}

	public boolean isBusy()
	{
		return mBusy;
	}

	public Task clone()
	throws CloneNotSupportedException
	{
		return (Task)super.clone();
	}

	public boolean equals(Object object)
	{
		Task other_task = (Task)object;

		if (null != object &&
			other_task.getId() == getId() &&
			other_task.getType().equals(getType()) &&
			other_task.getPlanned() == getPlanned() &&
			((null == other_task.getFrequency() && null == getFrequency()) ||
			 (other_task.getFrequency() != null && other_task.getFrequency().equals(getFrequency()))) &&
			other_task.getTaskManager() == getTaskManager())
		{
			return true;
		}
		
		return false;
	}

	public class InvalidPlanned implements ValidationRule
	{
		public boolean validate()
		{
			if (0 == mPlanned)
			{
				return true;
			}
			
			Calendar current_calendar = Calendar.getInstance(RifeConfig.Tools.getDefaultTimeZone(), Localization.getLocale());
			current_calendar.set(Calendar.SECOND, 0);
			current_calendar.set(Calendar.MILLISECOND, 0);
			return mPlanned >= current_calendar.getTimeInMillis();
		}

		public String getSubject()
		{
			return "planned";
		}

		public ValidationError getError()
		{
			return new ValidationError.INVALID(getSubject());
		}
		
		public Object getBean()
		{
			return null;
		}
		
		public <T extends ValidationRule> T setBean(Object bean)
		{
			return (T)this;
		}
		
		public Object clone()
		{
			return this;
		}
	}
}
