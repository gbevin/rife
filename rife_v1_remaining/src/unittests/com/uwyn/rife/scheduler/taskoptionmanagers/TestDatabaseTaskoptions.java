/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseTaskoptions.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.scheduler.Task;
import com.uwyn.rife.scheduler.TaskManager;
import com.uwyn.rife.scheduler.Taskoption;
import com.uwyn.rife.scheduler.TaskoptionManager;
import com.uwyn.rife.scheduler.TestTasktypes;
import com.uwyn.rife.scheduler.exceptions.SchedulerException;
import com.uwyn.rife.scheduler.exceptions.SchedulerManagerException;
import com.uwyn.rife.scheduler.exceptions.TaskoptionManagerException;
import com.uwyn.rife.scheduler.schedulermanagers.DatabaseScheduler;
import com.uwyn.rife.scheduler.schedulermanagers.DatabaseSchedulerFactory;
import com.uwyn.rife.scheduler.taskoptionmanagers.exceptions.InexistentTaskIdException;
import com.uwyn.rife.tools.ExceptionUtils;
import java.util.Calendar;
import java.util.Collection;
import junit.framework.TestCase;

public class TestDatabaseTaskoptions extends TestCase
{
	private Datasource  mDatasource = null;
    
	public TestDatabaseTaskoptions(Datasource datasource, String datasourceName, String name)
	{
		super(name);
		mDatasource = datasource;
	}
	
