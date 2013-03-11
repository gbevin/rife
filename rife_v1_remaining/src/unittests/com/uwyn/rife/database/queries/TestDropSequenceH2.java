/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropSequenceH2.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;

public class TestDropSequenceH2 extends TestDropSequence
{
	public TestDropSequenceH2(String name)
	{
		super(name);
	}

	public void testInstantiationH2()
	{
		DropSequence query = new DropSequence(mH2);
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

	public void testClearH2()
	{
		DropSequence query = new DropSequence(mH2);
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
	
	public void testCreateH2()
	{
		DropSequence query = new DropSequence(mH2);
		query.name("sequencename");
		assertEquals(query.getSql(), "DROP SEQUENCE sequencename");
		execute(mH2, query);
	}
	
	public void testCloneH2()
	{
		DropSequence query = new DropSequence(mH2);
		query.name("sequencename");
		DropSequence query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(mH2, query_clone);
	}
}
