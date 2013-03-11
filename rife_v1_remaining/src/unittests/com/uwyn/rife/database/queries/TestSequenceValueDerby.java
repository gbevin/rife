/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSequenceValueDerby.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;
import com.uwyn.rife.database.exceptions.SequenceOperationRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;

public class TestSequenceValueDerby extends TestSequenceValue
{
	public TestSequenceValueDerby(String name)
	{
		super(name);
	}

	public void testInstantiationDerby()
	{
		SequenceValue query = new SequenceValue(mDerby);
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

	public void testInvalidDerby()
	{
		SequenceValue query = new SequenceValue(mDerby);
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

	public void testClearDerby()
	{
		SequenceValue query = new SequenceValue(mDerby);
		query
			.name("sequencename")
			.next();
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
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

	public void testNextDerby()
	{
		SequenceValue query = new SequenceValue(mDerby);
		query
			.name("sequencename")
			.next();
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

	public void testCurrentDerby()
	{
		SequenceValue query = new SequenceValue(mDerby);
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

	public void testCloneDerby()
	{
		SequenceValue query = new SequenceValue(mDerby);
		query
			.name("sequencename")
			.next();
		SequenceValue query_clone = query.clone();
		try
		{
			query_clone.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
	}
}