	public void testInstall()
	{
		DatabaseScheduler manager = DatabaseSchedulerFactory.getInstance(mDatasource);

		try
		{
			assertTrue(true == manager.install());
		}
		catch (SchedulerManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testRemove()
	{
		DatabaseScheduler manager = DatabaseSchedulerFactory.getInstance(mDatasource);

		try
		{
			assertTrue(true == manager.remove());
		}
		catch (SchedulerManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	 }

	public void testInstantiateTaskoptionManager()
	{
		TaskoptionManager manager = DatabaseTaskoptionsFactory.getInstance(mDatasource);
		assertNotNull(manager);
	}

	public void testAddTaskoptionWithInexistentTaskId()
	{
		DatabaseScheduler schedulermanager = DatabaseSchedulerFactory.getInstance(mDatasource);
		try
		{
			schedulermanager.install();
		}
		catch (SchedulerManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		Taskoption taskoption = new Taskoption();
		taskoption.setTaskId(0);
		taskoption.setName("name");
		taskoption.setValue("value");

		TaskoptionManager manager = DatabaseTaskoptionsFactory.getInstance(mDatasource);
		try
		{
			manager.addTaskoption(taskoption);
			fail();
		}
		catch (InexistentTaskIdException e)
		{
			assertTrue(true);
		}
		catch (TaskoptionManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testAddTaskoption()
	{
		int					task_id = 0;
		Task				task = new Task();
		TaskManager 		task_manager = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler().getTaskManager();
		TaskoptionManager	taskoption_manager = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler().getTaskoptionManager();
		try
		{
			Calendar	cal = Calendar.getInstance();
			cal.set(2001, 10, 24, 0, 0, 0);

			task.setType(TestTasktypes.UPLOAD_GROUPS);
			task.setPlanned(cal.getTime());
			task.setFrequency("* * * * *");
			
			task_id = task_manager.addTask(task);
			assertTrue(task_id >= 0);

			String taskoption_name = "name";
			String value = "value";
	
			Taskoption taskoption = new Taskoption();
			taskoption.setTaskId(task_id);
			taskoption.setName(taskoption_name);
			taskoption.setValue(value);

			assertTrue(taskoption_manager.addTaskoption(taskoption));
		}
		catch (SchedulerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testAddDuplicateTaskoption()
	{
		int					task_id = 0;
		Task				task = new Task();
		TaskManager 		task_manager = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler().getTaskManager();
		TaskoptionManager	taskoption_manager = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler().getTaskoptionManager();
		try
		{
			Calendar	cal = Calendar.getInstance();
			cal.set(2001, 10, 24, 0, 0, 0);
			
			task.setType(TestTasktypes.UPLOAD_GROUPS);
			task.setPlanned(cal.getTime());
			task.setFrequency("* * * * *");
			
			task_id = task_manager.addTask(task);
			assertTrue(task_id >= 0);

			String taskoption_name = "name";
			String value = "value";
	
			Taskoption taskoption = new Taskoption();
			taskoption.setTaskId(task_id);
			taskoption.setName(taskoption_name);
			taskoption.setValue(value);

			assertTrue(taskoption_manager.addTaskoption(taskoption));
			
			taskoption_manager.addTaskoption(taskoption);
			fail();
		}
		catch (SchedulerException e)
		{
			assertTrue(true);
		}
	}

	public void testGetTaskoption()
	{
		int					task_id = 0;
		Task				task = new Task();
		TaskManager 		task_manager = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler().getTaskManager();
		TaskoptionManager	taskoption_manager = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler().getTaskoptionManager();
		try
		{
			Calendar	cal = Calendar.getInstance();
			cal.set(2001, 10, 24, 0, 0, 0);

			task.setType(TestTasktypes.UPLOAD_GROUPS);
			task.setFrequency("* * * * *");
			
			task_id = task_manager.addTask(task);
			assertTrue(task_id >= 0);

			String taskoption_name = "name";
			String value = "value";
	
			Taskoption taskoption = new Taskoption();
			taskoption.setTaskId(task_id);
			taskoption.setName(taskoption_name);
			taskoption.setValue(value);

			assertTrue(taskoption_manager.addTaskoption(taskoption));
			
			taskoption = taskoption_manager.getTaskoption(task_id, taskoption_name);
			assertNotNull(taskoption);

			assertEquals(taskoption.getTaskId(), task_id);
			assertEquals(taskoption.getName(), taskoption_name);
			assertEquals(taskoption.getValue(), value);
			
			task = task_manager.getTask(task_id);
			assertEquals(task.getTaskoptionValue(taskoption_name), value);
		}
		catch (SchedulerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testUpdateTaskoption()
	{
		int					task_id = 0;
		Task				task = new Task();
		TaskManager 		task_manager = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler().getTaskManager();
		TaskoptionManager	taskoption_manager = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler().getTaskoptionManager();
		try
		{
			Calendar	cal = Calendar.getInstance();
			cal.set(2001, 10, 24, 0, 0, 0);

			task.setType(TestTasktypes.UPLOAD_GROUPS);
			task.setPlanned(cal.getTime());
			task.setFrequency("* * * * *");
			
			task_id = task_manager.addTask(task);
			assertTrue(task_id >= 0);

			String taskoption_name = "name";
			String value = "value";
	
			Taskoption taskoption = new Taskoption();
			taskoption.setTaskId(task_id);
			taskoption.setName(taskoption_name);
			taskoption.setValue(value);

			assertTrue(taskoption_manager.addTaskoption(taskoption));
			
			value = "new_value";

			taskoption = new Taskoption();
			taskoption.setTaskId(task_id);
			taskoption.setName(taskoption_name);
			taskoption.setValue(value);

			assertTrue(true == taskoption_manager.updateTaskoption(taskoption));

			taskoption = taskoption_manager.getTaskoption(task_id, taskoption_name);
			assertNotNull(taskoption);

			assertEquals(taskoption.getTaskId(), task_id);
			assertEquals(taskoption.getName(), taskoption_name);
			assertEquals(taskoption.getValue(), value);
		}
		catch (SchedulerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetTaskoptions()
	{
		int					task_id = 0;
		Task				task = new Task();
		TaskManager 		task_manager = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler().getTaskManager();
		TaskoptionManager	taskoption_manager = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler().getTaskoptionManager();
		try
		{
			Calendar	cal = Calendar.getInstance();
			cal.set(2001, 10, 24, 0, 0, 0);

			task.setType(TestTasktypes.UPLOAD_GROUPS);
			task.setPlanned(cal.getTime());
			task.setFrequency("* * * * *");
			
			task_id = task_manager.addTask(task);
			assertTrue(task_id >= 0);

			String taskoption_name = "name";
			String value = "some_value";
	
			Taskoption taskoption = new Taskoption();
			taskoption.setTaskId(task_id);
			taskoption.setName(taskoption_name);
			taskoption.setValue(value);

			assertTrue(taskoption_manager.addTaskoption(taskoption));
			
			Collection<Taskoption> taskoptions = taskoption_manager.getTaskoptions(task_id);
			assertEquals(1, taskoptions.size());

			taskoption = taskoptions.iterator().next();
			assertEquals(taskoption.getTaskId(), task_id);
			assertEquals(taskoption.getName(), taskoption_name);
			assertEquals(taskoption.getValue(), "some_value");
		}
		catch (SchedulerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveTaskoption()
	{
		int					task_id = 0;
		Task				task = new Task();
		TaskManager 		task_manager = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler().getTaskManager();
		TaskoptionManager	taskoption_manager = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler().getTaskoptionManager();
		try
		{
			Calendar	cal = Calendar.getInstance();
			cal.set(2001, 10, 24, 0, 0, 0);

			task.setType(TestTasktypes.UPLOAD_GROUPS);
			task.setPlanned(cal.getTime());
			task.setFrequency("* * * * *");
			
			task_id = task_manager.addTask(task);
			assertTrue(task_id >= 0);

			String taskoption_name = "name";
			String value = "value";
	
			Taskoption taskoption = new Taskoption();
			taskoption.setTaskId(task_id);
			taskoption.setName(taskoption_name);
			taskoption.setValue(value);

			assertTrue(taskoption_manager.addTaskoption(taskoption));
			
			assertTrue(taskoption_manager.removeTaskoption(task_id, taskoption_name));
			
			assertTrue(task_manager.removeTask(task_id));
			task_id = 0;
			taskoption_name = null;
		}
		catch (SchedulerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetNonExistingTaskoption()
	{
		TaskoptionManager	manager = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler().getTaskoptionManager();
		int task_nonexisting_id = 340;
		try
		{
			assertNull(manager.getTaskoption(task_nonexisting_id, "unknownname"));
		}
		catch (TaskoptionManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveNonExistingTaskoption()
	{
		TaskoptionManager	manager = DatabaseSchedulerFactory.getInstance(mDatasource).getScheduler().getTaskoptionManager();
		int task_nonexisting_id = 120;
		try
		{
			assertTrue(false == manager.removeTaskoption(task_nonexisting_id, "unknownname"));
		}
		catch (TaskoptionManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		DatabaseScheduler schedulermanager = DatabaseSchedulerFactory.getInstance(mDatasource);
		try
		{
			schedulermanager.remove();
		}
		catch (SchedulerManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
}
