/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSequenceValueHsqldb.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;
import com.uwyn.rife.database.exceptions.SequenceOperationRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;

public class TestSequenceValueHsqldb extends TestSequenceValue
{
	public TestSequenceValueHsqldb(String name)
	{
		super(name);
	}

	public void testInstantiationHsqldb()
	{
		SequenceValue query = new SequenceValue(mHsqldb);
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

	public void testInvalidHsqldb()
	{
		SequenceValue query = new SequenceValue(mHsqldb);
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

	public void testClearHsqldb()
	{
		SequenceValue query = new SequenceValue(mHsqldb);
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

	public void testNextHsqldb()
	{
		SequenceValue query = new SequenceValue(mHsqldb);
		query
			.name("sequencename")
			.next();
		assertEquals(query.getSql(), "CALL NEXT VALUE FOR sequencename");
		assertTrue(execute(mHsqldb, query) >= 0);
	}

	public void testCurrentHsqldb()
	{
		SequenceValue query = new SequenceValue(mHsqldb);
		query
			.name("sequencename")
			.current();
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
	}

	public void testCloneHsqldb()
	{
		SequenceValue query = new SequenceValue(mHsqldb);
		query
			.name("sequencename")
			.next();
		SequenceValue query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		assertTrue(execute(mHsqldb, query_clone) >= 0);
	}
}
