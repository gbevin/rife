/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCapabilities.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.exceptions.ExecutionErrorException;
import com.uwyn.rife.database.exceptions.UndefinedVirtualParameterException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.DropTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;

public class TestCapabilities extends TestCase
{
	private Datasource  mDatasource = null;
    
	public TestCapabilities(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}

	public void setUp()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		
		CreateTable createtable = new CreateTable(mDatasource);
		createtable.table("tablename")
			.columns(BeanImpl.class)
			.precision("propertyBigDecimal", 18, 9)
			.precision("propertyChar", 1)
			.precision("propertyDouble", 12, 3)
			.precision("propertyDoubleObject", 12, 3)
			.precision("propertyFloat", 13, 2)
			.precision("propertyFloatObject", 13, 2)
			.precision("propertyString", 255)
			.precision("propertyStringbuffer", 100);
		
		try
		{
			// prepare table and data
			manager.executeUpdate(createtable);
			
			Insert insert = new Insert(mDatasource);
			insert.into("tablename")
				.fields(BeanImpl.getPopulatedBean());
			manager.executeUpdate(insert);
			
			insert.clear();
			insert.into("tablename")
				.fields(BeanImpl.getNullBean());
			manager.executeUpdate(insert);
			
			BeanImpl impl = BeanImpl.getPopulatedBean();
			insert.clear();
			impl.setPropertyInt(3);
			insert.into("tablename")
				.fields(impl);
			manager.executeUpdate(insert);
			insert.clear();
			impl.setPropertyInt(4);
			insert.into("tablename")
				.fields(impl);
			manager.executeUpdate(insert);
			insert.clear();
			impl.setPropertyInt(5);
			insert.into("tablename")
				.fields(impl);
			manager.executeUpdate(insert);
		}
		catch (DatabaseException e)
		{
			tearDown();
			throw new RuntimeException(e);
		}
	}

	public void tearDown()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		
		// clean up nicely
		DropTable drop_table = new DropTable(mDatasource);
		try
		{
			drop_table.table("tablename");
			manager.executeUpdate(drop_table);
		}
		catch (DatabaseException e)
		{
			System.out.println(e.toString());
		}
	}

	public void testLimitOffset()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);

		final List<Integer> limit_ids = new ArrayList<Integer>();

		Select query = new Select(mDatasource);
		query.from("tablename")
			.orderBy("propertyInt")
			.limit(3);

		assertTrue(manager.executeFetchAll(query, new DbRowProcessor() {
				public boolean processRow(ResultSet resultSet)
				throws SQLException
				{
					limit_ids.add(resultSet.getInt("propertyInt"));
					return true;
				}
			}));
		assertEquals(3, limit_ids.size());
		assertEquals(0, limit_ids.get(0).intValue());
		assertEquals(3, limit_ids.get(1).intValue());
		assertEquals(4, limit_ids.get(2).intValue());

		final List<Integer> offset_ids = new ArrayList<Integer>();

		query.offset(1);

		assertTrue(manager.executeFetchAll(query, new DbRowProcessor() {
				public boolean processRow(ResultSet resultSet)
				throws SQLException
				{
					offset_ids.add(resultSet.getInt("propertyInt"));
					return true;
				}
			}));
		assertEquals(3, offset_ids.size());
		assertEquals(3, offset_ids.get(0).intValue());
		assertEquals(4, offset_ids.get(1).intValue());
		assertEquals(5, offset_ids.get(2).intValue());

		query.clear();

		final List<Integer> plain_ids = new ArrayList<Integer>();

		query.from("tablename")
			.orderBy("propertyInt")
			.offset(10);

		assertTrue(manager.executeFetchAll(query, new DbRowProcessor() {
				public boolean processRow(ResultSet resultSet)
				throws SQLException
				{
					plain_ids.add(resultSet.getInt("propertyInt"));
					return true;
				}
			}));
		assertEquals(5, plain_ids.size());
		assertEquals(0, plain_ids.get(0).intValue());
		assertEquals(3, plain_ids.get(1).intValue());
		assertEquals(4, plain_ids.get(2).intValue());
		assertEquals(5, plain_ids.get(3).intValue());
		assertEquals(545, plain_ids.get(4).intValue());
	}

	public void testLimitOffsetParameters()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);

		final List<Integer> limit_ids = new ArrayList<Integer>();
		
		Select query = new Select(mDatasource);
		query.from("tablename")
			.orderBy("propertyInt")
			.limitParameter("limit");
		
		assertTrue(manager.executeFetchAll(query, new DbRowProcessor() {
				public boolean processRow(ResultSet resultSet)
				throws SQLException
				{
					limit_ids.add(resultSet.getInt("propertyInt"));
					return true;
				}
			}, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setInt("limit", 3);
				}
			}));
		assertEquals(3, limit_ids.size());
		assertEquals(0, limit_ids.get(0).intValue());
		assertEquals(3, limit_ids.get(1).intValue());
		assertEquals(4, limit_ids.get(2).intValue());

		final List<Integer> offset_ids = new ArrayList<Integer>();

		query.offsetParameter("offset");

		assertTrue(manager.executeFetchAll(query, new DbRowProcessor() {
				public boolean processRow(ResultSet resultSet)
				throws SQLException
				{
					offset_ids.add(resultSet.getInt("propertyInt"));
					return true;
				}
			}, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setInt("limit", 3)
						.setInt("offset", 1);
				}
			}));
		assertEquals(3, offset_ids.size());
		assertEquals(3, offset_ids.get(0).intValue());
		assertEquals(4, offset_ids.get(1).intValue());
		assertEquals(5, offset_ids.get(2).intValue());

		query.clear();

		final List<Integer> plain_ids = new ArrayList<Integer>();

		query.from("tablename")
			.orderBy("propertyInt")
			.offsetParameter("offset");

		assertTrue(manager.executeFetchAll(query, new DbRowProcessor() {
				public boolean processRow(ResultSet resultSet)
				throws SQLException
				{
					plain_ids.add(resultSet.getInt("propertyInt"));
					return true;
				}
			}));
		assertEquals(5, plain_ids.size());
		assertEquals(0, plain_ids.get(0).intValue());
		assertEquals(3, plain_ids.get(1).intValue());
		assertEquals(4, plain_ids.get(2).intValue());
		assertEquals(5, plain_ids.get(3).intValue());
		assertEquals(545, plain_ids.get(4).intValue());
	}

	public void testLimitOffsetParametersMissing()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);

		Select query = new Select(mDatasource);
		query.from("tablename")
			.orderBy("propertyInt")
			.limitParameter("limit");
		
		try
		{
			manager.executeFetchAll(query, new DbRowProcessor() {
					public boolean processRow(ResultSet resultSet)
					throws SQLException
					{
						return true;
					}
				});
			assertTrue("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver()));	// hsqldb 1.8.0 doesn't throw an exception when no limit parameter is provided
		}
		catch (ExecutionErrorException e)
		{
			assertTrue(e.getCause() instanceof SQLException);
		}
		catch (UndefinedVirtualParameterException e)
		{
			assertEquals("limit", e.getParameterName());
		}

		query.offsetParameter("offset");
		
		try
		{
			manager.executeFetchAll(query, new DbRowProcessor() {
					public boolean processRow(ResultSet resultSet)
					throws SQLException
					{
						return true;
					}
				}, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("limit", 3);
					}
				});
			assertTrue("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver()));	// hsqldb 1.8.0 doesn't throw an exception when no offset parameter is provided
		}
		catch (ExecutionErrorException e)
		{
			assertTrue(e.getCause() instanceof SQLException);
		}
		catch (UndefinedVirtualParameterException e)
		{
			assertEquals("offset", e.getParameterName());
		}
	}
}
