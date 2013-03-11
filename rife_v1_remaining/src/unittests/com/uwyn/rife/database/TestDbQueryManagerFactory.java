/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDbQueryManagerFactory.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.tools.ExceptionUtils;
import junit.framework.TestCase;

public class TestDbQueryManagerFactory extends TestCase
{
    private Datasource  mDatasource = null;
    
	public TestDbQueryManagerFactory(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}

	public void testGetDefault()
	{
		TestDbQueryManagerImpl	dbquerymanager1 = TestDbQueryManagerFactoryImpl.getInstance(mDatasource);
		TestDbQueryManagerImpl	dbquerymanager2 = TestDbQueryManagerFactoryImpl.getInstance(mDatasource);

		assertSame(dbquerymanager1.getDatasource(), mDatasource);
		assertSame(dbquerymanager2.getDatasource(), mDatasource);

		assertTrue(dbquerymanager1 == dbquerymanager2);
	}

	public void testGetIdentifier()
	{
		TestDbQueryManagerImpl	dbquerymanager1 = TestDbQueryManagerFactoryImpl.getInstance(mDatasource, "id1");
		TestDbQueryManagerImpl	dbquerymanager2 = TestDbQueryManagerFactoryImpl.getInstance(mDatasource, "id1");
		TestDbQueryManagerImpl	dbquerymanager3 = TestDbQueryManagerFactoryImpl.getInstance(mDatasource, "id2");
		TestDbQueryManagerImpl	dbquerymanager4 = TestDbQueryManagerFactoryImpl.getInstance(mDatasource, "id3");

		assertSame(dbquerymanager1.getDatasource(), mDatasource);
		assertSame(dbquerymanager2.getDatasource(), mDatasource);
		assertSame(dbquerymanager3.getDatasource(), mDatasource);
		assertSame(dbquerymanager4.getDatasource(), mDatasource);

		assertTrue(dbquerymanager1 == dbquerymanager2);
		assertTrue(dbquerymanager1 != dbquerymanager3);
		assertTrue(dbquerymanager1 != dbquerymanager4);
		assertTrue(dbquerymanager2 != dbquerymanager3);
		assertTrue(dbquerymanager2 != dbquerymanager4);
		assertTrue(dbquerymanager3 != dbquerymanager4);

		dbquerymanager1.setSetting("setting1");
		dbquerymanager3.setSetting("setting2");
		dbquerymanager4.setSetting("setting3");

		assertTrue(dbquerymanager1.getSetting().equals(dbquerymanager2.getSetting()));
		assertTrue(!dbquerymanager1.getSetting().equals(dbquerymanager3.getSetting()));
		assertTrue(!dbquerymanager1.getSetting().equals(dbquerymanager4.getSetting()));
		assertTrue(!dbquerymanager2.getSetting().equals(dbquerymanager3.getSetting()));
		assertTrue(!dbquerymanager2.getSetting().equals(dbquerymanager4.getSetting()));
		assertTrue(!dbquerymanager3.getSetting().equals(dbquerymanager4.getSetting()));
	}

	public void testQuery()
	{
		TestDbQueryManagerImpl	dbquerymanager = TestDbQueryManagerFactoryImpl.getInstance(mDatasource);
		try
		{
			dbquerymanager.install();
			assertEquals(0, dbquerymanager.count());
			dbquerymanager.store(1, "one");
			assertEquals(1, dbquerymanager.count());
			dbquerymanager.store(2, "two");
			assertEquals(2, dbquerymanager.count());
			dbquerymanager.store(3, "three");
			assertEquals(3, dbquerymanager.count());
			dbquerymanager.store(4, "four");
			assertEquals(4, dbquerymanager.count());
			try
			{
				dbquerymanager.store(4, "fourb");
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
			assertEquals(4, dbquerymanager.count());
			dbquerymanager.remove();
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
}
