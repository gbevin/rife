/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MemoryTasks.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskmanagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.uwyn.rife.scheduler.Scheduler;
import com.uwyn.rife.scheduler.Task;
import com.uwyn.rife.scheduler.TaskManager;
import com.uwyn.rife.scheduler.exceptions.FrequencyException;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;
import com.uwyn.rife.scheduler.taskmanagers.exceptions.ConcludeTaskErrorException;
import com.uwyn.rife.scheduler.taskmanagers.exceptions.RescheduleTaskErrorException;

public class MemoryTasks implements TaskManager
{
	private Scheduler			mScheduler = null;
	private Map<Integer, Task>	mTaskMapping = null;
	private int					mTaskIdSequence = 0;
	
	public MemoryTasks()
	{
		mTaskMapping = new HashMap<Integer, Task>();
	}

	public void setScheduler(Scheduler scheduler)
	{
		mScheduler = scheduler;
	}
	
	public Scheduler getScheduler()
	{
		return mScheduler;
	}
	
	public int addTask(Task task)
	throws TaskManagerException
	{
		if (null == task)	throw new IllegalArgumentException("task can't be null.");
		
		synchronized (this)
		{
			// FIXME: check for integer overflow
			int task_id = mTaskIdSequence++;
			
			task.setId(task_id);
			mTaskMapping.put(task_id, task);
			task.setTaskManager(this);
			
			return task_id;
		}
	}

	public boolean updateTask(Task task)
	throws TaskManagerException
	{
		if (null == task)		throw new IllegalArgumentException("task can't be null.");
		if (task.getId() < 0)	throw new IllegalArgumentException("the task id is required.");
		
		synchronized (this)
		{
			int task_id = task.getId();
			
			if (!mTaskMapping.containsKey(task_id))
			{
				return false;
			}
			
			mTaskMapping.put(task_id, task);
			task.setTaskManager(this);
			
			return true;
		}
	}

	public Task getTask(int id)
	throws TaskManagerException
	{
		if (id < 0)	throw new IllegalArgumentException("the task id can't be negative.");

		return mTaskMapping.get(id);
	}

	public Collection<Task> getTasksToProcess()
	throws TaskManagerException
	{
		ArrayList<Task>	tasks_to_process = new ArrayList<Task>();
		
		synchronized (this)
		{
			for (Task task : mTaskMapping.values())
			{
				if (!task.isBusy() &&
					task.getPlanned() < System.currentTimeMillis())
				{
					tasks_to_process.add(task);
				}
			}
		}
		
		return tasks_to_process;
	}

	public Collection<Task> getScheduledTasks()
	throws TaskManagerException
	{
		ArrayList<Task>	scheduled_tasks = new ArrayList<Task>();
		
		synchronized (this)
		{
			for (Task task : mTaskMapping.values())
			{
				if (!task.isBusy() &&
					task.getPlanned() >= System.currentTimeMillis())
				{
					scheduled_tasks.add(task);
				}
			}
		}
		
		return scheduled_tasks;
	}

	public boolean removeTask(int id)
	throws TaskManagerException
	{
		if (id < 0)	throw new IllegalArgumentException("the task id can't be negative.");
		
		synchronized (this)
		{
			if (null == mTaskMapping.remove(id))
			{
				return false;
			}
			
			return true;
		}
	}

	public boolean rescheduleTask(Task task, long newPlanned, String frequency)
	throws TaskManagerException
	{
		if (null == task)		throw new IllegalArgumentException("task can't be null.");
		if (newPlanned <= 0)	throw new IllegalArgumentException("newPlanned has to be bigger than 0.");
		
		boolean result = false;

		Task task_tmp = null;
		try
		{
			task_tmp = task.clone();
			task_tmp.setPlanned(newPlanned);
			task_tmp.setFrequency(frequency);
		}
		catch (Throwable e)
		{
			if (null == frequency)
			{
				throw new RescheduleTaskErrorException(task.getId(), newPlanned, e);
			}
			else
			{
				throw new RescheduleTaskErrorException(task.getId(), newPlanned, frequency, e);
			}
		}
		result = updateTask(task_tmp);

		assert result;

		return result;
	}

	public boolean concludeTask(Task task)
	throws TaskManagerException
	{
		if (null == task)	throw new IllegalArgumentException("task can't be null.");

		if (task.getPlanned() <= System.currentTimeMillis())
		{
			if (null == task.getFrequency())
			{
				return removeTask(task.getId());
			}
			
			try
			{
				long next_date = task.getNextDate();
				if (next_date >= 0 &&
					rescheduleTask(task, next_date, task.getFrequency()) &&
					deactivateTask(task.getId()))
				{
					return true;
				}
			}
			catch (FrequencyException e)
			{
				throw new ConcludeTaskErrorException(task.getId(), e);
			}
		}

		return false;
	}

	public boolean activateTask(int id)
	throws TaskManagerException
	{
		if (id < 0)	throw new IllegalArgumentException("the task id can't be negative.");
		
		synchronized (this)
		{
			Task task = mTaskMapping.get(id);
			if (null == task)
			{
				return false;
			}
			task.setBusy(true);
			return true;
		}
	}

	public boolean deactivateTask(int id)
	throws TaskManagerException
	{
		if (id < 0)	throw new IllegalArgumentException("the task id can't be negative.");
		
		synchronized (this)
		{
			Task task = mTaskMapping.get(id);
			if (null == task)
			{
				return false;
			}
			task.setBusy(false);
			return true;
		}
	}
}
