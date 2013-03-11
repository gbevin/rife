/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCreateSequenceH2.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;

public class TestCreateSequenceH2 extends TestCreateSequence
{
	public TestCreateSequenceH2(String name)
	{
		super(name);
	}

	public void testInstantiationH2()
	{
		CreateSequence query = new CreateSequence(mH2);
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

	public void testClearH2()
	{
		CreateSequence query = new CreateSequence(mH2);
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
	
	public void testCreateH2()
	{
		CreateSequence query = new CreateSequence(mH2);
		query.name("sequencename");
		assertEquals(query.getSql(), "CREATE SEQUENCE sequencename");
		execute(mH2, query);
	}
	
	public void testCloneH2()
	{
		CreateSequence query = new CreateSequence(mH2);
		query.name("sequencename");
		CreateSequence query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(mH2, query_clone);
	}
}
