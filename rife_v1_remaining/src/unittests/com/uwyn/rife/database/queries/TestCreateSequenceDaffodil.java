/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCreateSequenceDaffodil.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;

public class TestCreateSequenceDaffodil extends TestCreateSequence
{
	public TestCreateSequenceDaffodil(String name)
	{
		super(name);
	}

	public void testInstantiationDaffodil()
	{
		CreateSequence query = new CreateSequence(mDaffodil);
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

	public void testClearDaffodil()
	{
		CreateSequence query = new CreateSequence(mDaffodil);
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

	public void testCreateDaffodil()
	{
		CreateSequence query = new CreateSequence(mDaffodil);
		query.name("sequencename");
		assertEquals(query.getSql(), "CREATE SEQUENCE sequencename");
		execute(mDaffodil, query);
	}

	public void testCloneDaffodil()
	{
		CreateSequence query = new CreateSequence(mDaffodil);
		query.name("sequencename");
		CreateSequence query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(mDaffodil, query_clone);
	}
}
