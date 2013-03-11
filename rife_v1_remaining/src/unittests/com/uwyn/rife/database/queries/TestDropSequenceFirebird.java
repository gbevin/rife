/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropSequenceFirebird.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;

public class TestDropSequenceFirebird extends TestDropSequence
{
	public TestDropSequenceFirebird(String name)
	{
		super(name);
	}

	public void testInstantiationFirebird()
	{
		DropSequence query = new DropSequence(mFirebird);
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

	public void testClearFirebird()
	{
		DropSequence query = new DropSequence(mFirebird);
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
	
	public void testCreateFirebird()
	{
		DropSequence query = new DropSequence(mFirebird);
		query.name("sequencename");
		assertEquals(query.getSql(), "DROP GENERATOR sequencename");
		execute(mFirebird, query);
	}
	
	public void testCloneFirebird()
	{
		DropSequence query = new DropSequence(mFirebird);
		query.name("sequencename");
		DropSequence query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(mFirebird, query_clone);
	}
}
