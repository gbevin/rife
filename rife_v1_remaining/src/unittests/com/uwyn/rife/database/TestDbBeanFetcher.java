/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDbBeanFetcher.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.DropTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import junit.framework.TestCase;

public class TestDbBeanFetcher extends TestCase
{
	private Datasource  mDatasource = null;
    
	public TestDbBeanFetcher(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}

	public void testFetchBean()
	throws Exception
	{
		final DbQueryManager manager = new DbQueryManager(mDatasource);
		
		// create the temporary table
		CreateTable query_create = new CreateTable(mDatasource);
		query_create.table("tmp_beanmanager")
			.columns(BeanImpl.class)
			.precision("propertyString", 255)
			.precision("propertyStringbuffer", 255)
			.precision("propertyChar", 1)
			.precision("propertyDouble", 8, 3)
			.precision("propertyFloat", 10, 5)
			.precision("propertyDoubleObject", 8, 3)
			.precision("propertyFloatObject", 8, 2)
			.precision("propertyBigDecimal", 16, 6);
		
		
		DbBeanFetcher<BeanImpl> fetcher = null;
		try
		{
			manager.executeUpdate(query_create);
			
			fetcher = manager.inTransaction(new DbTransactionUser() {
					public DbBeanFetcher<BeanImpl> useTransaction()
					throws InnerClassException
					{
						DbBeanFetcher<BeanImpl>	fetcher = null;
						try
						{
							// populate with test data
							Calendar cal = Calendar.getInstance();
							cal.set(2002, 5, 17, 15, 36);
							cal.set(Calendar.MILLISECOND, 0);	// milliseconds are only correctly supported by postgresql, don't include them in generic tests

							BeanImpl bean_populated = new BeanImpl();
							bean_populated.setPropertyString("somestring");
							bean_populated.setPropertyStringbuffer(new StringBuffer("somestringbuffer"));
							bean_populated.setPropertyDate(cal.getTime());
							bean_populated.setPropertyCalendar(cal);
							bean_populated.setPropertySqlDate(new java.sql.Date(cal.getTime().getTime()));
							bean_populated.setPropertyTime(new Time(cal.getTime().getTime()));
							bean_populated.setPropertyTimestamp(new Timestamp(cal.getTime().getTime()));
							bean_populated.setPropertyChar('v');
							bean_populated.setPropertyBoolean(true);
							bean_populated.setPropertyByte((byte)127);
							bean_populated.setPropertyDouble(78634.433d);
							bean_populated.setPropertyFloat(76734.87834f);
							bean_populated.setPropertyDoubleObject(81432.971d);
							bean_populated.setPropertyFloatObject(311423.98f);
							bean_populated.setPropertyInt(13);
							bean_populated.setPropertyLong(18753L);
							bean_populated.setPropertyShort((short)23);
							bean_populated.setPropertyBigDecimal(new BigDecimal("7653564654.426587"));
							Insert query_insert = new Insert(mDatasource);
							query_insert
								.into("tmp_beanmanager")
								.fields(bean_populated);
							assertEquals(manager.executeUpdate(query_insert), 1);

							// construct the select query
							Select query_select = new Select(mDatasource);
							query_select
								.from("tmp_beanmanager")
								.fields(BeanImpl.class);
							fetcher = new DbBeanFetcher<BeanImpl>(mDatasource, BeanImpl.class);

							BeanImpl		bean = null;
							DbStatement		statement = null;
							try
							{
								statement = manager.executeQuery(query_select);
								manager.fetch(statement.getResultSet(), fetcher);
								bean = fetcher.getBeanInstance();
								statement.close();
							}
							catch (DatabaseException e)
							{
								assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
							}
							assertNotNull(bean);
							assertEquals(bean.getPropertyString(), bean_populated.getPropertyString());
							assertEquals(bean.getPropertyStringbuffer().toString(), bean_populated.getPropertyStringbuffer().toString());
							assertEquals(bean.getPropertyDate().getTime(), bean_populated.getPropertyDate().getTime());
							assertEquals(bean.getPropertyCalendar(), bean_populated.getPropertyCalendar());
							assertEquals(bean.getPropertySqlDate().toString(), bean_populated.getPropertySqlDate().toString());
							assertEquals(bean.getPropertyTime().toString(), bean_populated.getPropertyTime().toString());
							assertEquals(bean.getPropertyTimestamp(), bean_populated.getPropertyTimestamp());
							assertEquals(bean.isPropertyBoolean(), bean_populated.isPropertyBoolean());
							assertEquals(bean.getPropertyChar(), bean_populated.getPropertyChar());
							assertEquals(bean.getPropertyByte(), bean_populated.getPropertyByte());
							assertEquals(bean.getPropertyDouble(), bean_populated.getPropertyDouble(), 0.01);
							assertEquals(bean.getPropertyFloat(), bean_populated.getPropertyFloat(), 0.01);
							assertEquals(bean.getPropertyDoubleObject().doubleValue(), bean_populated.getPropertyDoubleObject().doubleValue(), 0.01);
							assertEquals(bean.getPropertyFloatObject().floatValue(), bean_populated.getPropertyFloatObject().floatValue(), 0.01);
							assertEquals(bean.getPropertyInt(), bean_populated.getPropertyInt());
							assertEquals(bean.getPropertyLong(), bean_populated.getPropertyLong());
							assertEquals(bean.getPropertyShort(), bean_populated.getPropertyShort());
							assertEquals(bean.getPropertyBigDecimal(), bean_populated.getPropertyBigDecimal());
						}
						catch (DatabaseException e)
						{
							assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
							throw e;
						}
						
						return fetcher;
					}
				});
		}
		finally
		{
			// remove the temporary table
			DropTable query_drop = new DropTable(mDatasource);
			query_drop.table(query_create.getTable());
			manager.executeUpdate(query_drop);
		}

		assertNotNull(fetcher.getBeanInstance());
	}

