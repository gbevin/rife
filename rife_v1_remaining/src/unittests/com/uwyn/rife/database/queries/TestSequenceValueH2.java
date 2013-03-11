/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSequenceValueH2.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;
import com.uwyn.rife.database.exceptions.SequenceOperationRequiredException;

public class TestSequenceValueH2 extends TestSequenceValue
{
	public TestSequenceValueH2(String name)
	{
		super(name);
	}

	public void testInstantiationH2()
	{
		SequenceValue query = new SequenceValue(mH2);
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

	public void testInvalidH2()
	{
		SequenceValue query = new SequenceValue(mH2);
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

	public void testClearH2()
	{
		SequenceValue query = new SequenceValue(mH2);
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

	public void testNextH2()
	{
		SequenceValue query = new SequenceValue(mH2);
		query
			.name("sequencename")
			.next();
		assertEquals(query.getSql(), "SELECT nextval('sequencename')");
		assertTrue(execute(mH2, query) >= 0);
	}

	public void testCurrentH2()
	{
		SequenceValue query = new SequenceValue(mH2);
		query
			.name("sequencename")
			.current();
		assertEquals(query.getSql(), "SELECT currval('sequencename')");
		assertTrue(execute(mH2, query) >= 0);
	}

	public void testCloneH2()
	{
		SequenceValue query = new SequenceValue(mH2);
		query
			.name("sequencename")
			.next();
		SequenceValue query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		assertTrue(execute(mH2, query_clone) >= 0);
	}
}
