/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestGenericQueryManagerBinary.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.querymanagers.generic.beans.BinaryBean;
import java.util.Arrays;
import junit.framework.TestCase;

public class TestGenericQueryManagerBinary extends TestCase
{
    private Datasource 	mDatasource = null;

	private GenericQueryManager<BinaryBean>	mBinaryManager = null;

	public TestGenericQueryManagerBinary(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}

	protected void setUp()
	throws Exception
	{
		mBinaryManager = GenericQueryManagerFactory.getInstance(mDatasource, BinaryBean.class);

		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mBinaryManager.install();
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
			mBinaryManager.remove();
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
		assertSame(BinaryBean.class, mBinaryManager.getBaseClass());
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
			mBinaryManager.remove();

			mBinaryManager.install(mBinaryManager.getInstallTableQuery());
		}
		finally
		{
			if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
			{
				mDatasource.setPoolsize(poolsize);
			}
		}
	}

	public void testSaveRestoreBinary()
	{
		BinaryBean bean = new BinaryBean();
		BinaryBean newbean = null;

		byte[] bytes1 = new byte[] {1, 3, 5, 7, 11, 13, 17, 19, 23};
		bean.setTheBytes(bytes1);

		int id = mBinaryManager.save(bean);

		newbean = mBinaryManager.restore(id);

		assertTrue(newbean != null);
		assertTrue(newbean != bean);
		assertTrue(Arrays.equals(newbean.getTheBytes(), bean.getTheBytes()));
		assertEquals(newbean.getId(), id);

		byte[] bytes2 = new byte[] {10, 30, 50, 70, 110};
		bean.setId(id);
		bean.setTheBytes(bytes2);

		assertEquals(mBinaryManager.save(bean), id);
		assertEquals(bean.getId(), id);

		newbean = mBinaryManager.restore(id);

		assertTrue(newbean != null);
		assertTrue(newbean != bean);

		assertTrue(Arrays.equals(newbean.getTheBytes(), bytes2));

		byte[] bytes3 = new byte[] {89, 22, 9, 31, 89};
		bean.setId(999999);
		bean.setTheBytes(bytes3);

		assertFalse(999999 == mBinaryManager.save(bean));
		assertEquals(bean.getId(), id+1);

		GenericQueryManager<BinaryBean> manager_othertable = GenericQueryManagerFactory.getInstance(mDatasource, BinaryBean.class, "othertable");
		manager_othertable.install();

		byte[] bytes4 = new byte[] {79, 15, 88, 42};
		BinaryBean bean2 = new BinaryBean();
		bean2.setTheBytes(bytes4);

		manager_othertable.save(bean2);

		BinaryBean bean3 = manager_othertable.restore(bean2.getId());

		assertTrue(Arrays.equals(bean3.getTheBytes(), bean2.getTheBytes()));

		manager_othertable.remove();
	}
}
