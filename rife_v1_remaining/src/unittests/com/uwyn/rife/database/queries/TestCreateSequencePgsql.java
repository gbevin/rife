/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCreateSequencePgsql.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;

public class TestCreateSequencePgsql extends TestCreateSequence
{
	public TestCreateSequencePgsql(String name)
	{
		super(name);
	}

	public void testInstantiationPgsql()
	{
		CreateSequence query = new CreateSequence(mPgsql);
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

	public void testClearPgsql()
	{
		CreateSequence query = new CreateSequence(mPgsql);
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
	
	public void testCreatePgsql()
	{
		CreateSequence query = new CreateSequence(mPgsql);
		query.name("sequencename");
		assertEquals(query.getSql(), "CREATE SEQUENCE sequencename");
		execute(mPgsql, query);
	}
	
	public void testClonePgsql()
	{
		CreateSequence query = new CreateSequence(mPgsql);
		query.name("sequencename");
		CreateSequence query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(mPgsql, query_clone);
	}
}
