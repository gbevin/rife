/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropSequenceDerby.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;

public class TestDropSequenceDerby extends TestDropSequence
{
	public TestDropSequenceDerby(String name)
	{
		super(name);
	}

	public void testInstantiationDerby()
	{
		DropSequence query = new DropSequence(mDerby);
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

	public void testClearDerby()
	{
		DropSequence query = new DropSequence(mDerby);
		query.name("sequencename");
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
	}
	
	public void testCreateDerby()
	{
		DropSequence query = new DropSequence(mDerby);
		query.name("sequencename");
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
	}
	
	public void testCloneDerby()
	{
		// sequences are not supported on mysql
	}
}
