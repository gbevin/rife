/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropSequenceOracle.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;

public class TestDropSequenceOracle extends TestDropSequence
{
	public TestDropSequenceOracle(String name)
	{
		super(name);
	}

	public void testInstantiationOracle()
	{
		DropSequence query = new DropSequence(mOracle);
		assertNotNull(query);
		try
		{
			query.getSql();
			fail();
		}
		catch (SequenceNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "DropSequence");
		}
	}

	public void testClearOracle()
	{
		DropSequence query = new DropSequence(mOracle);
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
			assertEquals(e.getQueryName(), "DropSequence");
		}
	}
	
	public void testCreateOracle()
	{
		DropSequence query = new DropSequence(mOracle);
		query.name("sequencename");
		assertEquals(query.getSql(), "DROP SEQUENCE sequencename");
		execute(mOracle, query);
	}
	
	public void testCloneOracle()
	{
		DropSequence query = new DropSequence(mOracle);
		query.name("sequencename");
		DropSequence query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(mOracle, query_clone);
	}
}
