/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropSequenceMysql.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;

public class TestDropSequenceMysql extends TestDropSequence
{
	public TestDropSequenceMysql(String name)
	{
		super(name);
	}

	public void testInstantiationMysql()
	{
		DropSequence query = new DropSequence(mMysql);
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

	public void testClearMysql()
	{
		DropSequence query = new DropSequence(mMysql);
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
	
	public void testCreateMysql()
	{
		DropSequence query = new DropSequence(mMysql);
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
	
	public void testCloneMysql()
	{
		// sequences are not supported on mysql
	}
}
