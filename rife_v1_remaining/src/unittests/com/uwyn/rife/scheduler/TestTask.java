/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestTask.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler;

import com.uwyn.rife.scheduler.Task;
import com.uwyn.rife.scheduler.exceptions.FrequencyException;
import com.uwyn.rife.site.ValidationError;
import com.uwyn.rife.tools.ExceptionUtils;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;

public class TestTask extends TestCase
{
	public TestTask(String name)
	{
		super(name);
	}

	public void testInstantiateTask()
	{
		Task	task = null;
		
		assertNull(task);
		task = new Task();
		assertNotNull(task);
	}

	public void testPopulateTask()
	{
		int		id = 1;
		String	type = TestTasktypes.UPLOAD_GROUPS;
		long	planned = System.currentTimeMillis();
		String	frequency = "* * * * *";
		boolean	busy = true;

		try
		{
			Task task = new Task();
			task.setId(id);
			task.setType(type);
			task.setPlanned(planned);
			task.setFrequency(frequency);
			task.setBusy(busy);
			
			assertEquals(id, task.getId());
			assertEquals(type, task.getType());
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(planned);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			assertEquals(calendar.getTimeInMillis(), task.getPlanned());
			assertEquals(frequency, task.getFrequency());
			assertEquals(busy, task.isBusy());
		}
		catch (FrequencyException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCloneTask()
	{
		int		id = 1;
		String	type = TestTasktypes.UPLOAD_GROUPS;
		long	planned = System.currentTimeMillis();
		String	frequency = "* * * * *";
		boolean	busy = true;

		try
		{
			Task task = new Task();
			task.setId(id);
			task.setType(type);
			task.setPlanned(planned);
			task.setFrequency(frequency);
			task.setBusy(busy);
			
			Task task_clone = task.clone();
			assertTrue(task != task_clone);
			assertNotNull(task_clone);
			assertTrue(task.equals(task_clone));
		}
		catch (FrequencyException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (CloneNotSupportedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testTaskValidation()
	{
		try
		{
			Task task = new Task();
			assertTrue(false == task.validate());
			assertTrue(2 == task.countValidationErrors());
			ValidationError error = null;
			Iterator<ValidationError> it = task.getValidationErrors().iterator();
			error = it.next();
			assertEquals(error.getIdentifier(), "MANDATORY");
			assertEquals(error.getSubject(), "type");
			error = it.next();
			assertEquals(error.getIdentifier(), "MANDATORY");
			assertEquals(error.getSubject(), "planned");
			
			task.setType(TestTasktypes.UPLOAD_GROUPS);
			task.setPlanned(System.currentTimeMillis()+2000);
			task.setFrequency("* * * * *");
			task.setBusy(false);
			task.resetValidation();
			assertTrue(true == task.validate());
			
			Calendar cal = Calendar.getInstance();
			cal.set(1970, 10, 25);
			task.setPlanned(cal.getTime());
			task.resetValidation();
			assertTrue(false == task.validate());
			assertTrue(1 == task.countValidationErrors());
			Set<ValidationError> set = task.getValidationErrors();
			error = set.iterator().next();
			assertEquals(error.getIdentifier(), "INVALID");
			assertEquals(error.getSubject(), "planned");
			task.setPlanned(System.currentTimeMillis()+2000);
			
			task.setFrequency(null);
			task.resetValidation();
			assertTrue(true == task.validate());
			task.setFrequency("* * * * *");
		}
		catch (FrequencyException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
}
