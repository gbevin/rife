/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSequenceValueDaffodil.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;
import com.uwyn.rife.database.exceptions.SequenceOperationRequiredException;

public class TestSequenceValueDaffodil extends TestSequenceValue
{
	public TestSequenceValueDaffodil(String name)
	{
		super(name);
	}

	public void testInstantiationDaffodil()
	{
		SequenceValue query = new SequenceValue(mDaffodil);
		assertNotNull(query);
		try
		{
			query.getSql();
			fail();
		}
		catch (SequenceNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "SequenceValue");
		}
	}

	public void testInvalidDaffodil()
	{
		SequenceValue query = new SequenceValue(mDaffodil);
		try
		{
			query.getSql();
			fail();
		}
		catch (SequenceNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "SequenceValue");
		}
		query.name("sequencename");
		try
		{
			query.getSql();
			fail();
		}
		catch (SequenceOperationRequiredException e)
		{
			assertEquals(e.getQueryName(), "SequenceValue");
		}
		query.clear();
		query.next();
		try
		{
			query.getSql();
			fail();
		}
		catch (SequenceNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "SequenceValue");
		}
		query.clear();
	}

	public void testClearDaffodil()
	{
		SequenceValue query = new SequenceValue(mDaffodil);
		query
			.name("sequencename")
			.next();
		assertNotNull(query.getSql());
		query
			.clear();
		try
		{
			query.getSql();
			fail();
		}
		catch (SequenceNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "SequenceValue");
		}
	}

	public void testNextDaffodil()
	{
		SequenceValue query = new SequenceValue(mDaffodil);
		query
			.name("sequencename")
			.next();
		assertEquals(query.getSql(), "SELECT sequencename.NEXTVAL FROM dual");
		assertTrue(execute(mDaffodil, query) >= 0);
	}

	public void testCurrentDaffodil()
	{
		SequenceValue query = new SequenceValue(mDaffodil);
		query
			.name("sequencename")
			.current();
		assertEquals(query.getSql(), "SELECT sequencename.CURRENTVAL FROM dual");
		assertTrue(execute(mDaffodil, query) >= 0);
	}

	public void testCloneDaffodil()
	{
		SequenceValue query = new SequenceValue(mDaffodil);
		query
			.name("sequencename")
			.next();
		SequenceValue query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		assertTrue(execute(mDaffodil, query_clone) >= 0);
	}
}
