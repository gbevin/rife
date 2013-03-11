/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestTaskoption.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler;

import com.uwyn.rife.scheduler.Taskoption;
import com.uwyn.rife.site.ValidationError;
import com.uwyn.rife.tools.ExceptionUtils;
import java.util.Iterator;
import junit.framework.TestCase;

public class TestTaskoption extends TestCase
{
	public TestTaskoption(String name)
	{
		super(name);
	}

	public void testInstantiateTaskoption()
	{
		Taskoption	taskoption = null;
		assertNull(taskoption);
		taskoption = new Taskoption();
		assertNotNull(taskoption);
	}

	public void testPopulateTaskoption()
	{
		int		taskid = 1;
		String	name = "name";
		String	value = "value";

		Taskoption taskoption = new Taskoption();
		taskoption.setTaskId(taskid);
		taskoption.setName(name);
		taskoption.setValue(value);

		assertEquals(taskid, taskoption.getTaskId());
		assertEquals(name, taskoption.getName());
		assertEquals(value, taskoption.getValue());
	}

	public void testCloneTaskoption()
	{
		try
		{
			int		taskid = 1;
			String	name = "name";
			String	value = "value";
			
			Taskoption taskoption = new Taskoption();
			taskoption.setTaskId(taskid);
			taskoption.setName(name);
			taskoption.setValue(value);
			
			Taskoption taskoption_clone = taskoption.clone();
			assertTrue(taskoption != taskoption_clone);
			assertNotNull(taskoption_clone);
			assertTrue(taskoption.equals(taskoption_clone));
		}
		catch (CloneNotSupportedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testTaskoptionValidation()
	{
		Taskoption taskoption = new Taskoption();
		assertTrue(false == taskoption.validate());
		assertTrue(3 == taskoption.countValidationErrors());
		ValidationError error = null;
		Iterator<ValidationError> it = taskoption.getValidationErrors().iterator();
		error = it.next();
		assertEquals(error.getIdentifier(), "INVALID");
		assertEquals(error.getSubject(), "taskId");
		error = it.next();
		assertEquals(error.getIdentifier(), "MANDATORY");
		assertEquals(error.getSubject(), "name");
		error = it.next();
		assertEquals(error.getIdentifier(), "MANDATORY");
		assertEquals(error.getSubject(), "value");
	}
}
