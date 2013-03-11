/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropTableDaffodil.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.TableNameRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;

public class TestDropTableDaffodil extends TestDropTable
{
	public TestDropTableDaffodil(String name)
	{
		super(name);
	}

	public void testInstantiationDaffodil()
	{
		DropTable query = new DropTable(mDaffodil);
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

	public void testIncompleteQueryDaffodil()
	{
		DropTable query = new DropTable(mDaffodil);
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

	public void testClearDaffodil()
	{
		DropTable query = new DropTable(mDaffodil);
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

	public void testOneTableDaffodil()
	{
		DropTable query = new DropTable(mDaffodil);
		query.table("tabletodrop");
		assertEquals(query.getSql(), "DROP TABLE tabletodrop");
		execute(query);
	}

	public void testMultipleTablesDaffodil()
	{
		DropTable query = new DropTable(mDaffodil);
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

	public void testCloneDaffodil()
	{
		DropTable query = new DropTable(mDaffodil);
		query.table("tabletodrop1");
//			.table("tabletodrop2")
//			.table("tabletodrop3");
		DropTable query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(query_clone);
	}
}
