/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestGenericQueryManagerSimple.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbRowProcessor;
import com.uwyn.rife.database.SomeEnum;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.querymanagers.generic.beans.NoDefaultConstructorBean;
import com.uwyn.rife.database.querymanagers.generic.beans.SimpleBean;
import com.uwyn.rife.database.querymanagers.generic.beans.SparseBean;
import com.uwyn.rife.database.querymanagers.generic.exceptions.MissingDefaultConstructorException;

public class TestGenericQueryManagerSimple extends TestCase
{
    private Datasource 	mDatasource = null;

	private GenericQueryManager<SimpleBean>	mSimpleManager = null;

	public TestGenericQueryManagerSimple(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}

	protected void setUp()
	throws Exception
	{
		mSimpleManager = GenericQueryManagerFactory.getInstance(mDatasource, SimpleBean.class);

		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mSimpleManager.install();
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
			mSimpleManager.remove();
		}
		finally
		{
			if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
			{
				mDatasource.setPoolsize(poolsize);
			}
		}
	}

	public void testNoDefaultConstructor()
	{
		try
		{
			GenericQueryManagerFactory.getInstance(mDatasource, NoDefaultConstructorBean.class);
			fail("MissingDefaultConstructorException exception wasn't thrown");
		}
		catch (MissingDefaultConstructorException e)
		{
			assertSame(e.getBeanClass(), NoDefaultConstructorBean.class);
		}
	}

	public void testGetBaseClass()
	{
		assertSame(SimpleBean.class, mSimpleManager.getBaseClass());
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
			mSimpleManager.remove();

			mSimpleManager.install(mSimpleManager.getInstallTableQuery());
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

		UUID uuid1 = UUID.randomUUID();
		bean.setTestString("This is my test string");
		bean.setUuid(uuid1);
		bean.setEnum(SomeEnum.VALUE_TWO);

		int id = mSimpleManager.save(bean);

		newbean = mSimpleManager.restore(id);

		assertTrue(newbean != null);
		assertTrue(newbean != bean);
		assertEquals(newbean.getTestString(), bean.getTestString());
		assertEquals(newbean.getId(), id);
		assertEquals(newbean.getUuid(), uuid1);
		assertEquals(newbean.getEnum(), SomeEnum.VALUE_TWO);

		UUID uuid2 = UUID.randomUUID();
		bean.setId(id);
		bean.setTestString("This is a new test string");
		bean.setUuid(uuid2);
		bean.setEnum(SomeEnum.VALUE_THREE);

		assertEquals(mSimpleManager.save(bean), id);
		assertEquals(bean.getId(), id);

		newbean = mSimpleManager.restore(id);

		assertTrue(newbean != null);
		assertTrue(newbean != bean);

		assertEquals(newbean.getTestString(), "This is a new test string");
		assertEquals(newbean.getUuid(), uuid2);
		assertEquals(newbean.getEnum(), SomeEnum.VALUE_THREE);

		bean.setId(999999);
		bean.setTestString("This is another test string");

		assertFalse(999999 == mSimpleManager.save(bean));
		assertEquals(bean.getId(), id+1);

		GenericQueryManager<SimpleBean> manager_othertable = GenericQueryManagerFactory.getInstance(mDatasource, SimpleBean.class, "othertable");
		manager_othertable.install();

		SimpleBean bean2 = new SimpleBean();
		bean2.setTestString("test");

		manager_othertable.save(bean2);

		SimpleBean bean3 = manager_othertable.restore(bean2.getId());

		assertEquals(bean3.getTestString(), bean2.getTestString());

		manager_othertable.remove();
	}

	public void testSparseIdentifier()
	{
		GenericQueryManager<SparseBean> manager_sparsebean = GenericQueryManagerFactory.getInstance(mDatasource, SparseBean.class);
		SparseBean sparse_bean = new SparseBean();

		manager_sparsebean.install();

		sparse_bean.setId(1000);
		sparse_bean.setTestString("Test String");
		assertTrue(1000 == manager_sparsebean.save(sparse_bean));

		SparseBean restored_sparsebean = manager_sparsebean.restore(1000);
		assertEquals(restored_sparsebean.getId(), 1000);
		assertEquals(restored_sparsebean.getTestString(), "Test String");

		try
		{
			manager_sparsebean.insert(sparse_bean);
			assertFalse(true);
		}
		catch (DatabaseException e)
		{
			assertTrue(true);
		}
		assertTrue(1000 == manager_sparsebean.update(sparse_bean));

		sparse_bean.setId(1001);
		assertTrue(-1 == manager_sparsebean.update(sparse_bean)); 	// not there; update should fail

		manager_sparsebean.remove();
	}

	public void testDelete()
	{
		SimpleBean bean = new SimpleBean();

		bean.setTestString("This is my test string");

		int id1 = mSimpleManager.save(bean);
		assertTrue(mSimpleManager.restore(id1) != null);
		mSimpleManager.delete(id1);
		assertTrue(mSimpleManager.restoreFirst(mSimpleManager.getRestoreQuery(id1)) == null);

		int id2 = mSimpleManager.save(bean);
		assertTrue(mSimpleManager.restoreFirst(mSimpleManager.getRestoreQuery(id2)) != null);
		mSimpleManager.delete(mSimpleManager.getDeleteQuery(id2));
		assertTrue(mSimpleManager.restore(id2) == null);
	}

	public void testRestore()
	{
		SimpleBean bean1 = new SimpleBean();
		SimpleBean bean2 = new SimpleBean();
		SimpleBean bean3 = new SimpleBean();

		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();
		UUID uuid3 = UUID.randomUUID();

		bean1.setTestString("This is bean1");
		bean1.setUuid(uuid1);
		bean1.setEnum(SomeEnum.VALUE_ONE);
		bean2.setTestString("This is bean2");
		bean2.setUuid(uuid2);
		bean2.setEnum(SomeEnum.VALUE_TWO);
		bean3.setTestString("This is bean3");
		bean3.setUuid(uuid3);
		bean3.setEnum(SomeEnum.VALUE_THREE);

		mSimpleManager.save(bean1);
		mSimpleManager.save(bean2);
		mSimpleManager.save(bean3);

		List<SimpleBean> list = mSimpleManager.restore();

		assertEquals(list.size(), 3);

		for (SimpleBean bean : list)
		{
			assertTrue(bean != null);
			assertTrue(bean != bean1 || bean != bean2 || bean != bean3);
			assertTrue(
				bean.getTestString().equals("This is bean1") &&
				bean.getUuid().equals(uuid1) &&
				bean.getEnum().equals(SomeEnum.VALUE_ONE) ||

				bean.getTestString().equals("This is bean2") &&
				bean.getUuid().equals(uuid2) &&
				bean.getEnum().equals(SomeEnum.VALUE_TWO) ||

				bean.getTestString().equals("This is bean3") &&
				bean.getUuid().equals(uuid3) &&
				bean.getEnum().equals(SomeEnum.VALUE_THREE));
		}
	}

	public void testRestoreRowProcessor()
	{
		SimpleBean bean1 = new SimpleBean();
		SimpleBean bean2 = new SimpleBean();
		SimpleBean bean3 = new SimpleBean();

		final UUID uuid1 = UUID.randomUUID();
		final UUID uuid2 = UUID.randomUUID();
		final UUID uuid3 = UUID.randomUUID();

		bean1.setTestString("This is bean1");
		bean1.setUuid(uuid1);
		bean1.setEnum(SomeEnum.VALUE_ONE);
		bean2.setTestString("This is bean2");
		bean2.setUuid(uuid2);
		bean2.setEnum(SomeEnum.VALUE_TWO);
		bean3.setTestString("This is bean3");
		bean3.setUuid(uuid3);
		bean3.setEnum(SomeEnum.VALUE_THREE);

		mSimpleManager.save(bean1);
		mSimpleManager.save(bean2);
		mSimpleManager.save(bean3);

		final int[] count = new int[] {0};
		mSimpleManager.restore(new DbRowProcessor() {
				public boolean processRow(ResultSet resultSet)
				throws SQLException
				{
					count[0]++;

					String test_string = resultSet.getString("testString");
					assertTrue(
						test_string.equals("This is bean1") ||
						test_string.equals("This is bean2") ||
						test_string.equals("This is bean3") );

					String uuid = resultSet.getString("uuid");
					assertTrue(
							uuid.equals(uuid1.toString()) ||
							uuid.equals(uuid2.toString()) ||
							uuid.equals(uuid3.toString()));

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

		final UUID uuid1 = UUID.randomUUID();
		final UUID uuid2 = UUID.randomUUID();
		final UUID uuid3 = UUID.randomUUID();

		bean1.setTestString("This is bean1");
		bean1.setUuid(uuid1);
		bean2.setTestString("This is bean2");
		bean2.setUuid(uuid2);
		bean3.setTestString("This is bean3");
		bean3.setUuid(uuid3);

		mSimpleManager.save(bean1);
		mSimpleManager.save(bean2);
		mSimpleManager.save(bean3);

		final int[] count = new int[] {0};
		mSimpleManager.restore(mSimpleManager.getRestoreQuery().where("testString", "LIKE", "%bean2"), new DbRowProcessor() {
				public boolean processRow(ResultSet resultSet)
				throws SQLException
				{
					count[0]++;

					String test_string = resultSet.getString("testString");
					assertTrue(test_string.equals("This is bean2"));

					String uuid = resultSet.getString("uuid");
					assertTrue(uuid.equals(uuid2.toString()));

					return true;
				}
			});

		assertEquals(count[0], 1);
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

		mSimpleManager.save(bean1);
		mSimpleManager.save(bean2);
		mSimpleManager.save(bean3);
		mSimpleManager.save(bean4);
		mSimpleManager.save(bean5);

		assertEquals(mSimpleManager.count(), 5);

		mSimpleManager.delete(bean1.getId());
		mSimpleManager.delete(bean2.getId());
		mSimpleManager.delete(bean3.getId());

		assertEquals(mSimpleManager.count(), 2);
	}

}
