/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestRestoreQuery.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.database.querymanagers.generic.beans.BeanImpl;
import com.uwyn.rife.database.querymanagers.generic.beans.LinkBean;
import com.uwyn.rife.database.querymanagers.generic.beans.SimpleBean;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import junit.framework.TestCase;

public class TestRestoreQuery extends TestCase
{
	private Datasource 						mDatasource = null;
	private GenericQueryManager<SimpleBean>	mManager = null;
	private GenericQueryManager<LinkBean>	mLinkManager = null;
	private GenericQueryManager<BeanImpl> 	mBigBeanManager = null;
	
	public TestRestoreQuery(Datasource datasource, String datasourceName, String name)
	{
		super(name);
		mDatasource = datasource;
	}
    
	protected void setUp()
	{
		mManager = GenericQueryManagerFactory.getInstance(mDatasource, SimpleBean.class);
		mLinkManager = GenericQueryManagerFactory.getInstance(mDatasource, LinkBean.class);
		mBigBeanManager = GenericQueryManagerFactory.getInstance(mDatasource, BeanImpl.class);
		
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
			mBigBeanManager.install();
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
	{
		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mManager.remove();
			mLinkManager.remove();
			mBigBeanManager.remove();
		}
		finally
		{
			if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
			{
				mDatasource.setPoolsize(poolsize);
			}
		}
	}
	
	public void testLimit()
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
		
		List<SimpleBean> list = mManager.restore(mManager.getRestoreQuery().limit(2));
		