	public void testFetchNullBean()
	throws Exception
	{
		final DbQueryManager manager = new DbQueryManager(mDatasource);
		
		// create the temporary table
		CreateTable query_create = new CreateTable(mDatasource);
		query_create.table("tmp_beanmanager")
			.columns(BeanImpl.class)
			.precision("propertyString", 255)
			.precision("propertyStringbuffer", 255)
			.precision("propertyChar", 1)
			.precision("propertyDouble", 5, 4)
			.precision("propertyFloat", 5, 5)
			.precision("propertyDoubleObject", 5, 4)
			.precision("propertyFloatObject", 5, 5)
			.precision("propertyBigDecimal", 16, 6);
		
		
		DbBeanFetcher<BeanImpl> fetcher = null;
		try
		{
			manager.executeUpdate(query_create);
			
			fetcher = manager.inTransaction(new DbTransactionUser() {
					public DbBeanFetcher<BeanImpl> useTransaction()
					throws InnerClassException
					{
						DbBeanFetcher<BeanImpl>	fetcher = null;
						try
						{
							BeanImpl bean_null = new BeanImpl();
							Insert query_insert = new Insert(mDatasource);
							query_insert
								.into("tmp_beanmanager")
								.fields(bean_null);
							assertEquals(manager.executeUpdate(query_insert), 1);

							// construct the select query
							Select query_select = new Select(mDatasource);
							query_select
								.from("tmp_beanmanager")
								.fields(BeanImpl.class);
							fetcher = new DbBeanFetcher<BeanImpl>(mDatasource, BeanImpl.class);

							BeanImpl		bean = null;
							DbStatement		statement = null;
							try
							{
								statement = manager.executeQuery(query_select);
								manager.fetch(statement.getResultSet(), fetcher);
								bean = fetcher.getBeanInstance();
								statement.close();
							}
							catch (DatabaseException e)
							{
								assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
							}
							assertNotNull(bean);
							assertEquals(bean.getPropertyString(), bean_null.getPropertyString());
							assertEquals(bean.getPropertyStringbuffer(), bean_null.getPropertyStringbuffer());
							assertEquals(bean.getPropertyDate(), bean_null.getPropertyDate());
							assertEquals(bean.getPropertyCalendar(), bean_null.getPropertyCalendar());
							assertEquals(bean.getPropertySqlDate(), bean_null.getPropertySqlDate());
							assertEquals(bean.getPropertyTime(), bean_null.getPropertyTime());
							assertEquals(bean.getPropertyTimestamp(), bean_null.getPropertyTimestamp());
							assertEquals(bean.isPropertyBoolean(), bean_null.isPropertyBoolean());
							assertEquals(bean.getPropertyChar(), bean_null.getPropertyChar());
							assertEquals(bean.getPropertyByte(), bean_null.getPropertyByte());
							assertEquals(bean.getPropertyDouble(), bean_null.getPropertyDouble(), 0.01);
							assertEquals(bean.getPropertyFloat(), bean_null.getPropertyFloat(), 0.01);
							assertEquals(bean.getPropertyDoubleObject(), bean_null.getPropertyDoubleObject());
							assertEquals(bean.getPropertyFloatObject(), bean_null.getPropertyFloatObject());
							assertEquals(bean.getPropertyInt(), bean_null.getPropertyInt());
							assertEquals(bean.getPropertyLong(), bean_null.getPropertyLong());
							assertEquals(bean.getPropertyShort(), bean_null.getPropertyShort());
							assertEquals(bean.getPropertyBigDecimal(), bean_null.getPropertyBigDecimal());
						}
						catch (DatabaseException e)
						{
							assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
							throw e;
						}
						
						return fetcher;
					}
				});
		}
		finally
		{
			// remove the temporary table
			DropTable query_drop = new DropTable(mDatasource);
			query_drop.table(query_create.getTable());
			manager.executeUpdate(query_drop);
		}

		assertNotNull(fetcher.getBeanInstance());
	}
}
