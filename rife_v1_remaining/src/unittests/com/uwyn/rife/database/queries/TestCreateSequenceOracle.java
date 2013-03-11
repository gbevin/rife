/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCreateSequenceOracle.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;

public class TestCreateSequenceOracle extends TestCreateSequence
{
	public TestCreateSequenceOracle(String name)
	{
		super(name);
	}

	public void testInstantiationOracle()
	{
		CreateSequence query = new CreateSequence(mOracle);
		assertNotNull(query);
		try
		{
			query.getSql();
			fail();
		}
		catch (SequenceNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "CreateSequence");
		}
	}

	public void testClearOracle()
	{
		CreateSequence query = new CreateSequence(mOracle);
		query.name("sequencename");
		assertNotNull(query.getSql());
		query.clear();
		try
		{
			query.getSql();
			fail();
		}
		catch (SequenceNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "CreateSequence");
		}
	}

	public void testCreateOracle()
	{
		CreateSequence query = new CreateSequence(mOracle);
		query.name("sequencename");
		assertEquals(query.getSql(), "CREATE SEQUENCE sequencename");
		execute(mOracle, query);
	}

	public void testCloneOracle()
	{
		CreateSequence query = new CreateSequence(mOracle);
		query.name("sequencename");
		CreateSequence query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(mOracle, query_clone);
	}
}
