/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestGenericQueryManagerChild.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.querymanagers.generic.beans.ChildBean;
import junit.framework.TestCase;

public class TestGenericQueryManagerChild extends TestCase
{
    private Datasource 	mDatasource = null;

	private GenericQueryManager<ChildBean> 	mChildManager = null;

	public TestGenericQueryManagerChild(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}

	protected void setUp()
	throws Exception
	{
		mChildManager = GenericQueryManagerFactory.getInstance(mDatasource, ChildBean.class);

		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mChildManager.install();
		}
		finally
		{
			if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
			{
				mDatasource.setPoolsize(poolsize);
			}
		}
 	}

	protected void tearDown()
	throws Exception
	{
		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mChildManager.remove();
		}
		finally
		{
			if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
			{
				mDatasource.setPoolsize(poolsize);
			}
		}
	}

	public void testGetBaseClass()
	{
		assertSame(ChildBean.class, mChildManager.getBaseClass());
	}

	public void testInstallCustomQuery()
	{
		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mChildManager.remove();

			mChildManager.install(mChildManager.getInstallTableQuery());
		}
		finally
		{
			if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
			{
				mDatasource.setPoolsize(poolsize);
			}
		}
	}

	public void testChildBean()
	{
		ChildBean bean = new ChildBean();
		
		bean.setParentString("This is bean");
		bean.setChildString("This is childbean");

		int id = mChildManager.save(bean);

		ChildBean rbean = mChildManager.restore(id);

		assertEquals(rbean.getParentString(), bean.getParentString());
		assertEquals(rbean.getChildString(), bean.getChildString());
	}
}
