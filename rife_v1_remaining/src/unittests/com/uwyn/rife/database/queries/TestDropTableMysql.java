/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropTableMysql.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.TableNameRequiredException;

public class TestDropTableMysql extends TestDropTable
{
	public TestDropTableMysql(String name)
	{
		super(name);
	}

	public void testInstantiationMysql()
	{
		DropTable query = new DropTable(mMysql);
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

	public void testIncompleteQueryMysql()
	{
		DropTable query = new DropTable(mMysql);
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

	public void testClearMysql()
	{
		DropTable query = new DropTable(mMysql);
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

	public void testOneTableMysql()
	{
		DropTable query = new DropTable(mMysql);
		query.table("tabletodrop");
		assertEquals(query.getSql(), "DROP TABLE tabletodrop");
		execute(query);
	}

	public void testMultipleTablesMysql()
	{
		DropTable query = new DropTable(mMysql);
		query.table("tabletodrop1")
			.table("tabletodrop2")
			.table("tabletodrop3");
		assertEquals(query.getSql(), "DROP TABLE tabletodrop1, tabletodrop2, tabletodrop3");
		execute(query);
	}

	public void testCloneMysql()
	{
		DropTable query = new DropTable(mMysql);
		query.table("tabletodrop1")
			.table("tabletodrop2")
			.table("tabletodrop3");
		DropTable query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(query_clone);
	}
}
