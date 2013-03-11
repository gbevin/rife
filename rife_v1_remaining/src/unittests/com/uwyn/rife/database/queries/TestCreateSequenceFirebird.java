/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCreateSequenceFirebird.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;

public class TestCreateSequenceFirebird extends TestCreateSequence
{
	public TestCreateSequenceFirebird(String name)
	{
		super(name);
	}

	public void testInstantiationFirebird()
	{
		CreateSequence query = new CreateSequence(mFirebird);
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

	public void testClearFirebird()
	{
		CreateSequence query = new CreateSequence(mFirebird);
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

	public void testCreateFirebird()
	{
		CreateSequence query = new CreateSequence(mFirebird);
		query.name("sequencename");
		assertEquals(query.getSql(), "CREATE GENERATOR sequencename");
		execute(mFirebird, query);
	}

	public void testCloneFirebird()
	{
		CreateSequence query = new CreateSequence(mFirebird);
		query.name("sequencename");
		CreateSequence query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(mFirebird, query_clone);
	}
}
