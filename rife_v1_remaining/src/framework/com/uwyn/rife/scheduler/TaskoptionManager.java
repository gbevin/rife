/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TaskoptionManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler;

import com.uwyn.rife.scheduler.Taskoption;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;
import java.util.Collection;

public interface TaskoptionManager
{
	public void setScheduler(Scheduler scheduler);
	public Scheduler getScheduler();
	public boolean addTaskoption(Taskoption taskoption) throws TaskoptionManagerException;
	public boolean updateTaskoption(Taskoption taskoption) throws TaskoptionManagerException;
	public Taskoption getTaskoption(int taskid, String name) throws TaskoptionManagerException;
	public Collection<Taskoption> getTaskoptions(int taskid) throws TaskoptionManagerException;
	public boolean removeTaskoption(Taskoption taskoption) throws TaskoptionManagerException;
	public boolean removeTaskoption(int taskid, String name) throws TaskoptionManagerException;
}
