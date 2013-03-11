/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropSequenceMckoi.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;

public class TestDropSequenceMckoi extends TestDropSequence
{
	public TestDropSequenceMckoi(String name)
	{
		super(name);
	}

	public void testInstantiationMckoi()
	{
		DropSequence query = new DropSequence(mMckoi);
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

	public void testClearMckoi()
	{
		DropSequence query = new DropSequence(mMckoi);
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
	
	public void testCreateMckoi()
	{
		DropSequence query = new DropSequence(mMckoi);
		query.name("sequencename");
		assertEquals(query.getSql(), "DROP SEQUENCE sequencename");
		execute(mMckoi, query);
	}
	
	public void testCloneMckoi()
	{
		DropSequence query = new DropSequence(mMckoi);
		query.name("sequencename");
		DropSequence query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(mMckoi, query_clone);
	}
}
