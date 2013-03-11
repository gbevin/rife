/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDbStatement.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.tools.ExceptionUtils;
import junit.framework.TestCase;

public class TestDbStatement extends TestCase
{
	private Datasource		mDatasource = null;
	private DbConnection	mConnection = null;

	public TestDbStatement(Datasource datasource, String datasourceName, String name)
	{
		super(name);
		mDatasource = datasource;
	}

	public void setUp()
	{
		try
		{
			mConnection = mDatasource.getConnection();
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void tearDown()
	{
		try
		{
			mConnection.close();
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecute()
	{
		// FIXME : write unittests
	}
}
