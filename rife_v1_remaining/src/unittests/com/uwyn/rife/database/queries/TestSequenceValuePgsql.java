/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSequenceValuePgsql.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;
import com.uwyn.rife.database.exceptions.SequenceOperationRequiredException;

public class TestSequenceValuePgsql extends TestSequenceValue
{
	public TestSequenceValuePgsql(String name)
	{
		super(name);
	}

	public void testInstantiationPgsql()
	{
		SequenceValue query = new SequenceValue(mPgsql);
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

	public void testInvalidPgsql()
	{
		SequenceValue query = new SequenceValue(mPgsql);
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

	public void testClearPgsql()
	{
		SequenceValue query = new SequenceValue(mPgsql);
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

	public void testNextPgsql()
	{
		SequenceValue query = new SequenceValue(mPgsql);
		query
			.name("sequencename")
			.next();
		assertEquals(query.getSql(), "SELECT nextval('sequencename')");
		assertTrue(execute(mPgsql, query) >= 0);
	}

	public void testCurrentPgsql()
	{
		SequenceValue query = new SequenceValue(mPgsql);
		query
			.name("sequencename")
			.current();
		assertEquals(query.getSql(), "SELECT currval('sequencename')");
		assertTrue(execute(mPgsql, query) >= 0);
	}

	public void testClonePgsql()
	{
		SequenceValue query = new SequenceValue(mPgsql);
		query
			.name("sequencename")
			.next();
		SequenceValue query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		assertTrue(execute(mPgsql, query_clone) >= 0);
	}
}
