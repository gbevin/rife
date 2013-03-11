/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropSequenceDaffodil.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;

public class TestDropSequenceDaffodil extends TestDropSequence
{
	public TestDropSequenceDaffodil(String name)
	{
		super(name);
	}

	public void testInstantiationDaffodil()
	{
		DropSequence query = new DropSequence(mDaffodil);
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

	public void testClearDaffodil()
	{
		DropSequence query = new DropSequence(mDaffodil);
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
	
	public void testCreateDaffodil()
	{
		DropSequence query = new DropSequence(mDaffodil);
		query.name("sequencename");
		assertEquals(query.getSql(), "DROP SEQUENCE sequencename");
		execute(mDaffodil, query);
	}
	
	public void testCloneDaffodil()
	{
		DropSequence query = new DropSequence(mDaffodil);
		query.name("sequencename");
		DropSequence query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(mDaffodil, query_clone);
	}
}