		assertEquals(list.size(), 2);
	}
	
	public void testOffset()
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
	
		List<SimpleBean> list = mManager.restore(mManager.getRestoreQuery().limit(1).offset(1).orderBy("id"));
		
		assertEquals(list.size(), 1);
		
		assertTrue(list.get(0).getTestString().equals("This is bean2"));
	}
	
	public void testCloneToStringAndClear()
	{
		RestoreQuery query = mManager.getRestoreQuery().where("testString", "=", "bean set 1");
		
		assertEquals(query.toString(), "SELECT * FROM simplebean WHERE testString = 'bean set 1'");
		
		RestoreQuery queryclone = query.clone();
		
		assertEquals(queryclone.toString(), "SELECT * FROM simplebean WHERE testString = 'bean set 1'");
		
		queryclone.where("testString", "!=", "bean set 2");
		
		assertEquals(queryclone.toString(), "SELECT * FROM simplebean WHERE testString = 'bean set 1' AND testString != 'bean set 2'");
		
		queryclone.clear();
		
		assertEquals(queryclone.toString(), "SELECT * FROM simplebean WHERE testString = 'bean set 1'");
		
		query.clear();
		
		assertEquals(query.toString(), "SELECT * FROM simplebean");
	}
	
	public void testDistinctOn()
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
		
		RestoreQuery query = mManager.getRestoreQuery().distinctOn("testString");
		
		try
		{
			List<SimpleBean> beanList = mManager.restore(query);
			
			assertEquals(beanList.size(), 2);
			
			if ("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver()) ||
				"oracle.jdbc.driver.OracleDriver".equals(mDatasource.getAliasedDriver()) ||
				"org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver()) ||
				"org.h2.Driver".equals(mDatasource.getAliasedDriver()) ||
				"org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()) ||
				"com.mckoi.JDBCDriver".equals(mDatasource.getAliasedDriver()) ||
				"org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()) ||
				"in.co.daffodil.db.jdbc.DaffodilDBDriver".equals(mDatasource.getAliasedDriver()))
			{
				fail();
			}
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertEquals("DISTINCT ON", e.getFeature());
			assertTrue("com.mysql.jdbc.Driver".equals(e.getDriver()) ||
					   "oracle.jdbc.driver.OracleDriver".equals(e.getDriver()) ||
					   "org.hsqldb.jdbcDriver".equals(e.getDriver()) ||
					   "org.h2.Driver".equals(e.getDriver()) ||
					   "org.firebirdsql.jdbc.FBDriver".equals(e.getDriver()) ||
					   "com.mckoi.JDBCDriver".equals(e.getDriver()) ||
					   "org.apache.derby.jdbc.EmbeddedDriver".equals(e.getDriver()) ||
					   "in.co.daffodil.db.jdbc.DaffodilDBDriver".equals(e.getDriver()));
		}
		
		query.clear();
		
		query = mManager.getRestoreQuery().distinctOn(new String[] { "testString" });
		
		try
		{
			List<SimpleBean> beanList = mManager.restore(query);
			
			assertEquals(beanList.size(), 2);
			
			if ("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver()) ||
				"oracle.jdbc.driver.OracleDriver".equals(mDatasource.getAliasedDriver()) ||
				"org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver()) ||
				"org.h2.Driver".equals(mDatasource.getAliasedDriver()) ||
				"org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()) ||
				"com.mckoi.JDBCDriver".equals(mDatasource.getAliasedDriver()) ||
				"org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()) ||
				"in.co.daffodil.db.jdbc.DaffodilDBDriver".equals(mDatasource.getAliasedDriver()))
			{
				fail();
			}
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertEquals("DISTINCT ON", e.getFeature());
			assertTrue("com.mysql.jdbc.Driver".equals(e.getDriver()) ||
					   "oracle.jdbc.driver.OracleDriver".equals(e.getDriver()) ||
					   "org.hsqldb.jdbcDriver".equals(e.getDriver()) ||
					   "org.h2.Driver".equals(e.getDriver()) ||
					   "org.firebirdsql.jdbc.FBDriver".equals(e.getDriver()) ||
					   "com.mckoi.JDBCDriver".equals(e.getDriver()) ||
					   "org.apache.derby.jdbc.EmbeddedDriver".equals(e.getDriver()) ||
					   "in.co.daffodil.db.jdbc.DaffodilDBDriver".equals(e.getDriver()));
		}
	}
	
	public void testGetDatasource()
	{
		assertTrue(mDatasource.equals(mManager.getRestoreQuery().getDatasource()));
	}
	
	public void testGetFrom()
	{
		assertTrue(mManager
					   .getRestoreQuery()
					   .getFrom()
					   .equals(SimpleBean.class
								   .getName()
								   .replaceAll(SimpleBean.class
												   .getPackage()
												   .getName()+".", "")
								   .toLowerCase()));
	}
	
	public void testGetParameters()
	{
		Select select = new Select(mDatasource);
		select
			.from("simplebean")
			.whereParameter("testString", "=");
		
		RestoreQuery query = new RestoreQuery(select);
		
		assertEquals(query.getParameters().getOrderedNames().size(), 1);
		assertTrue(query.getParameters().getOrderedNames().contains("testString"));
		
		assertEquals(query.getParameters().getOrderedNamesArray().length, 1);
		assertEquals(query.getParameters().getOrderedNamesArray()[0], "testString");
	}
	
	public void testRestoreFirst()
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
		
		RestoreQuery query = mManager.getRestoreQuery();
		query
			.where("testString", "=", "bean set 1")
			.orderBy("id", Select.ASC);
		
		SimpleBean newbean = mManager.restoreFirst(query);
		
		assertFalse(newbean == bean1);
		assertEquals(newbean.getTestString(), bean1.getTestString());
		assertEquals(newbean.getId(), bean1.getId());
		
		query.clear();
		query
			.where("testString", "=", "bean set 2")
			.orderBy("id", Select.DESC);
		
		SimpleBean otherbean = mManager.restoreFirst(query);
		
		assertFalse(otherbean == bean5);
		assertEquals(otherbean.getTestString(), bean5.getTestString());
		assertEquals(otherbean.getId(), bean5.getId());
	}
	
	public void testJoin()
	{
		SimpleBean bean1 = new SimpleBean();
		SimpleBean bean2 = new SimpleBean();
		SimpleBean bean3 = new SimpleBean();
		SimpleBean bean4 = new SimpleBean();
		SimpleBean bean5 = new SimpleBean();
		
		LinkBean linkbean1 = new LinkBean();
		LinkBean linkbean2 = new LinkBean();
		
		linkbean1.setTestString("linkbean 1");
		linkbean2.setTestString("linkbean 2");
		
		mLinkManager.save(linkbean1);
		mLinkManager.save(linkbean2);
		
		bean1.setTestString("bean set 1");
		bean2.setTestString("bean set 1");
		bean3.setTestString("bean set 1");
		bean4.setTestString("bean set 2");
		bean5.setTestString("bean set 2");
		
		bean1.setLinkBean(linkbean1.getId());
		bean2.setLinkBean(linkbean1.getId());
		bean3.setLinkBean(linkbean1.getId());
		bean4.setLinkBean(linkbean2.getId());
		bean5.setLinkBean(linkbean2.getId());
		
		mManager.save(bean1);
		mManager.save(bean2);
		mManager.save(bean3);
		mManager.save(bean4);
		mManager.save(bean5);
		
		String table = mManager.getTable();
		String table2 = mLinkManager.getTable();
		
		RestoreQuery query = mManager.getRestoreQuery()
			.join(table2)
			.where(table2+".id = "+table+".linkBean")
			.whereAnd(table+".linkBean", "=", linkbean2.getId());
		
		List<SimpleBean> list = mManager.restore(query);
		
		assertEquals(list.size(), 2);
	}
	
	public void testJoinCross()
	{
		SimpleBean bean1 = new SimpleBean();
		SimpleBean bean2 = new SimpleBean();
		SimpleBean bean3 = new SimpleBean();
		SimpleBean bean4 = new SimpleBean();
		SimpleBean bean5 = new SimpleBean();
		
		LinkBean linkbean1 = new LinkBean();
		LinkBean linkbean2 = new LinkBean();
		
		linkbean1.setTestString("linkbean 1");
		linkbean2.setTestString("linkbean 2");
		
		mLinkManager.save(linkbean1);
		mLinkManager.save(linkbean2);
		
		bean1.setTestString("bean set 1");
		bean2.setTestString("bean set 1");
		bean3.setTestString("bean set 1");
		bean4.setTestString("bean set 2");
		bean5.setTestString("bean set 2");
		
		bean1.setLinkBean(linkbean1.getId());
		bean2.setLinkBean(linkbean1.getId());
		bean3.setLinkBean(linkbean1.getId());
		bean4.setLinkBean(linkbean2.getId());
		bean5.setLinkBean(linkbean2.getId());
		
		mManager.save(bean1);
		mManager.save(bean2);
		mManager.save(bean3);
		mManager.save(bean4);
		mManager.save(bean5);
		
		String table = mManager.getTable();
		String table2 = mLinkManager.getTable();
		
		RestoreQuery query = mManager.getRestoreQuery()
			.joinCross(table2)
			.where(table2+".id = "+table+".linkBean")
			.whereAnd(table+".linkBean", "=", linkbean2.getId());
		
		try
		{
			List<SimpleBean> list = mManager.restore(query);
			
			assertEquals(list.size(), 2);
			
			if ("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver()) ||
				"org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()) ||
				"com.mckoi.JDBCDriver".equals(mDatasource.getAliasedDriver()) ||
				"org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
			{
				fail();
			}
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertEquals("CROSS JOIN", e.getFeature());
			assertTrue("org.hsqldb.jdbcDriver".equals(e.getDriver()) ||
					   "org.firebirdsql.jdbc.FBDriver".equals(e.getDriver()) ||
					   "com.mckoi.JDBCDriver".equals(e.getDriver()) ||
					   "org.apache.derby.jdbc.EmbeddedDriver".equals(e.getDriver()));
		}
	}
	
	public void testJoinInner()
	{
		SimpleBean bean1 = new SimpleBean();
		SimpleBean bean2 = new SimpleBean();
		SimpleBean bean3 = new SimpleBean();
		SimpleBean bean4 = new SimpleBean();
		SimpleBean bean5 = new SimpleBean();
		
		LinkBean linkbean1 = new LinkBean();
		LinkBean linkbean2 = new LinkBean();
		
		linkbean1.setTestString("linkbean 1");
		linkbean2.setTestString("linkbean 2");
		
		mLinkManager.save(linkbean1);
		mLinkManager.save(linkbean2);
		
		bean1.setTestString("bean set 1");
		bean2.setTestString("bean set 1");
		bean3.setTestString("bean set 1");
		bean4.setTestString("bean set 2");
		bean5.setTestString("bean set 2");
		
		bean1.setLinkBean(linkbean1.getId());
		bean2.setLinkBean(linkbean1.getId());
		bean3.setLinkBean(linkbean1.getId());
		bean4.setLinkBean(linkbean2.getId());
		bean5.setLinkBean(linkbean2.getId());
		
		mManager.save(bean1);
		mManager.save(bean2);
		mManager.save(bean3);
		mManager.save(bean4);
		mManager.save(bean5);
		
		String table = mManager.getTable();
		String table2 = mLinkManager.getTable();
		
		RestoreQuery query = mManager.getRestoreQuery()
			.joinInner(table2, Select.ON, "0 = 0") // evals to true for mysql sake
			.where(table2+".id = "+table+".linkBean")
			.whereAnd(table+".linkBean", "=", linkbean2.getId());
		
		List<SimpleBean> list = mManager.restore(query);
		
		assertEquals(list.size(), 2);
	}
	
	public void testJoinOuter()
	{
		SimpleBean bean1 = new SimpleBean();
		SimpleBean bean2 = new SimpleBean();
		SimpleBean bean3 = new SimpleBean();
		SimpleBean bean4 = new SimpleBean();
		SimpleBean bean5 = new SimpleBean();
		
		LinkBean linkbean1 = new LinkBean();
		LinkBean linkbean2 = new LinkBean();
		
		linkbean1.setTestString("linkbean 1");
		linkbean2.setTestString("linkbean 2");
		
		mLinkManager.save(linkbean1);
		mLinkManager.save(linkbean2);
		
		bean1.setTestString("bean set 1");
		bean2.setTestString("bean set 1");
		bean3.setTestString("bean set 1");
		bean4.setTestString("bean set 2");
		bean5.setTestString("bean set 2");
		
		bean1.setLinkBean(linkbean1.getId());
		bean2.setLinkBean(linkbean1.getId());
		bean3.setLinkBean(linkbean1.getId());
		bean4.setLinkBean(linkbean2.getId());
		bean5.setLinkBean(linkbean2.getId());
		
		mManager.save(bean1);
		mManager.save(bean2);
		mManager.save(bean3);
		mManager.save(bean4);
		mManager.save(bean5);

		String table = mManager.getTable();
		String table2 = mLinkManager.getTable();
		
		RestoreQuery query = mManager.getRestoreQuery()
			.joinOuter(table2, Select.LEFT, Select.ON, "0 = 0") // evals to true for mysql sake
			.where(table2+".id = "+table+".linkBean")
			.whereAnd(table+".linkBean", "=", linkbean2.getId());
		
		List<SimpleBean> list = mManager.restore(query);
		
		assertEquals(list.size(), 2);
	}
	
	public void testJoinCustom()
	{
		SimpleBean bean1 = new SimpleBean();
		SimpleBean bean2 = new SimpleBean();
		SimpleBean bean3 = new SimpleBean();
		SimpleBean bean4 = new SimpleBean();
		SimpleBean bean5 = new SimpleBean();
		
		LinkBean linkbean1 = new LinkBean();
		LinkBean linkbean2 = new LinkBean();
		
		linkbean1.setTestString("linkbean 1");
		linkbean2.setTestString("linkbean 2");
		
		mLinkManager.save(linkbean1);
		mLinkManager.save(linkbean2);
		
		bean1.setTestString("bean set 1");
		bean2.setTestString("bean set 1");
		bean3.setTestString("bean set 1");
		bean4.setTestString("bean set 2");
		bean5.setTestString("bean set 2");
		
		bean1.setLinkBean(linkbean1.getId());
		bean2.setLinkBean(linkbean1.getId());
		bean3.setLinkBean(linkbean1.getId());
		bean4.setLinkBean(linkbean2.getId());
		bean5.setLinkBean(linkbean2.getId());
		
		mManager.save(bean1);
		mManager.save(bean2);
		mManager.save(bean3);
		mManager.save(bean4);
		mManager.save(bean5);

		String table = mManager.getTable();
		String table2 = mLinkManager.getTable();
		
		RestoreQuery query = mManager.getRestoreQuery()
			.joinCustom("LEFT OUTER JOIN "+table2+" ON 0 = 0") // evals to true for mysql sake
			.where(table2+".id = "+table+".linkBean")
			.whereAnd(table+".linkBean", "=", linkbean2.getId());
		
		List<SimpleBean> list = mManager.restore(query);
		
		assertEquals(list.size(), 2);
	}
	
	public void testOrderBy()
		throws DatabaseException
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
		
		List<SimpleBean> list = mManager.restore(mManager.getRestoreQuery().orderBy("id", Select.DESC));
		
		assertEquals(list.size(), 3);
		
		assertEquals(list.get(0).getId(), bean3.getId());
		assertEquals(list.get(1).getId(), bean2.getId());
		assertEquals(list.get(2).getId(), bean1.getId());
	}
	
	public void testWhere()
		throws DatabaseException
	{
		BeanImpl bean1 = new BeanImpl();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2004, 6, 19, 16, 27, 15);
		cal.set(Calendar.MILLISECOND, 765);
		bean1.setPropertyBigDecimal(new BigDecimal("384834838434.38483"));
		bean1.setPropertyBoolean(false);
		bean1.setPropertyBooleanObject(true);
		bean1.setPropertyByte((byte)90);
		bean1.setPropertyByteObject((byte)35);
		bean1.setPropertyCalendar(cal);
		bean1.setPropertyChar('w');
		bean1.setPropertyCharacterObject('s');
		bean1.setPropertyDate(cal.getTime());
		bean1.setPropertyDouble(37478.34d);
		bean1.setPropertyDoubleObject(384724.692d);
		bean1.setPropertyFloat(34241.2f);
		bean1.setPropertyFloatObject(3432.7f);
		bean1.setPropertyLong(23432L);
		bean1.setPropertyLongObject(23423L);
		bean1.setPropertyShort((short)44);
		bean1.setPropertyShortObject((short)69);
		bean1.setPropertyIntegerObject(421);
		bean1.setPropertySqlDate(new java.sql.Date(cal.getTime().getTime()));
		bean1.setPropertyString("nostringhere");
		bean1.setPropertyStringbuffer(new StringBuffer("buffbuffbuff"));
		bean1.setPropertyTime(new Time(cal.getTime().getTime()));
		bean1.setPropertyTimestamp(new Timestamp(cal.getTime().getTime()));
		
		mBigBeanManager.save(bean1);
		
		List<BeanImpl> list = mBigBeanManager.restore(mBigBeanManager.getRestoreQuery().where("propertyString = 'nostringhere'"));
		assertEquals(list.size(), 1);
		
		list = mBigBeanManager.restore(mBigBeanManager.getRestoreQuery().where("propertyBoolean", "=", false));
		assertEquals(list.size(), 1);
		
		list = mBigBeanManager.restore(mBigBeanManager.getRestoreQuery().where("propertyByte", "=", (byte)90));
		assertEquals(list.size(), 1);
		
		list = mBigBeanManager.restore(mBigBeanManager.getRestoreQuery().where("propertyChar", "=", 'w'));
		assertEquals(list.size(), 1);
		
		list = mBigBeanManager.restore(mBigBeanManager.getRestoreQuery().where("propertyDouble", "=", 37478.34d));
		assertEquals(list.size(), 1);
		
		if (!("org.postgresql.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver())) &&
			!("org.h2.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))) // skip this for pgsql, mysql, hsqldb, h2 and derby since it doesn't work
		{
			list = mBigBeanManager.restore(mBigBeanManager.getRestoreQuery().where("propertyFloat", "=", 34241.2f));
			
			assertEquals(list.size(), 1);
		}
		
		list = mBigBeanManager.restore(mBigBeanManager.getRestoreQuery().where("propertyLong", "=", 23432L));
		assertEquals(list.size(), 1);
		
		list = mBigBeanManager.restore(mBigBeanManager.getRestoreQuery().where("propertyShort", "=", (short)44));
		assertEquals(list.size(), 1);
		
		// cheating because the GQM doesn't currently return any queries with a where clause already
		RestoreQuery query = new RestoreQuery(mBigBeanManager.getRestoreQuery().where("id", "=", bean1.getId()).getDelegate());
		
		list = mBigBeanManager.restore(query.where("propertyString = 'nostringhere'"));
		assertEquals(list.size(), 1);
		
		list = mBigBeanManager.restore(query.where("propertyBoolean", "=", false));
		assertEquals(list.size(), 1);
		
		list = mBigBeanManager.restore(query.where("propertyByte", "=", (byte)90));
		assertEquals(list.size(), 1);
		
		list = mBigBeanManager.restore(query.where("propertyChar", "=", 'w'));
		assertEquals(list.size(), 1);
		
		list = mBigBeanManager.restore(query.where("propertyDouble", "=", 37478.34d));
		assertEquals(list.size(), 1);
		
		if (!("org.postgresql.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver())) &&
			!("org.h2.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))) // skip this for postgres, mysql, hsqldb, h2 and derby since it doesn't work
		{
			list = mBigBeanManager.restore(query.where("propertyFloat", "=", 34241.2f));
			
			assertEquals(list.size(), 1);
		}
		
		list = mBigBeanManager.restore(query.where("id", "=", bean1.getId())); // primary key
		assertEquals(list.size(), 1);
		
		list = mBigBeanManager.restore(query.where("propertyLong", "=", 23432L));
		assertEquals(list.size(), 1);
		
		list = mBigBeanManager.restore(query.where("propertyShort", "=", (short)44));
		assertEquals(list.size(), 1);
	}
	
	public void testWhereAnd()
	{
		BeanImpl bean1 = new BeanImpl();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2004, 6, 19, 16, 27, 15);
		cal.set(Calendar.MILLISECOND, 765);
		bean1.setPropertyBigDecimal(new BigDecimal("384834838434.38483"));
		bean1.setPropertyBoolean(false);
		bean1.setPropertyBooleanObject(true);
		bean1.setPropertyByte((byte)90);
		bean1.setPropertyByteObject((byte)35);
		bean1.setPropertyCalendar(cal);
		bean1.setPropertyChar('w');
		bean1.setPropertyCharacterObject('s');
		bean1.setPropertyDate(cal.getTime());
		bean1.setPropertyDouble(37478.34d);
		bean1.setPropertyDoubleObject(384724.692d);
		bean1.setPropertyFloat(34241.2f);
		bean1.setPropertyFloatObject(3432.7f);
		bean1.setPropertyLong(23432L);
		bean1.setPropertyLongObject(23423L);
		bean1.setPropertyShort((short)44);
		bean1.setPropertyShortObject((short)69);
		bean1.setPropertyIntegerObject(421);
		bean1.setPropertySqlDate(new java.sql.Date(cal.getTime().getTime()));
		bean1.setPropertyString("nostringhere");
		bean1.setPropertyStringbuffer(new StringBuffer("buffbuffbuff"));
		bean1.setPropertyTime(new Time(cal.getTime().getTime()));
		bean1.setPropertyTimestamp(new Timestamp(cal.getTime().getTime()));
		
		mBigBeanManager.save(bean1);
		
		List<BeanImpl> list = mBigBeanManager.restore(
			mBigBeanManager.getRestoreQuery()
				.where("id", "=", bean1.getId())
				.whereAnd("propertyString = 'nostringhere'")
				.whereAnd("propertyBoolean", "=", false)
				.whereAnd("propertyByte", "=", (byte)90)
				.whereAnd("propertyChar", "=", 'w')
				.whereAnd("propertyDouble", "=", 37478.34d)
				.whereAnd("propertyLong", "=", 23432L)
				.whereAnd("propertyString", "=", "nostringhere")
				.whereAnd("propertyIntegerObject", "=", 421)
				.whereAnd("propertyShort", "=", (short)44)
		);
		
		assertEquals(list.size(), 1);
		
		if (!("org.postgresql.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver())) &&
			!("org.h2.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))) // skip this for postgres, mysql, hsqldb, h2 and derby since it doesn't work
		{
			list = mBigBeanManager.restore(mBigBeanManager.getRestoreQuery().where("id = 1").whereAnd("propertyFloat", "=", 34241.2f));
			
			assertEquals(list.size(), 1);
		}
	}
	
	public void testWhereOr()
	{
		BeanImpl bean1 = new BeanImpl();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2004, 6, 19, 16, 27, 15);
		cal.set(Calendar.MILLISECOND, 765);
		bean1.setPropertyBigDecimal(new BigDecimal("384834838434.38483"));
		bean1.setPropertyBoolean(false);
		bean1.setPropertyBooleanObject(true);
		bean1.setPropertyByte((byte)90);
		bean1.setPropertyByteObject((byte)35);
		bean1.setPropertyCalendar(cal);
		bean1.setPropertyChar('w');
		bean1.setPropertyCharacterObject('s');
		bean1.setPropertyDate(cal.getTime());
		bean1.setPropertyDouble(37478.34d);
		bean1.setPropertyDoubleObject(384724.692d);
		bean1.setPropertyFloat(34241.2f);
		bean1.setPropertyFloatObject(3432.7f);
		bean1.setPropertyLong(23432L);
		bean1.setPropertyLongObject(23423L);
		bean1.setPropertyShort((short)44);
		bean1.setPropertyShortObject((short)69);
		bean1.setPropertyIntegerObject(421);
		bean1.setPropertySqlDate(new java.sql.Date(cal.getTime().getTime()));
		bean1.setPropertyString("nostringhere");
		bean1.setPropertyStringbuffer(new StringBuffer("buffbuffbuff"));
		bean1.setPropertyTime(new Time(cal.getTime().getTime()));
		bean1.setPropertyTimestamp(new Timestamp(cal.getTime().getTime()));
		
		mBigBeanManager.save(bean1);
		
		List<BeanImpl> list = mBigBeanManager.restore(
			mBigBeanManager.getRestoreQuery()
				.where("id = 1")
				.whereOr( "propertyString = 'nostringhere'")
				.whereOr("propertyBoolean", "=", false)
				.whereOr("propertyByte", "=", (byte)90)
				.whereOr("propertyChar", "=", 'w')
				.whereOr("propertyDouble", "=", 37478.34d)
				.whereOr("propertyLong", "=", 23432L)
				.whereOr("propertyIntegerObject", "=", 421)
				.whereOr("propertyShort", "=", (short)44)
				.whereOr("propertyString", "=", "nostringhere")
		);
		
		assertEquals(list.size(), 1);
		
		if (!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver()))) // skip this for mysql and hsqldb since it doesn't work
		{
			list = mBigBeanManager.restore(mBigBeanManager.getRestoreQuery().where("id = 1").whereOr("propertyFloat", "=", 34241.2f));
			
			assertEquals(list.size(), 1);
		}
	}
	
	public void testUnion()
	{
		RestoreQuery query = mManager.getRestoreQuery();
		
		query
			.union("uexpr1")
			.union(new Select(mDatasource).from("table2"));
		
		if ("com.mckoi.JDBCDriver".equals(mDatasource.getAliasedDriver())) // McKoi only supports UNION All
		{
			assertEquals(query.getSql(), "SELECT * FROM simplebean UNION ALL uexpr1 UNION ALL SELECT * FROM table2");
		}
		else
		{
			assertEquals(query.getSql(), "SELECT * FROM simplebean UNION uexpr1 UNION SELECT * FROM table2");
		}
	}
	
	public void testWhereSubselect()
	{
		SimpleBean bean1 = new SimpleBean();
		SimpleBean bean2 = new SimpleBean();
		SimpleBean bean3 = new SimpleBean();
		SimpleBean bean4 = new SimpleBean();
		SimpleBean bean5 = new SimpleBean();
		
		LinkBean linkbean1 = new LinkBean();
		LinkBean linkbean2 = new LinkBean();
		
		linkbean1.setTestString("linkbean 1");
		linkbean2.setTestString("linkbean 2");
		
		mLinkManager.save(linkbean1);
		mLinkManager.save(linkbean2);
		
		bean1.setTestString("bean set 1");
		bean2.setTestString("bean set 1");
		bean3.setTestString("bean set 1");
		bean4.setTestString("bean set 2");
		bean5.setTestString("bean set 2");
		
		bean1.setLinkBean(linkbean1.getId());
		bean2.setLinkBean(linkbean1.getId());
		bean3.setLinkBean(linkbean1.getId());
		bean4.setLinkBean(linkbean2.getId());
		bean5.setLinkBean(linkbean2.getId());
		
		mManager.save(bean1);
		mManager.save(bean2);
		mManager.save(bean3);
		mManager.save(bean4);
		mManager.save(bean5);
		
		Select select = new Select(mDatasource);
		select
			.from(mLinkManager.getTable())
			.field("id")
			.where("id", "=", linkbean1.getId());
		
		RestoreQuery query = mManager.getRestoreQuery();
		query
			.where("linkBean = ("+select.getSql()+")")
			.whereSubselect(select);
		
		if (!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver()))) // skip this for mysql since it doesn't work
		{
			List<SimpleBean> list = mManager.restore(query);
			assertEquals(list.size(), 3);
		}
	}
	
	public void testFields()
	{
		SimpleBean bean1 = new SimpleBean();
		SimpleBean bean2 = new SimpleBean();
		
		LinkBean linkbean1 = new LinkBean();
		
		linkbean1.setTestString("linkbean 1");
		
		bean1.setTestString("bean set 1");
		bean2.setTestString("bean set 1");
		
		bean1.setLinkBean(1);
		bean2.setLinkBean(1);
		
		mLinkManager.save(linkbean1);
		
		mManager.save(bean1);
		mManager.save(bean2);
		
		RestoreQuery query = mManager.getRestoreQuery();
		query
			.field("testString");
		
		List<SimpleBean> bean_list = mManager.restore(query);
		
		for (SimpleBean bean : bean_list)
		{
			assertEquals(-1, bean.getLinkBean());
			assertEquals("bean set 1", bean.getTestString());
		}
		
		query = mManager.getRestoreQuery();
		query
			.fields(SimpleBean.class);
		
		bean_list = mManager.restore(query);
		
		query = mManager.getRestoreQuery();
		query
			.fields("simplebean", SimpleBean.class);
		
		bean_list = mManager.restore(query);
		
		for (SimpleBean bean : bean_list)
		{
			assertEquals(1, bean.getLinkBean());
			assertEquals("bean set 1", bean.getTestString());
		}
		
		query = mManager.getRestoreQuery();
		query
			.fieldsExcluded(SimpleBean.class, "testString" );
		
		bean_list = mManager.restore(query);
		
		query = mManager.getRestoreQuery();
		query
			.fieldsExcluded("simplebean", SimpleBean.class, "testString" );
		
		bean_list = mManager.restore(query);
		
		for (SimpleBean bean : bean_list)
		{
			assertEquals(1, bean.getLinkBean());
			assertEquals(null, bean.getTestString());
		}
		
		query = mManager.getRestoreQuery();
		query
			.fields(new String[] { "linkBean" });
		
		bean_list = mManager.restore(query);
		
		for (SimpleBean bean : bean_list)
		{
			assertEquals(1, bean.getLinkBean());
			assertEquals(null, bean.getTestString());
		}
		
		Select select = new Select(mDatasource).field("name").from("tablename");
		
		query = mManager.getRestoreQuery();
		query
			.fieldSubselect(select)
			.field('('+select.getSql()+')');
		
		assertEquals("SELECT (SELECT name FROM tablename) FROM simplebean", query.getSql());
	}
	
	public void testWhereGroupChaining()
	{
		SimpleBean bean1 = new SimpleBean();
		bean1.setTestString("test");
		
		mManager.save(bean1);
		
		RestoreQuery query = mManager.getRestoreQuery()
			.where("id", ">=", 0)
			.startWhereAnd()
				.where("testString", "=", "test")
			.end();
		
		assertTrue(mManager.restore(query).size() > 0);
	}
}


