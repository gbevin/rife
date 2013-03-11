/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropSequenceHsqldb.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;

public class TestDropSequenceHsqldb extends TestDropSequence
{
	public TestDropSequenceHsqldb(String name)
	{
		super(name);
	}

	public void testInstantiationHsqldb()
	{
		DropSequence query = new DropSequence(mHsqldb);
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

	public void testClearHsqldb()
	{
		DropSequence query = new DropSequence(mHsqldb);
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
	
	public void testCreateHsqldb()
	{
		DropSequence query = new DropSequence(mHsqldb);
		query.name("sequencename");
		assertEquals(query.getSql(), "DROP SEQUENCE sequencename");
		execute(mHsqldb, query);
	}
	
	public void testCloneHsqldb()
	{
		DropSequence query = new DropSequence(mHsqldb);
		query.name("sequencename");
		DropSequence query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(mHsqldb, query_clone);
	}
}
