/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCountQuery.java 3918 2008-04-14 17:35:35Z gbevin $
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
import junit.framework.TestCase;

public class TestCountQuery extends TestCase
{
	private Datasource 						mDatasource = null;
	private GenericQueryManager<SimpleBean>	mManager = null;
	private GenericQueryManager<LinkBean>	mLinkManager = null;
	private GenericQueryManager<BeanImpl> 	mBigBeanManager = null;
	
	public TestCountQuery(Datasource datasource, String datasourceName, String name)
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
	
	public void testCloneToStringAndClear()
	{
		CountQuery query = mManager.getCountQuery().where("testString", "=", "bean set 1");
		
		assertEquals(query.toString(), "SELECT count(*) FROM simplebean WHERE testString = 'bean set 1'");
		
		CountQuery queryclone = query.clone();
		
		assertEquals(queryclone.toString(), "SELECT count(*) FROM simplebean WHERE testString = 'bean set 1'");
		
		queryclone.where("testString", "!=", "bean set 2");
		
		assertEquals(queryclone.toString(), "SELECT count(*) FROM simplebean WHERE testString = 'bean set 1' AND testString != 'bean set 2'");
		
		queryclone.clear();
		
		assertEquals(queryclone.toString(), "SELECT count(*) FROM simplebean WHERE testString = 'bean set 1'");
		
		query.clear();
		
		assertEquals(query.toString(), "SELECT count(*) FROM simplebean");
	}
	
	public void testGetDatasource()
	{
		assertTrue(mDatasource.equals(mManager.getCountQuery().getDatasource()));
	}
	
