/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestGenericQueryManagerConstrained.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.querymanagers.generic.beans.ConstrainedBean;
import com.uwyn.rife.database.querymanagers.generic.beans.LinkBean;
import com.uwyn.rife.site.ValidationError;
import java.sql.SQLException;
import junit.framework.TestCase;

public class TestGenericQueryManagerConstrained extends TestCase
{
    private Datasource 	mDatasource = null;

	private GenericQueryManager<LinkBean>			mLinkManager = null;
	private GenericQueryManager<ConstrainedBean> 	mConstrainedManager = null;

	public TestGenericQueryManagerConstrained(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}

	protected void setUp()
	throws Exception
	{
		mLinkManager = GenericQueryManagerFactory.getInstance(mDatasource, LinkBean.class);
		mConstrainedManager = GenericQueryManagerFactory.getInstance(mDatasource, ConstrainedBean.class);

		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mLinkManager.install();
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
			mLinkManager.remove();
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
		assertSame(LinkBean.class, mLinkManager.getBaseClass());
		assertSame(ConstrainedBean.class, mConstrainedManager.getBaseClass());
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
			mConstrainedManager.remove();
			mLinkManager.remove();

			mLinkManager.install(mLinkManager.getInstallTableQuery());
			mConstrainedManager.install(mConstrainedManager.getInstallTableQuery());
		}
		finally
		{
			if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
			{
				mDatasource.setPoolsize(poolsize);
			}
		}
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
	
	public void testValidationContextManyToOne()
	{
		LinkBean linkbean1 = new LinkBean();
		LinkBean linkbean2 = new LinkBean();
		LinkBean linkbean3 = new LinkBean();

		linkbean1.setTestString("linkbean 1");
		linkbean2.setTestString("linkbean 2");
		linkbean3.setTestString("linkbean 3");

		mLinkManager.save(linkbean1);
		mLinkManager.save(linkbean2);
		mLinkManager.save(linkbean3);

		ConstrainedBean bean1 = new ConstrainedBean();
		bean1.setTestString("test_string1");
		assertTrue(bean1.validate(mConstrainedManager));
		int id1 = mConstrainedManager.save(bean1);

		ConstrainedBean bean2 = new ConstrainedBean();
		bean2.setTestString("test_string2");
		bean2.setLinkBean(linkbean1.getId());
		assertTrue(bean2.validate(mConstrainedManager));
		int id2 = mConstrainedManager.save(bean2);
		assertTrue(id1 != id2);

		ConstrainedBean bean3 = new ConstrainedBean();
		bean3.setTestString("test_string2");
		bean3.setLinkBean(23);
		assertFalse(bean3.validate(mConstrainedManager));
		ValidationError error = (ValidationError)bean3.getValidationErrors().iterator().next();
		assertEquals(error.getSubject(), "linkBean");
		assertEquals(error.getIdentifier(), ValidationError.IDENTIFIER_INVALID);
		if (!"com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver()))
		{
			try
			{
				mConstrainedManager.save(bean3);
				fail("exception not thrown");
			}
			catch (DatabaseException e)
			{
				assertTrue(e.getCause() instanceof SQLException);
			}
		}

		bean3.resetValidation();
		bean3.setLinkBean(linkbean3.getId());
		assertTrue(bean3.validate(mConstrainedManager));
		int id3 = mConstrainedManager.save(bean3);
		assertTrue(id2 != id3);
	}
}
