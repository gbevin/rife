/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MemoryTaskoptions.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.uwyn.rife.scheduler.Scheduler;
import com.uwyn.rife.scheduler.Taskoption;
import com.uwyn.rife.scheduler.TaskoptionManager;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;
import com.uwyn.rife.scheduler.taskoptionmanagers.exceptions.AddTaskoptionErrorException;
import com.uwyn.rife.scheduler.taskoptionmanagers.exceptions.DuplicateTaskoptionException;
import com.uwyn.rife.scheduler.taskoptionmanagers.exceptions.InexistentTaskIdException;
import com.uwyn.rife.scheduler.taskoptionmanagers.exceptions.UpdateTaskoptionErrorException;

public class MemoryTaskoptions implements TaskoptionManager
{
	private Scheduler							mScheduler = null;
	private Map<Integer, ArrayList<Taskoption>>	mTaskoptionsMapping = null;
	
	public MemoryTaskoptions()
	{
		mTaskoptionsMapping = new HashMap<Integer, ArrayList<Taskoption>>();
	}

	public void setScheduler(Scheduler scheduler)
	{
		mScheduler = scheduler;
	}
	
	public Scheduler getScheduler()
	{
		return mScheduler;
	}
	
	public boolean addTaskoption(Taskoption taskoption)
	throws TaskoptionManagerException
	{
		if (null == taskoption)			throw new IllegalArgumentException("taskoption can't be null.");
		if (taskoption.getTaskId() < 0)	throw new IllegalArgumentException("the task id is required.");
		
		synchronized (this)
		{
			Taskoption cloned_taskoption = null;
			try
			{
				cloned_taskoption = taskoption.clone();
			}
			catch (CloneNotSupportedException e)
			{
				throw new AddTaskoptionErrorException(cloned_taskoption, e);
			}
			
			// check if the task id exists
			try
			{
				if (null == getScheduler().getTaskManager().getTask(cloned_taskoption.getTaskId()))
				{
					throw new InexistentTaskIdException(cloned_taskoption.getTaskId());
				}
			}
			catch (TaskManagerException e)
			{
				throw new AddTaskoptionErrorException(cloned_taskoption, e);
			}
			
			// get the taskoptions list for the same task id
			int						task_id = cloned_taskoption.getTaskId();
			ArrayList<Taskoption>	taskoptions = mTaskoptionsMapping.get(task_id);
			if (null == taskoptions)
			{
				// no list exists, create one
				taskoptions = new ArrayList<Taskoption>();
				mTaskoptionsMapping.put(cloned_taskoption.getTaskId(), taskoptions);
			}
			else
			{
				// list exists, check if the same taskoption isn't already present
				// and throw an exception if that is the case
				for (Taskoption taskoption_to_check : taskoptions)
				{
					if (taskoption_to_check.getName().equals(cloned_taskoption.getName()))
					{
						throw new DuplicateTaskoptionException(cloned_taskoption.getTaskId(), cloned_taskoption.getName());
					}
				}
			}
			
			taskoptions.add(cloned_taskoption);
			
			return true;
		}
	}

	public boolean updateTaskoption(Taskoption taskoption)
	throws TaskoptionManagerException
	{
		if (null == taskoption)			throw new IllegalArgumentException("taskoption can't be null.");
		if (taskoption.getTaskId() < 0)	throw new IllegalArgumentException("the task id is required.");
		
		synchronized (this)
		{
			Taskoption cloned_taskoption = null;
			try
			{
				cloned_taskoption = taskoption.clone();
			}
			catch (CloneNotSupportedException e)
			{
				throw new AddTaskoptionErrorException(cloned_taskoption, e);
			}
			
			// check if the task id exists
			try
			{
				if (null == getScheduler().getTaskManager().getTask(cloned_taskoption.getTaskId()))
				{
					throw new InexistentTaskIdException(cloned_taskoption.getTaskId());
				}
			}
			catch (TaskManagerException e)
			{
				throw new UpdateTaskoptionErrorException(cloned_taskoption, e);
			}
			
			// get the taskoptions for the same task id
			int						task_id = cloned_taskoption.getTaskId();
			ArrayList<Taskoption>	taskoptions = mTaskoptionsMapping.get(task_id);
			if (null == taskoptions)
			{
				return false;
			}
			// obtain the taskoption with same name
			Taskoption	taskoption_to_remove = null;
			for (Taskoption taskoption_to_check : taskoptions)
			{
				if (taskoption_to_check.getName().equals(cloned_taskoption.getName()))
				{
					taskoption_to_remove = taskoption_to_check;
					break;
				}
			}
			
			// no match was found
			if (null == taskoption_to_remove)
			{
				return false;
			}
			
			// remove the old taskoption and store the new one
			taskoptions.remove(taskoption_to_remove);
			taskoptions.add(cloned_taskoption);

			return true;
		}
	}

	public Taskoption getTaskoption(int taskid, String name)
	throws TaskoptionManagerException
	{
		if (taskid < 0)			throw new IllegalArgumentException("taskid can't be negative.");
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		synchronized (this)
		{
			// get the taskoptions for the same task id
			ArrayList<Taskoption>	taskoptions = mTaskoptionsMapping.get(taskid);
			if (null == taskoptions)
			{
				return null;
			}
			// obtain the taskoption with same name
			for (Taskoption taskoption : taskoptions)
			{
				if (taskoption.getName().equals(name))
				{
					return taskoption;
				}
			}
		}
		
		return null;
	}

	public Collection<Taskoption> getTaskoptions(int taskid)
	throws TaskoptionManagerException
	{
		if (taskid < 0)	throw new IllegalArgumentException("taskid can't be negative.");
		
		return mTaskoptionsMapping.get(taskid);
	}

	public boolean removeTaskoption(Taskoption taskoption)
	throws TaskoptionManagerException
	{
		if (null == taskoption)	throw new IllegalArgumentException("taskoption can't be null.");
		
		return removeTaskoption(taskoption.getTaskId(), taskoption.getName());
	}

	public boolean removeTaskoption(int taskid, String name)
	throws TaskoptionManagerException
	{
		if (taskid < 0)			throw new IllegalArgumentException("taskid can't be negative.");
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		synchronized (this)
		{
			// get the taskoptions for the same task id
			ArrayList<Taskoption>	taskoptions = mTaskoptionsMapping.get(taskid);
			if (null == taskoptions)
			{
				return false;
			}
			// obtain the taskoption with same name
			Taskoption	taskoption_to_remove = null;
			for (Taskoption taskoption : taskoptions)
			{
				if (taskoption.getName().equals(name))
				{
					taskoption_to_remove = taskoption;
					break;
				}
			}
			
			if (null == taskoption_to_remove)
			{
				return false;
			}
			
			taskoptions.remove(taskoption_to_remove);
		}
		
		return true;
	}
}
