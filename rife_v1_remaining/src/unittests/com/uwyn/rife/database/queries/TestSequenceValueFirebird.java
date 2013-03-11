/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSequenceValueFirebird.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;
import com.uwyn.rife.database.exceptions.SequenceOperationRequiredException;

public class TestSequenceValueFirebird extends TestSequenceValue
{
	public TestSequenceValueFirebird(String name)
	{
		super(name);
	}

	public void testInstantiationFirebird()
	{
		SequenceValue query = new SequenceValue(mFirebird);
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

	public void testInvalidFirebird()
	{
		SequenceValue query = new SequenceValue(mFirebird);
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

	public void testClearFirebird()
	{
		SequenceValue query = new SequenceValue(mFirebird);
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

	public void testNextFirebird()
	{
		SequenceValue query = new SequenceValue(mFirebird);
		query
			.name("sequencename")
			.next();
		assertEquals(query.getSql(), "SELECT GEN_ID(sequencename, 1) FROM RDB$DATABASE");
		assertTrue(execute(mFirebird, query) >= 0);
	}

	public void testCurrentFirebird()
	{
		SequenceValue query = new SequenceValue(mFirebird);
		query
			.name("sequencename")
			.current();
		assertEquals(query.getSql(), "SELECT GEN_ID(sequencename, 0) FROM RDB$DATABASE");
		assertTrue(execute(mFirebird, query) >= 0);
	}

	public void testCloneFirebird()
	{
		SequenceValue query = new SequenceValue(mFirebird);
		query
			.name("sequencename")
			.next();
		SequenceValue query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		assertTrue(execute(mFirebird, query_clone) >= 0);
	}
}