	public void testGetFrom()
	{
		assertTrue(mManager
					   .getCountQuery()
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
		
		CountQuery query = new CountQuery(select);
		
		assertEquals(query.getParameters().getOrderedNames().size(), 1);
		assertTrue(query.getParameters().getOrderedNames().contains("testString"));
		
		assertEquals(query.getParameters().getOrderedNamesArray().length, 1);
		assertEquals(query.getParameters().getOrderedNamesArray()[0], "testString");
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
		
		CountQuery query = mManager.getCountQuery()
			.join(table2)
			.where(table2+".id = "+table+".linkBean")
			.whereAnd(table+".linkBean", "=", linkbean2.getId());
		
		assertEquals(2, mManager.count(query));
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
		
		CountQuery query = mManager.getCountQuery()
			.joinCross(table2)
			.where(table2+".id = "+table+".linkBean")
			.whereAnd(table+".linkBean", "=", linkbean2.getId());
		
		try
		{
			assertEquals(2, mManager.count(query));
			
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
		
		CountQuery query = mManager.getCountQuery()
			.joinInner(table2, Select.ON, "0 = 0") // evals to true for mysql sake
			.where(table2+".id = "+table+".linkBean")
			.whereAnd(table+".linkBean", "=", linkbean2.getId());
		
		assertEquals(2, mManager.count(query));
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
		
		bean1.setTestString("bean set 1");
		bean2.setTestString("bean set 1");
		bean3.setTestString("bean set 1");
		bean4.setTestString("bean set 2");
		bean5.setTestString("bean set 2");
		
		bean1.setLinkBean(1);
		bean2.setLinkBean(1);
		bean3.setLinkBean(1);
		bean4.setLinkBean(2);
		bean5.setLinkBean(2);
		
		mLinkManager.save(linkbean1);
		mLinkManager.save(linkbean2);
		
		mManager.save(bean1);
		mManager.save(bean2);
		mManager.save(bean3);
		mManager.save(bean4);
		mManager.save(bean5);
		
		String table = mManager.getTable();
		String table2 = mLinkManager.getTable();
		
		CountQuery query = mManager.getCountQuery()
			.joinOuter(table2, Select.LEFT, Select.ON, table2+".id = "+table+".linkBean") // evals to true for mysql sake
			.where(table+".linkBean = 2");

		assertEquals(2, mManager.count(query));
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
		
		bean1.setTestString("bean set 1");
		bean2.setTestString("bean set 1");
		bean3.setTestString("bean set 1");
		bean4.setTestString("bean set 2");
		bean5.setTestString("bean set 2");
		
		bean1.setLinkBean(1);
		bean2.setLinkBean(1);
		bean3.setLinkBean(1);
		bean4.setLinkBean(2);
		bean5.setLinkBean(2);
		
		mLinkManager.save(linkbean1);
		mLinkManager.save(linkbean2);
		
		mManager.save(bean1);
		mManager.save(bean2);
		mManager.save(bean3);
		mManager.save(bean4);
		mManager.save(bean5);
		
		String table = mManager.getTable();
		String table2 = mLinkManager.getTable();
		
		CountQuery query = mManager.getCountQuery()
			.joinCustom("LEFT OUTER JOIN "+table2+" ON "+table2+".id = "+table+".linkBean") // evals to true for mysql sake
			.where(table+".linkBean = 2");
		
		assertEquals(2, mManager.count(query));
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
		
		assertEquals(1, mBigBeanManager.count(mBigBeanManager.getCountQuery().where("propertyString = 'nostringhere'")));
		
		assertEquals(1, mBigBeanManager.count(mBigBeanManager.getCountQuery().where("propertyBoolean", "=", false)));
		
		assertEquals(1, mBigBeanManager.count(mBigBeanManager.getCountQuery().where("propertyByte", "=", (byte)90)));
		
		assertEquals(1, mBigBeanManager.count(mBigBeanManager.getCountQuery().where("propertyChar", "=", 'w')));
		
		assertEquals(1, mBigBeanManager.count(mBigBeanManager.getCountQuery().where("propertyDouble", "=", 37478.34d)));
		
		if (!("org.postgresql.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver())) &&
			!("org.h2.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))) // skip this for postgres, mysql, hsqldb, h2 and derby since it doesn't work
		{
			assertEquals(1, mBigBeanManager.count(mBigBeanManager.getCountQuery().where("propertyFloat", "=", 34241.2f)));
		}
		
		assertEquals(1, mBigBeanManager.count(mBigBeanManager.getCountQuery().where("propertyLong", "=", 23432L)));
		
		assertEquals(1, mBigBeanManager.count(mBigBeanManager.getCountQuery().where("propertyShort", "=", (short)44)));
		
		// cheating because the GQM doesn't currently return any queries with a where clause already
		CountQuery query = new CountQuery(mBigBeanManager.getCountQuery().where("id", "=", bean1.getId()).getDelegate());
		
		assertEquals(1, mBigBeanManager.count(query.where("propertyString = 'nostringhere'")));
		
		assertEquals(1, mBigBeanManager.count(query.where("propertyBoolean", "=", false)));
		
		assertEquals(1, mBigBeanManager.count(query.where("propertyByte", "=", (byte)90)));
		
		assertEquals(1, mBigBeanManager.count(query.where("propertyChar", "=", 'w')));
		
		assertEquals(1, mBigBeanManager.count(query.where("propertyDouble", "=", 37478.34d)));
		
		if (!("org.postgresql.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver())) &&
			!("org.h2.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))) // skip this for postgres, mysql, hsqldb, h2 and derby since it doesn't work
		{
			assertEquals(1, mBigBeanManager.count(query.where("propertyFloat", "=", 34241.2f)));
		}
		
		assertEquals(1, mBigBeanManager.count(query.where("id", "=", bean1.getId()))); // primary key
		
		assertEquals(1, mBigBeanManager.count(query.where("propertyLong", "=", 23432L)));
		
		assertEquals(1, mBigBeanManager.count(query.where("propertyShort", "=", (short)44)));
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
		
		assertEquals(1, mBigBeanManager.count(
			mBigBeanManager.getCountQuery()
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
		));
		
		if (!("org.postgresql.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver())) &&
			!("org.h2.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))) // skip this for postgres, mysql, hsqldb, h2 and derby since it doesn't work
		{
			assertEquals(1, mBigBeanManager.count(mBigBeanManager.getCountQuery().where("id", "=", bean1.getId()).whereAnd("propertyFloat", "=", 34241.2f)));
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
		
		assertEquals(1, mBigBeanManager.count(
			mBigBeanManager.getCountQuery()
				.where("id", "=", bean1.getId())
				.whereOr( "propertyString = 'nostringhere'")
				.whereOr("propertyBoolean", "=", false)
				.whereOr("propertyByte", "=", (byte)90)
				.whereOr("propertyChar", "=", 'w')
				.whereOr("propertyDouble", "=", 37478.34d)
				.whereOr("propertyLong", "=", 23432L)
				.whereOr("propertyIntegerObject", "=", 421)
				.whereOr("propertyShort", "=", (short)44)
				.whereOr("propertyString", "=", "nostringhere")
		));
		
		if (!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver()))) // skip this for mysql and hsqldb since it doesn't work
		{
			assertEquals(1, mBigBeanManager.count(mBigBeanManager.getCountQuery().where("id", "=", bean1.getId()).whereOr("propertyFloat", "=", 34241.2f)));
		}
	}
	
	public void testUnion()
	{
		CountQuery query = mManager.getCountQuery();
		
		query
			.union("uexpr1")
			.union(new Select(mDatasource).field("count(*)").from("table2"));
		
		if ("com.mckoi.JDBCDriver".equals(mDatasource.getAliasedDriver())) // McKoi only supports UNION All
		{
			assertEquals(query.getSql(), "SELECT count(*) FROM simplebean UNION ALL uexpr1 UNION ALL SELECT count(*) FROM table2");
		}
		else
		{
			assertEquals(query.getSql(), "SELECT count(*) FROM simplebean UNION uexpr1 UNION SELECT count(*) FROM table2");
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
		
		CountQuery query = mManager.getCountQuery();
		query
			.where("linkBean = ("+select.getSql()+")")
			.whereSubselect(select);
		
		if (!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver()))) // skip this for mysql since it doesn't work
		{
			assertEquals(3, mManager.count(query));
		}
	}
}

