/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSequenceValueOracle.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;
import com.uwyn.rife.database.exceptions.SequenceOperationRequiredException;

public class TestSequenceValueOracle extends TestSequenceValue
{
	public TestSequenceValueOracle(String name)
	{
		super(name);
	}

	public void testInstantiationOracle()
	{
		SequenceValue query = new SequenceValue(mOracle);
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

	public void testInvalidOracle()
	{
		SequenceValue query = new SequenceValue(mOracle);
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

	public void testClearOracle()
	{
		SequenceValue query = new SequenceValue(mOracle);
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

	public void testNextOracle()
	{
		SequenceValue query = new SequenceValue(mOracle);
		query
			.name("sequencename")
			.next();
		assertEquals(query.getSql(), "SELECT sequencename.nextval FROM DUAL");
		assertTrue(execute(mOracle, query) >= 0);
	}

	public void testCurrentOracle()
	{
		SequenceValue query = new SequenceValue(mOracle);
		query
			.name("sequencename")
			.current();
		assertEquals(query.getSql(), "SELECT sequencename.currval FROM DUAL");
		assertTrue(execute(mOracle, query) >= 0);
	}

	public void testCloneOracle()
	{
		SequenceValue query = new SequenceValue(mOracle);
		query
			.name("sequencename")
			.next();
		SequenceValue query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		assertTrue(execute(mOracle, query_clone) >= 0);
	}
}
