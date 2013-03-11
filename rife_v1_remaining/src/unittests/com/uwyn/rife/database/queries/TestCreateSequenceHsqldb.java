/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCreateSequenceHsqldb.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;

public class TestCreateSequenceHsqldb extends TestCreateSequence
{
	public TestCreateSequenceHsqldb(String name)
	{
		super(name);
	}

	public void testInstantiationHsqldb()
	{
		CreateSequence query = new CreateSequence(mHsqldb);
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

	public void testClearHsqldb()
	{
		CreateSequence query = new CreateSequence(mHsqldb);
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

	public void testCreateHsqldb()
	{
		CreateSequence query = new CreateSequence(mHsqldb);
		query.name("sequencename");
		assertEquals(query.getSql(), "CREATE SEQUENCE sequencename");
		execute(mHsqldb, query);
	}

	public void testCloneHsqldb()
	{
		CreateSequence query = new CreateSequence(mHsqldb);
		query.name("sequencename");
		CreateSequence query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(mHsqldb, query_clone);
	}
}
