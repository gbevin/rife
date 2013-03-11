/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestGenericQueryManagerUnique.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.querymanagers.generic.beans.UniqueBean;
import com.uwyn.rife.database.querymanagers.generic.beans.UniqueBeanNotNull;
import java.sql.SQLException;
import junit.framework.TestCase;

public class TestGenericQueryManagerUnique extends TestCase
{
    private Datasource 	mDatasource = null;

	private GenericQueryManager<UniqueBean> mUniqueManager = null;

	public TestGenericQueryManagerUnique(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}
	
	private UniqueBean createNewUniqueBean()
	{
		if ("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
		{
			return new UniqueBeanNotNull();
		}
		else
		{
			return new UniqueBean();
		}
	}

	protected void setUp()
	throws Exception
	{
		if ("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
		{
			mUniqueManager = (GenericQueryManager<UniqueBean>)((GenericQueryManager)GenericQueryManagerFactory.getInstance(mDatasource, UniqueBeanNotNull.class));
		}
		else
		{
			mUniqueManager = GenericQueryManagerFactory.getInstance(mDatasource, UniqueBean.class);
		}

		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mUniqueManager.install();
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
			mUniqueManager.remove();
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
		if ("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
		{
			assertSame(UniqueBeanNotNull.class, mUniqueManager.getBaseClass());
		}
		else
		{
			assertSame(UniqueBean.class, mUniqueManager.getBaseClass());
		}
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
			mUniqueManager.remove();

			mUniqueManager.install(mUniqueManager.getInstallTableQuery());
		}
		finally
		{
			if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
			{
				mDatasource.setPoolsize(poolsize);
			}
		}
	}

	public void testValidationContextUnique()
	{
		// uniqueness of individual properties
		UniqueBean bean1 = createNewUniqueBean();
		bean1.setTestString("test_string");
		bean1.setAnotherString("another_string_one");
		bean1.setThirdString("third_string_one");
		assertTrue(bean1.validate(mUniqueManager));
		int id1 = mUniqueManager.save(bean1);

		bean1 = mUniqueManager.restore(id1);
		bean1.setTestString("test_string_one");
		assertTrue(bean1.validate(mUniqueManager));
		assertEquals(id1, mUniqueManager.save(bean1));

		UniqueBean bean2 = createNewUniqueBean();
		bean2.setTestString("test_string_one");
		bean2.setAnotherString("another_string_two");
		bean2.setThirdString("third_string_two");
		assertFalse(bean2.validate(mUniqueManager));
		try
		{
			mUniqueManager.save(bean2);
			fail();
		}
		catch (DatabaseException e)
		{
			assertTrue(e.getCause() instanceof SQLException);
		}

		bean2.resetValidation();
		bean2.setTestString("test_string_two");
		bean2.setAnotherString("another_string_two");
		assertTrue(bean2.validate(mUniqueManager));
		int id2 = mUniqueManager.save(bean2);
		assertTrue(id1 != id2);

		bean1.resetValidation();
		bean1.setTestString("test_string_two");
		assertFalse(bean1.validate(mUniqueManager));
		try
		{
			mUniqueManager.save(bean1);
			fail();
		}
		catch (DatabaseException e)
		{
			assertTrue(e.getCause() instanceof SQLException);
		}

		// uniqueness of multiple properties
		UniqueBean bean3 = createNewUniqueBean();
		bean3.setTestString("test_string_three");
		bean3.setAnotherString("another_string");
		bean3.setThirdString("third_string");
		assertTrue(bean3.validate(mUniqueManager));
		int id3 = mUniqueManager.save(bean3);

		bean3 = mUniqueManager.restore(id3);
		bean3.setAnotherString("another_string_three");
		bean3.setThirdString("third_string_three");
		assertTrue(bean3.validate(mUniqueManager));
		assertEquals(id3, mUniqueManager.save(bean3));

		UniqueBean bean4 = createNewUniqueBean();
		bean4.setTestString("test_string_four");
		bean4.setAnotherString("another_string_three");
		bean4.setThirdString("third_string_three");
		assertFalse(bean4.validate(mUniqueManager));
		try
		{
			mUniqueManager.save(bean4);
			fail();
		}
		catch (DatabaseException e)
		{
			assertTrue(e.getCause() instanceof SQLException);
		}

		bean4.resetValidation();
		bean4.setAnotherString("another_string_four");
		bean4.setThirdString("third_string_four");
		assertTrue(bean4.validate(mUniqueManager));
		int id4 = mUniqueManager.save(bean4);
		assertTrue(id3 != id4);

		bean3.resetValidation();
		bean3.setAnotherString("another_string_four");
		bean3.setThirdString("third_string_four");
		assertFalse(bean3.validate(mUniqueManager));
		try
		{
			mUniqueManager.save(bean3);
			fail();
		}
		catch (DatabaseException e)
		{
			assertTrue(e.getCause() instanceof SQLException);
		}

		if (!"org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
		{
			UniqueBean bean5 = createNewUniqueBean();
			bean5.setTestString("test_string_five");
			bean5.setAnotherString("another_string_five");
			assertTrue(bean5.validate(mUniqueManager));
			mUniqueManager.save(bean5);

			UniqueBean bean6 = createNewUniqueBean();
			bean6.setTestString("test_string_six");
			bean6.setAnotherString("another_string_five");
			assertTrue(bean6.validate(mUniqueManager));
			// this is DB-specific
//			try
//			{
//				mUniqueManager.save(bean6);
//				fail();
//			}
//			catch (DatabaseException e)
//			{
//				assertTrue(e.getCause() instanceof SQLException);
//			}
		}
	}
	
	public void testGroupValidationContextUnique()
	{
		// uniqueness of individual properties
		UniqueBean bean1 = createNewUniqueBean();
		bean1.setTestString("test_string");
		bean1.setAnotherString("another_string_one");
		bean1.setThirdString("third_string_one");
		assertTrue(bean1.validateGroup("group1", mUniqueManager));
		bean1.resetValidation();
		assertTrue(bean1.validateGroup("group2", mUniqueManager));
		int id1 = mUniqueManager.save(bean1);
		
		bean1 = mUniqueManager.restore(id1);
		bean1.setTestString("test_string_one");
		assertTrue(bean1.validateGroup("group1", mUniqueManager));
		bean1.resetValidation();
		assertTrue(bean1.validateGroup("group2", mUniqueManager));
		assertEquals(id1, mUniqueManager.save(bean1));
		
		UniqueBean bean2 = createNewUniqueBean();
		bean2.setTestString("test_string_one");
		bean2.setAnotherString("another_string_two");
		bean2.setThirdString("third_string_two");
		assertFalse(bean2.validateGroup("group1", mUniqueManager));
		bean2.resetValidation();
		assertFalse(bean2.validateGroup("group2", mUniqueManager));
		try
		{
			mUniqueManager.save(bean2);
			fail();
		}
		catch (DatabaseException e)
		{
			assertTrue(e.getCause() instanceof SQLException);
		}
		
		bean2.resetValidation();
		bean2.setTestString("test_string_two");
		bean2.setAnotherString("another_string_two");
		assertTrue(bean2.validateGroup("group1", mUniqueManager));
		bean2.resetValidation();
		assertTrue(bean2.validateGroup("group2", mUniqueManager));
		int id2 = mUniqueManager.save(bean2);
		assertTrue(id1 != id2);
		
		bean1.resetValidation();
		bean1.setTestString("test_string_two");
		assertFalse(bean1.validateGroup("group1", mUniqueManager));
		bean1.resetValidation();
		assertFalse(bean1.validateGroup("group2", mUniqueManager));
		try
		{
			mUniqueManager.save(bean1);
			fail();
		}
		catch (DatabaseException e)
		{
			assertTrue(e.getCause() instanceof SQLException);
		}
	}
}
