/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestGenericQueryManagerDelegate.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbRowProcessor;
import com.uwyn.rife.database.querymanagers.generic.beans.ChildBean;
import com.uwyn.rife.database.querymanagers.generic.beans.ConstrainedBean;
import com.uwyn.rife.database.querymanagers.generic.beans.LinkBean;
import com.uwyn.rife.database.querymanagers.generic.beans.SimpleBean;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import junit.framework.TestCase;

public class TestGenericQueryManagerDelegate extends TestCase
{
    private Datasource mDatasource = null;
	
	class GQMSimpleBean extends GenericQueryManagerDelegate<SimpleBean>
	{
		GQMSimpleBean(Datasource datasource)
		{
			super(datasource, SimpleBean.class);
		}
	}
	
	class GQMLinkBean extends GenericQueryManagerDelegate<LinkBean>
	{
		GQMLinkBean(Datasource datasource)
		{
			super(datasource, LinkBean.class);
		}
	}
	
	class GQMChildBean extends GenericQueryManagerDelegate<ChildBean>
	{
		GQMChildBean(Datasource datasource)
		{
			super(datasource, ChildBean.class);
		}
	}
	
	class GQMConstrainedBean extends GenericQueryManagerDelegate<ConstrainedBean>
	{
		GQMConstrainedBean(Datasource datasource)
		{
			super(datasource, ConstrainedBean.class);
		}
	}
	
	private GQMSimpleBean		mManager = null;
	private GQMLinkBean			mLinkManager = null;
	private GQMChildBean 		mChildManager = null;
	private GQMConstrainedBean 	mConstrainedManager = null;

	public TestGenericQueryManagerDelegate(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}
    
