/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropTableDerby.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.TableNameRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;

public class TestDropTableDerby extends TestDropTable
{
	public TestDropTableDerby(String name)
	{
		super(name);
	}

	public void testInstantiationDerby()
	{
		DropTable query = new DropTable(mDerby);
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

	public void testIncompleteQueryDerby()
	{
		DropTable query = new DropTable(mDerby);
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

	public void testClearDerby()
	{
		DropTable query = new DropTable(mDerby);
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

	public void testOneTableDerby()
	{
		DropTable query = new DropTable(mDerby);
		query.table("tabletodrop");
		assertEquals(query.getSql(), "DROP TABLE tabletodrop");
		execute(query);
	}

	public void testMultipleTablesDerby()
	{
		DropTable query = new DropTable(mDerby);
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
			assertTrue(true);
		}
	}

	public void testCloneDerby()
	{
		DropTable query = new DropTable(mDerby);
		query.table("tabletodrop1");
//			.table("tabletodrop2")
//			.table("tabletodrop3");
		DropTable query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(query_clone);
	}
}
