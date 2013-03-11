/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropTableFirebird.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;
import com.uwyn.rife.database.exceptions.TableNameRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;



public class TestDropTableFirebird extends TestDropTable
{
	public TestDropTableFirebird(String name)
	{
		super(name);
	}

	public void testInstantiationFirebird()
	{
		DropTable query = new DropTable(mFirebird);
		assertNotNull(query);
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "DropTable");
		}
	}

	public void testIncompleteQueryFirebird()
	{
		DropTable query = new DropTable(mFirebird);
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "DropTable");
		}
		query.table("tablename");
		assertNotNull(query.getSql());
	}

	public void testClearFirebird()
	{
		DropTable query = new DropTable(mFirebird);
		query.table("tablename");
		assertNotNull(query.getSql());
		query.clear();
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "DropTable");
		}
	}

	public void testOneTableFirebird()
	{
		DropTable query = new DropTable(mFirebird);
		query.table("tabletodrop");
		assertEquals(query.getSql(), "DROP TABLE tabletodrop");
		execute(query);
	}

	public void testMultipleTablesFirebird()
	{
		DropTable query = new DropTable(mFirebird);
		query.table("tabletodrop1")
			.table("tabletodrop2")
			.table("tabletodrop3");
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertEquals(e.getFeature(), "MULTIPLE TABLE DROP");
		}
	}

	public void testCloneFirebird()
	{
		DropTable query = new DropTable(mFirebird);
		query.table("tabletodrop1");
		DropTable query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(query_clone);
	}
}