	protected void setUp()
	throws Exception
	{
		mManager = new GQMSimpleBean(mDatasource);
		mLinkManager = new GQMLinkBean(mDatasource);
		mChildManager = new GQMChildBean(mDatasource);
		mConstrainedManager = new GQMConstrainedBean(mDatasource);
		
		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mManager.install();
			mLinkManager.install();
			mChildManager.install();
			mConstrainedManager.install();
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
			mConstrainedManager.remove();
			mManager.remove();
			mLinkManager.remove();
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

	public void testSaveRestore()
	{
		SimpleBean bean = new SimpleBean();
		SimpleBean newbean = null;

		bean.setTestString("This is my test string");

		int id = mManager.save(bean);

		newbean = mManager.restore(id);

		assertTrue(newbean != null);
		assertTrue(newbean != bean);
		assertEquals(newbean.getTestString(), bean.getTestString());
		assertEquals(newbean.getId(), id);

		bean.setId(id);
		bean.setTestString("This is a new test string");

		assertEquals(mManager.save(bean), id);
		assertEquals(bean.getId(), id);

		newbean = mManager.restore(id);

		assertTrue(newbean != null);
		assertTrue(newbean != bean);

		assertEquals(newbean.getTestString(), "This is a new test string");

		bean.setId(999999);
		bean.setTestString("This is another test string");

		assertFalse(999999 == mManager.save(bean));
		assertEquals(bean.getId(), id+1);
	}

	public void testSaveRestoreConstrained()
	{
		ConstrainedBean bean = new ConstrainedBean();
		ConstrainedBean newbean = null;

		bean.setTestString("This is my test string");

		int id = mConstrainedManager.save(bean);

		newbean = mConstrainedManager.restore(id);

		assertTrue(newbean != null);
		assertTrue(newbean != bean);
		assertEquals(newbean.getTestString(), bean.getTestString());
		assertEquals(newbean.getIdentifier(), id);

		bean.setIdentifier(id);
		bean.setTestString("This is a new test string");

		assertEquals(mConstrainedManager.save(bean), id);
		assertEquals(bean.getIdentifier(), id);

		newbean = mConstrainedManager.restore(id);

		assertTrue(newbean != null);
		assertTrue(newbean != bean);

		assertEquals(newbean.getTestString(), "This is a new test string");

		bean.setIdentifier(999999);
		bean.setTestString("This is another test string");

		assertFalse(999999 == mConstrainedManager.save(bean));
		assertEquals(bean.getIdentifier(), id+1);
	}

	public void testDelete()
	{
		SimpleBean bean = new SimpleBean();

		bean.setTestString("This is my test string");

		int id1 = mManager.save(bean);
		assertTrue(mManager.restore(id1) != null);
		mManager.delete(id1);
		assertTrue(mManager.restoreFirst(mManager.getRestoreQuery(id1)) == null);

		int id2 = mManager.save(bean);
		assertTrue(mManager.restoreFirst(mManager.getRestoreQuery(id2)) != null);
		mManager.delete(mManager.getDeleteQuery(id2));
		assertTrue(mManager.restore(id2) == null);
	}

	public void testRestore()
	{
		SimpleBean bean1 = new SimpleBean();
		SimpleBean bean2 = new SimpleBean();
		SimpleBean bean3 = new SimpleBean();

		bean1.setTestString("This is bean1");
		bean2.setTestString("This is bean2");
		bean3.setTestString("This is bean3");

		mManager.save(bean1);
		mManager.save(bean2);
		mManager.save(bean3);

		List<SimpleBean> list = mManager.restore();

		assertEquals(list.size(), 3);

		for (SimpleBean bean : list)
		{
			assertTrue(bean != null);
			assertTrue(bean != bean1 || bean != bean2 || bean != bean3);
			assertTrue(
				bean.getTestString().equals("This is bean1") ||
				bean.getTestString().equals("This is bean2") ||
				bean.getTestString().equals("This is bean3") );
		}
	}

	public void testRestoreRowProcessor()
	{
		SimpleBean bean1 = new SimpleBean();
		SimpleBean bean2 = new SimpleBean();
		SimpleBean bean3 = new SimpleBean();

		bean1.setTestString("This is bean1");
		bean2.setTestString("This is bean2");
		bean3.setTestString("This is bean3");

		mManager.save(bean1);
		mManager.save(bean2);
		mManager.save(bean3);

		final int[] count = new int[] {0};
		mManager.restore(new DbRowProcessor() {
				public boolean processRow(ResultSet resultSet)
				throws SQLException
				{
					count[0]++;

					String test_string = resultSet.getString("testString");
					assertTrue(
						test_string.equals("This is bean1") ||
						test_string.equals("This is bean2") ||
						test_string.equals("This is bean3") );

					return true;
				}
			});

		assertEquals(count[0], 3);
	}

	public void testRestoreQueryRowProcessor()
	{
		SimpleBean bean1 = new SimpleBean();
		SimpleBean bean2 = new SimpleBean();
		SimpleBean bean3 = new SimpleBean();

		bean1.setTestString("This is bean1");
		bean2.setTestString("This is bean2");
		bean3.setTestString("This is bean3");

		mManager.save(bean1);
		mManager.save(bean2);
		mManager.save(bean3);

		final int[] count = new int[] {0};
		mManager.restore(mManager.getRestoreQuery().where("testString", "LIKE", "%bean2"), new DbRowProcessor() {
				public boolean processRow(ResultSet resultSet)
				throws SQLException
				{
					count[0]++;

					String test_string = resultSet.getString("testString");
					assertTrue(test_string.equals("This is bean2"));

					return true;
				}
			});

		assertEquals(count[0], 1);
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

	public void testCount()
	{
		SimpleBean bean1 = new SimpleBean();
		SimpleBean bean2 = new SimpleBean();
		SimpleBean bean3 = new SimpleBean();
		SimpleBean bean4 = new SimpleBean();
		SimpleBean bean5 = new SimpleBean();

		bean1.setTestString("bean set 1");
		bean2.setTestString("bean set 1");
		bean3.setTestString("bean set 1");
		bean4.setTestString("bean set 2");
		bean5.setTestString("bean set 2");

		mManager.save(bean1);
		mManager.save(bean2);
		mManager.save(bean3);
		mManager.save(bean4);
		mManager.save(bean5);

		assertEquals(mManager.count(), 5);

		mManager.delete(bean1.getId());
		mManager.delete(bean2.getId());
		mManager.delete(bean3.getId());

		assertEquals(mManager.count(), 2);
	}
}

