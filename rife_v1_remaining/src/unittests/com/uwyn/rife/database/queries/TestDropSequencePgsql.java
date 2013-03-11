/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropSequencePgsql.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;

public class TestDropSequencePgsql extends TestDropSequence
{
	public TestDropSequencePgsql(String name)
	{
		super(name);
	}

	public void testInstantiationPgsql()
	{
		DropSequence query = new DropSequence(mPgsql);
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

	public void testClearPgsql()
	{
		DropSequence query = new DropSequence(mPgsql);
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
	
	public void testCreatePgsql()
	{
		DropSequence query = new DropSequence(mPgsql);
		query.name("sequencename");
		assertEquals(query.getSql(), "DROP SEQUENCE sequencename");
		execute(mPgsql, query);
	}
	
	public void testClonePgsql()
	{
		DropSequence query = new DropSequence(mPgsql);
		query.name("sequencename");
		DropSequence query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(mPgsql, query_clone);
	}
}
