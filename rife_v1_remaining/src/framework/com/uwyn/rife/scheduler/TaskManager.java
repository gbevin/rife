/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TaskManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler;

import com.uwyn.rife.scheduler.Task;
import com.uwyn.rife.scheduler.exceptions.TaskManagerException;
import java.util.Collection;

public interface TaskManager
{
	public void setScheduler(Scheduler scheduler);
	public Scheduler getScheduler();
	public int addTask(Task task) throws TaskManagerException;
	public boolean updateTask(Task task) throws TaskManagerException;
	public Task getTask(int id) throws TaskManagerException;
	public Collection<Task> getTasksToProcess() throws TaskManagerException;
	public Collection<Task> getScheduledTasks() throws TaskManagerException;
	public boolean removeTask(int id) throws TaskManagerException;
	public boolean rescheduleTask(Task task, long interval, String frequency) throws TaskManagerException;
	public boolean concludeTask(Task task) throws TaskManagerException;
	public boolean activateTask(int id) throws TaskManagerException;
	public boolean deactivateTask(int id) throws TaskManagerException;
}
