/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSelectDerby.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;

import com.uwyn.rife.database.BeanImpl;
import com.uwyn.rife.database.BeanImplConstrained;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.exceptions.TableNameOrFieldsRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;

public class TestSelectDerby extends TestSelect
{
	public TestSelectDerby(String name)
	{
		super(name);
	}

	public void testInstantiationDerby()
	{
		Select query = new Select(mDerby);
		assertNotNull(query);
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameOrFieldsRequiredException e)
		{
			assertEquals(e.getQueryName(), "Select");
		}
	}

	public void testIncompleteQueryDerby()
	{
		Select query = new Select(mDerby);
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameOrFieldsRequiredException e)
		{
			assertEquals(e.getQueryName(), "Select");
		}
		query.from("tablename");
		assertNotNull(query.getSql());

		query = new Select(mDerby);
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameOrFieldsRequiredException e)
		{
			assertEquals(e.getQueryName(), "Select");
		}
		query.field("field");
		assertNotNull(query.getSql());
	}

	public void testClearDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename");
		assertNotNull(query.getSql());
		query.clear();
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameOrFieldsRequiredException e)
		{
			assertEquals(e.getQueryName(), "Select");
		}
	}

	public void testBasicDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename");
		assertEquals(query.getSql(), "SELECT * FROM tablename");
		assertTrue(execute(query));
	}

	public void testHintDerby()
	{
		Select query = new Select(mDerby);
		query
			.hint("NO_INDEX")
			.from("tablename");
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
	}

	public void testOrderByAscendingDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.orderBy("propertyInt", Select.ASC);
		assertEquals(query.getSql(), "SELECT * FROM tablename ORDER BY propertyInt ASC");
		assertTrue(execute(query));
	}

	public void testOrderByDescendingDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.orderBy("propertyInt", Select.DESC);
		assertEquals(query.getSql(), "SELECT * FROM tablename ORDER BY propertyInt DESC");
		assertTrue(execute(query));
	}

	public void testBeanDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.fields(BeanImpl.class);
		assertEquals(query.getSql(), "SELECT propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyCalendar, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyEnum, propertyFloat, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShort, propertyShortObject, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp FROM tablename");
		assertTrue(execute(query));
	}

	public void testBeanConstrainedDerby()
	{
		Select query = new Select(mDerby, BeanImplConstrained.class);
		query.from("tablename");
		assertEquals(query.getSql(), "SELECT * FROM tablename ORDER BY propertyString ASC, propertyInt DESC");
		assertTrue(execute(query));

		query = new Select(mDerby, BeanImplConstrained.class);
		query.from("tablename")
			.orderBy("propertyByte");
		assertEquals(query.getSql(), "SELECT * FROM tablename ORDER BY propertyByte ASC");
		assertTrue(execute(query));
	}

	public void testBeanExcludedDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.fieldsExcluded(BeanImpl.class, new String[] {"propertyCalendar", "propertyFloat", "propertyShort"});
		assertEquals(query.getSql(), "SELECT propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyEnum, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShortObject, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp FROM tablename");
		assertTrue(execute(query));
	}

	public void testBeanTableDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.fields("tablename", BeanImpl.class);
		assertEquals(query.getSql(), "SELECT tablename.propertyBigDecimal, tablename.propertyBoolean, tablename.propertyBooleanObject, tablename.propertyByte, tablename.propertyByteObject, tablename.propertyCalendar, tablename.propertyChar, tablename.propertyCharacterObject, tablename.propertyDate, tablename.propertyDouble, tablename.propertyDoubleObject, tablename.propertyEnum, tablename.propertyFloat, tablename.propertyFloatObject, tablename.propertyInt, tablename.propertyIntegerObject, tablename.propertyLong, tablename.propertyLongObject, tablename.propertyShort, tablename.propertyShortObject, tablename.propertySqlDate, tablename.propertyString, tablename.propertyStringbuffer, tablename.propertyTime, tablename.propertyTimestamp FROM tablename");
		assertTrue(execute(query));
	}

	public void testBeanExcludedTableDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.fieldsExcluded("tablename", BeanImpl.class, "propertyCalendar", "propertyFloat", "propertyShort");
		assertEquals(query.getSql(), "SELECT tablename.propertyBigDecimal, tablename.propertyBoolean, tablename.propertyBooleanObject, tablename.propertyByte, tablename.propertyByteObject, tablename.propertyChar, tablename.propertyCharacterObject, tablename.propertyDate, tablename.propertyDouble, tablename.propertyDoubleObject, tablename.propertyEnum, tablename.propertyFloatObject, tablename.propertyInt, tablename.propertyIntegerObject, tablename.propertyLong, tablename.propertyLongObject, tablename.propertyShortObject, tablename.propertySqlDate, tablename.propertyString, tablename.propertyStringbuffer, tablename.propertyTime, tablename.propertyTimestamp FROM tablename");
		assertTrue(execute(query));
	}

	public void testWhereTypedDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename");

		Calendar cal = Calendar.getInstance();
		cal.set(2003, 2, 3, 10, 1, 28);
		cal.set(Calendar.MILLISECOND, 154);

		query
			.where("propertyBigDecimal", ">=", new BigDecimal("53443433.9784567"))
			.whereAnd("propertyBoolean", "=", false)
			.whereOr("propertyByte", "=", (byte)54)
			.whereAnd("propertyCalendar", "<=", cal)
			.whereOr("propertyChar", "=", 'f')
			.whereAnd("propertyDate", "=", cal.getTime())
			.whereAnd("propertyDouble", "!=", 73453.71d)
			.whereOr("propertyFloat", ">=", 1987.14f)
			.whereAnd("propertyInt", "=", 973)
			.whereAnd("propertyLong", "<", 347678L)
			.whereAnd("propertyShort", "=", (short)78)
			.whereOr("propertySqlDate", "=", new java.sql.Date(cal.getTime().getTime()))
			.whereAnd("propertyString", "LIKE", "someotherstring%")
			.whereAnd("propertyStringbuffer", "=", new StringBuffer("someotherstringbuff"))
			.whereOr("propertyTime", "=", new Time(cal.getTime().getTime()))
			.whereAnd("propertyTimestamp", "<=", new Timestamp(cal.getTime().getTime()));

		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBigDecimal >= 53443433.9784567 AND propertyBoolean = 0 OR propertyByte = 54 AND propertyCalendar <= '2003-03-03 10:01:28.154' OR propertyChar = 'f' AND propertyDate = '2003-03-03 10:01:28.154' AND propertyDouble != 73453.71 OR propertyFloat >= 1987.14 AND propertyInt = 973 AND propertyLong < 347678 AND propertyShort = 78 OR propertySqlDate = '2003-03-03' AND propertyString LIKE 'someotherstring%' AND propertyStringbuffer = 'someotherstringbuff' OR propertyTime = '10:01:28' AND propertyTimestamp <= '2003-03-03 10:01:28.154'");
		assertFalse(execute(query));
	}

	public void testWhereTypedMixedDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename");

		final Calendar cal = Calendar.getInstance();
		cal.set(2003, 2, 3, 10, 1, 28);
		cal.set(Calendar.MILLISECOND, 154);

		query
			.where("propertyBigDecimal", ">=", new BigDecimal("53443433.9784567"))
			.whereAnd("propertyBoolean", "=", false)
			.whereOr("propertyByte = 54")
			.whereAnd("propertyCalendar", "<=", cal)
			.whereOr("propertyChar", "=", 'f')
			.whereAnd("propertyDate", "=", cal.getTime())
			.whereAnd("propertyDouble", "!=", 73453.71d)
			.whereOr("propertyFloat >= 1987.14")
			.whereAnd("propertyInt", "=", 973)
			.whereAnd("propertyLong", "<", 347678L)
			.whereAnd("propertyShort", "=", (short)78)
			.whereParameterOr("propertySqlDate", "=")
			.whereAnd("propertyString", "LIKE", "someotherstring%")
			.whereAnd("propertyStringbuffer", "=", new StringBuffer("someotherstringbuff"))
			.whereOr("propertyTime", "=", new Time(cal.getTime().getTime()))
			.whereAnd("propertyTimestamp", "<=", new Timestamp(cal.getTime().getTime()));

		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBigDecimal >= 53443433.9784567 AND propertyBoolean = 0 OR propertyByte = 54 AND propertyCalendar <= '2003-03-03 10:01:28.154' OR propertyChar = 'f' AND propertyDate = '2003-03-03 10:01:28.154' AND propertyDouble != 73453.71 OR propertyFloat >= 1987.14 AND propertyInt = 973 AND propertyLong < 347678 AND propertyShort = 78 OR propertySqlDate = ? AND propertyString LIKE 'someotherstring%' AND propertyStringbuffer = 'someotherstringbuff' OR propertyTime = '10:01:28' AND propertyTimestamp <= '2003-03-03 10:01:28.154'");

		assertFalse(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setDate("propertySqlDate", new java.sql.Date(cal.getTime().getTime()));
				}
			}));
	}

	public void testWhereParametersDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename");

		assertNull(query.getParameters());

		query.whereParameter("propertyInt", "=")
			.whereParameterAnd("propertyLong", "<")
			.whereParameterOr("propertyChar", "=");

		assertEquals(query.getParameters().getOrderedNames().size(), 3);
		assertEquals(query.getParameters().getOrderedNames().get(0), "propertyInt");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyLong");
		assertEquals(query.getParameters().getOrderedNames().get(2), "propertyChar");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {"propertyInt", "propertyLong", "propertyChar"}));

		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyInt = ? AND propertyLong < ? OR propertyChar = ?");
		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setInt(1, 545)
						.setLong(2, 50000)
						.setString(3, "v");
				}
			}));

		query.where("propertyInt = 545");

		assertNull(query.getParameters());
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyInt = 545");
	}

	public void testWhereParametersMixedDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.where("propertyInt = 545")
			.whereParameterAnd("propertyLong", "<")
			.whereParameterOr("propertyChar", "=");

		assertEquals(query.getParameters().getOrderedNames().get(0), "propertyLong");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyChar");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {"propertyLong", "propertyChar"}));

		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyInt = 545 AND propertyLong < ? OR propertyChar = ?");
		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setLong(1, 50000)
						.setString(2, "v");
				}
			}));
	}

	public void testWhereConstructionDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.where("propertyInt = 545")
			.whereAnd("propertyLong < 50000")
			.whereOr("propertyChar = 'v'");
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyInt = 545 AND propertyLong < 50000 OR propertyChar = 'v'");
		assertTrue(execute(query));
	}

	public void testWhereConstructionGroupDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.startWhere()
				.where("propertyInt", "=", 545)
				.whereAnd("propertyByte", "=", 89)
			.end()
			.whereAnd("propertyLong < 50000")
			.startWhereOr()
				.whereParameter("propertyString",  "=")
				.whereAnd("propertyByte", "<=", (byte)0)
				.startWhereAnd()
					.where("propertyBoolean", "!=", true)
					.whereParameterOr("propertyStringbuffer", "LIKE")
				.end()
			.end()
			.whereOr("propertyChar = 'v'");

		assertEquals(query.getParameters().getOrderedNames().get(0), "propertyString");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyStringbuffer");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {"propertyString", "propertyStringbuffer"}));

		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE (propertyInt = 545 AND propertyByte = 89) AND propertyLong < 50000 OR (propertyString = ? AND propertyByte <= 0 AND (propertyBoolean != 1 OR propertyStringbuffer LIKE ?)) OR propertyChar = 'v'");

		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setString("propertyString", "someotherstring")
						.setString("propertyStringbuffer", "stringbuff");
				}
			}));
	}

	public void testWhereBeanDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.where(BeanImpl.getPopulatedBean());
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBigDecimal = 219038743.392874 AND propertyBoolean = 1 AND propertyBooleanObject = 0 AND propertyByte = 89 AND propertyByteObject = 34 AND propertyCalendar = '2002-06-18 15:26:14.764' AND propertyChar = 'v' AND propertyCharacterObject = 'r' AND propertyDate = '2002-06-18 15:26:14.764' AND propertyDouble = 53348.34 AND propertyDoubleObject = 143298.692 AND propertyEnum = 'VALUE_THREE' AND propertyFloat = 98634.2 AND propertyFloatObject = 8734.7 AND propertyInt = 545 AND propertyIntegerObject = 968 AND propertyLong = 34563 AND propertyLongObject = 66875 AND propertyShort = 43 AND propertyShortObject = 68 AND propertySqlDate = '2002-06-18' AND propertyString = 'someotherstring' AND propertyStringbuffer = 'someotherstringbuff' AND propertyTime = '15:26:14' AND propertyTimestamp = '2002-06-18 15:26:14.764'");
		assertTrue(execute(query));
	}

	public void testWhereBeanConstrainedDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.where(BeanImplConstrained.getPopulatedBean());
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBigDecimal = 219038743.392874 AND propertyBoolean = 1 AND propertyBooleanObject = 0 AND propertyByte = 89 AND propertyByteObject = 34 AND propertyCalendar = '2002-06-18 15:26:14.764' AND propertyChar = 'v' AND propertyCharacterObject = 'r' AND propertyDate = '2002-06-18 15:26:14.764' AND propertyDouble = 53348.34 AND propertyDoubleObject = 143298.692 AND propertyFloat = 98634.2 AND propertyFloatObject = 8734.7 AND propertyInt = 545 AND propertyIntegerObject = 968 AND propertyLongObject = 66875 AND propertyShort = 43 AND propertySqlDate = '2002-06-18' AND propertyString = 'someotherstring' AND propertyStringbuffer = 'someotherstringbuff' AND propertyTime = '15:26:14' AND propertyTimestamp = '2002-06-18 15:26:14.764'");
		assertTrue(execute(query));
	}

	public void testWhereBeanNullValuesDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.where(BeanImpl.getNullBean());
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBoolean = 0 AND propertyBooleanObject = 0 AND propertyByte = 0 AND propertyByteObject = 0 AND propertyDouble = 0.0 AND propertyDoubleObject = 0.0 AND propertyFloat = 0.0 AND propertyFloatObject = 0.0 AND propertyInt = 0 AND propertyIntegerObject = 0 AND propertyLong = 0 AND propertyLongObject = 0 AND propertyShort = 0 AND propertyShortObject = 0");
		assertTrue(execute(query));
	}

	public void testWhereBeanIncludedDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.whereIncluded(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"});
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyByte = 89 AND propertyDouble = 53348.34 AND propertyShort = 43 AND propertyStringbuffer = 'someotherstringbuff' AND propertyTime = '15:26:14'");
		assertTrue(execute(query));
	}

	public void testWhereBeanExcludedDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.whereExcluded(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"});
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBigDecimal = 219038743.392874 AND propertyBoolean = 1 AND propertyBooleanObject = 0 AND propertyByteObject = 34 AND propertyCalendar = '2002-06-18 15:26:14.764' AND propertyChar = 'v' AND propertyCharacterObject = 'r' AND propertyDate = '2002-06-18 15:26:14.764' AND propertyDoubleObject = 143298.692 AND propertyEnum = 'VALUE_THREE' AND propertyFloat = 98634.2 AND propertyFloatObject = 8734.7 AND propertyInt = 545 AND propertyIntegerObject = 968 AND propertyLong = 34563 AND propertyLongObject = 66875 AND propertyShortObject = 68 AND propertySqlDate = '2002-06-18' AND propertyString = 'someotherstring' AND propertyTimestamp = '2002-06-18 15:26:14.764'");
		assertTrue(execute(query));
	}

	public void testWhereBeanFilteredDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.whereFiltered(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"}, new String[] {"propertyByte", "propertyShort", "propertyTime"});
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyDouble = 53348.34 AND propertyStringbuffer = 'someotherstringbuff'");
		assertTrue(execute(query));
	}

	public void testWhereParametersBeanDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.whereParameters(BeanImpl.class);
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBigDecimal = ? AND propertyBoolean = ? AND propertyBooleanObject = ? AND propertyByte = ? AND propertyByteObject = ? AND propertyCalendar = ? AND propertyChar = ? AND propertyCharacterObject = ? AND propertyDate = ? AND propertyDouble = ? AND propertyDoubleObject = ? AND propertyEnum = ? AND propertyFloat = ? AND propertyFloatObject = ? AND propertyInt = ? AND propertyIntegerObject = ? AND propertyLong = ? AND propertyLongObject = ? AND propertyShort = ? AND propertyShortObject = ? AND propertySqlDate = ? AND propertyString = ? AND propertyStringbuffer = ? AND propertyTime = ? AND propertyTimestamp = ?");
		
		assertEquals(query.getParameters().getOrderedNames().size(), 25);
		assertEquals(query.getParameters().getOrderedNames().get(0), "propertyBigDecimal");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyBoolean");
		assertEquals(query.getParameters().getOrderedNames().get(2), "propertyBooleanObject");
		assertEquals(query.getParameters().getOrderedNames().get(3), "propertyByte");
		assertEquals(query.getParameters().getOrderedNames().get(4), "propertyByteObject");
		assertEquals(query.getParameters().getOrderedNames().get(5), "propertyCalendar");
		assertEquals(query.getParameters().getOrderedNames().get(6), "propertyChar");
		assertEquals(query.getParameters().getOrderedNames().get(7), "propertyCharacterObject");
		assertEquals(query.getParameters().getOrderedNames().get(8), "propertyDate");
		assertEquals(query.getParameters().getOrderedNames().get(9), "propertyDouble");
		assertEquals(query.getParameters().getOrderedNames().get(10), "propertyDoubleObject");
		assertEquals(query.getParameters().getOrderedNames().get(11), "propertyEnum");
		assertEquals(query.getParameters().getOrderedNames().get(12), "propertyFloat");
		assertEquals(query.getParameters().getOrderedNames().get(13), "propertyFloatObject");
		assertEquals(query.getParameters().getOrderedNames().get(14), "propertyInt");
		assertEquals(query.getParameters().getOrderedNames().get(15), "propertyIntegerObject");
		assertEquals(query.getParameters().getOrderedNames().get(16), "propertyLong");
		assertEquals(query.getParameters().getOrderedNames().get(17), "propertyLongObject");
		assertEquals(query.getParameters().getOrderedNames().get(18), "propertyShort");
		assertEquals(query.getParameters().getOrderedNames().get(19), "propertyShortObject");
		assertEquals(query.getParameters().getOrderedNames().get(20), "propertySqlDate");
		assertEquals(query.getParameters().getOrderedNames().get(21), "propertyString");
		assertEquals(query.getParameters().getOrderedNames().get(22), "propertyStringbuffer");
		assertEquals(query.getParameters().getOrderedNames().get(23), "propertyTime");
		assertEquals(query.getParameters().getOrderedNames().get(24), "propertyTimestamp");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {"propertyBigDecimal", "propertyBoolean", "propertyBooleanObject", "propertyByte", "propertyByteObject", "propertyCalendar", "propertyChar", "propertyCharacterObject", "propertyDate", "propertyDouble", "propertyDoubleObject", "propertyEnum", "propertyFloat", "propertyFloatObject", "propertyInt", "propertyIntegerObject", "propertyLong", "propertyLongObject", "propertyShort", "propertyShortObject", "propertySqlDate", "propertyString", "propertyStringbuffer", "propertyTime", "propertyTimestamp"}));
		
		assertTrue(execute(query, new DbPreparedStatementHandler() {
						   public void setParameters(DbPreparedStatement statement)
						   {
							   Calendar cal = Calendar.getInstance();
							   cal.set(2002, 5, 18, 15, 26, 14);
							   cal.set(Calendar.MILLISECOND, 764);
							   statement
								   .setBigDecimal(1, new BigDecimal("219038743.392874"))
								   .setBoolean(2, true)
								   .setBoolean(3, false)
								   .setByte(4, (byte)89)
								   .setByte(5, (byte)34)
								   .setTimestamp(6, new java.sql.Timestamp(cal.getTime().getTime()))
								   .setString(7, "v")
								   .setString(8, "r")
								   .setTimestamp(9, new java.sql.Timestamp(cal.getTime().getTime()))
								   .setDouble(10, 53348.34d)
								   .setDouble(11, 143298.692d)
								   .setString(12, "VALUE_THREE")
								   .setDouble(13, 98634.2d)
								   .setDouble(14, 8734.7d)
								   .setInt(15, 545)
								   .setInt(16, 968)
								   .setLong(17, 34563L)
								   .setLong(18, 66875L)
								   .setShort(19, (short)43)
								   .setShort(20, (short)68)
								   .setDate(21, new java.sql.Date(cal.getTime().getTime()))
								   .setString(22, "someotherstring")
								   .setString(23, "someotherstringbuff")
								   .setTime(24, new Time(15, 26, 14))
								   .setTimestamp(25, new Timestamp(cal.getTime().getTime()));
						   }
					   }));
	}

	public void testWhereParametersBeanConstrainedDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.whereParameters(BeanImplConstrained.class);
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBigDecimal = ? AND propertyBoolean = ? AND propertyBooleanObject = ? AND propertyByte = ? AND propertyByteObject = ? AND propertyCalendar = ? AND propertyChar = ? AND propertyCharacterObject = ? AND propertyDate = ? AND propertyDouble = ? AND propertyDoubleObject = ? AND propertyFloat = ? AND propertyFloatObject = ? AND propertyInt = ? AND propertyIntegerObject = ? AND propertyLongObject = ? AND propertyShort = ? AND propertySqlDate = ? AND propertyString = ? AND propertyStringbuffer = ? AND propertyTime = ? AND propertyTimestamp = ?");

		assertEquals(query.getParameters().getOrderedNames().size(), 22);
		assertEquals(query.getParameters().getOrderedNames().get(0), "propertyBigDecimal");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyBoolean");
		assertEquals(query.getParameters().getOrderedNames().get(2), "propertyBooleanObject");
		assertEquals(query.getParameters().getOrderedNames().get(3), "propertyByte");
		assertEquals(query.getParameters().getOrderedNames().get(4), "propertyByteObject");
		assertEquals(query.getParameters().getOrderedNames().get(5), "propertyCalendar");
		assertEquals(query.getParameters().getOrderedNames().get(6), "propertyChar");
		assertEquals(query.getParameters().getOrderedNames().get(7), "propertyCharacterObject");
		assertEquals(query.getParameters().getOrderedNames().get(8), "propertyDate");
		assertEquals(query.getParameters().getOrderedNames().get(9), "propertyDouble");
		assertEquals(query.getParameters().getOrderedNames().get(10), "propertyDoubleObject");
		assertEquals(query.getParameters().getOrderedNames().get(11), "propertyFloat");
		assertEquals(query.getParameters().getOrderedNames().get(12), "propertyFloatObject");
		assertEquals(query.getParameters().getOrderedNames().get(13), "propertyInt");
		assertEquals(query.getParameters().getOrderedNames().get(14), "propertyIntegerObject");
		assertEquals(query.getParameters().getOrderedNames().get(15), "propertyLongObject");
		assertEquals(query.getParameters().getOrderedNames().get(16), "propertyShort");
		assertEquals(query.getParameters().getOrderedNames().get(17), "propertySqlDate");
		assertEquals(query.getParameters().getOrderedNames().get(18), "propertyString");
		assertEquals(query.getParameters().getOrderedNames().get(19), "propertyStringbuffer");
		assertEquals(query.getParameters().getOrderedNames().get(20), "propertyTime");
		assertEquals(query.getParameters().getOrderedNames().get(21), "propertyTimestamp");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {"propertyBigDecimal", "propertyBoolean", "propertyBooleanObject", "propertyByte", "propertyByteObject", "propertyCalendar", "propertyChar", "propertyCharacterObject", "propertyDate", "propertyDouble", "propertyDoubleObject", "propertyFloat", "propertyFloatObject", "propertyInt", "propertyIntegerObject", "propertyLongObject", "propertyShort", "propertySqlDate", "propertyString", "propertyStringbuffer", "propertyTime", "propertyTimestamp"}));

		// don't check if actual rows were returned, since Derby doesn't
		// match on the float
		execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					Calendar cal = Calendar.getInstance();
					cal.set(2002, 5, 18, 15, 26, 14);
					cal.set(Calendar.MILLISECOND, 764);
					statement
						.setBigDecimal(1, new BigDecimal("219038743.392874"))
						.setBoolean(2, true)
						.setBoolean(3, false)
						.setByte(4, (byte)89)
						.setByte(5, (byte)34)
						.setTimestamp(6, new java.sql.Timestamp(cal.getTime().getTime()))
						.setString(7, "v")
						.setString(8, "r")
						.setTimestamp(9, new java.sql.Timestamp(cal.getTime().getTime()))
						.setDouble(10, 53348.34d)
						.setDouble(11, 143298.692d)
						.setFloat(12, 98634.2f)
						.setFloat(13, 8734.7f)
						.setInt(14, 545)
						.setInt(15, 968)
						.setLong(16, 66875L)
						.setShort(17, (short)43)
						.setDate(18, new java.sql.Date(cal.getTime().getTime()))
						.setString(19, "someotherstring")
						.setString(20, "someotherstringbuff")
						.setTime(21, new Time(cal.getTime().getTime()))
						.setTimestamp(22, new Timestamp(cal.getTime().getTime()));
				}
			});
	}

	public void testWhereParametersBeanExcludedDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.whereParametersExcluded(BeanImpl.class,
				new String[] {"propertyBoolean", "propertyByte", "propertyChar",
							  "propertyDouble", "propertyInt", "propertyLong",
							  "propertySqlDate", "propertyStringbuffer", "propertyTimestamp"});
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBigDecimal = ? AND propertyBooleanObject = ? AND propertyByteObject = ? AND propertyCalendar = ? AND propertyCharacterObject = ? AND propertyDate = ? AND propertyDoubleObject = ? AND propertyEnum = ? AND propertyFloat = ? AND propertyFloatObject = ? AND propertyIntegerObject = ? AND propertyLongObject = ? AND propertyShort = ? AND propertyShortObject = ? AND propertyString = ? AND propertyTime = ?");

		assertEquals(query.getParameters().getOrderedNames().size(), 16);
		assertEquals(query.getParameters().getOrderedNames().get(0), "propertyBigDecimal");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyBooleanObject");
		assertEquals(query.getParameters().getOrderedNames().get(2), "propertyByteObject");
		assertEquals(query.getParameters().getOrderedNames().get(3), "propertyCalendar");
		assertEquals(query.getParameters().getOrderedNames().get(4), "propertyCharacterObject");
		assertEquals(query.getParameters().getOrderedNames().get(5), "propertyDate");
		assertEquals(query.getParameters().getOrderedNames().get(6), "propertyDoubleObject");
		assertEquals(query.getParameters().getOrderedNames().get(7), "propertyEnum");
		assertEquals(query.getParameters().getOrderedNames().get(8), "propertyFloat");
		assertEquals(query.getParameters().getOrderedNames().get(9), "propertyFloatObject");
		assertEquals(query.getParameters().getOrderedNames().get(10), "propertyIntegerObject");
		assertEquals(query.getParameters().getOrderedNames().get(11), "propertyLongObject");
		assertEquals(query.getParameters().getOrderedNames().get(12), "propertyShort");
		assertEquals(query.getParameters().getOrderedNames().get(13), "propertyShortObject");
		assertEquals(query.getParameters().getOrderedNames().get(14), "propertyString");
		assertEquals(query.getParameters().getOrderedNames().get(15), "propertyTime");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {"propertyBigDecimal", "propertyBooleanObject", "propertyByteObject", "propertyCalendar", "propertyCharacterObject", "propertyDate", "propertyDoubleObject", "propertyEnum", "propertyFloat", "propertyFloatObject", "propertyIntegerObject", "propertyLongObject", "propertyShort", "propertyShortObject", "propertyString", "propertyTime"}));
		assertTrue(execute(query, new DbPreparedStatementHandler() {
						   public void setParameters(DbPreparedStatement statement)
						   {
							   Calendar cal = Calendar.getInstance();
							   cal.set(2002, 5, 18, 15, 26, 14);
							   cal.set(Calendar.MILLISECOND, 764);
							   statement
								   .setBigDecimal(1, new BigDecimal("219038743.392874"))
								   .setBoolean(2, false)
								   .setByte(3, (byte)34)
								   .setTimestamp(4, new java.sql.Timestamp(cal.getTime().getTime()))
								   .setString(5, "r")
								   .setTimestamp(6, new java.sql.Timestamp(cal.getTime().getTime()))
								   .setDouble(7, 143298.692d)
								   .setString(8, "VALUE_THREE")
								   .setDouble(9, 98634.2d)
								   .setDouble(10, 8734.7d)
								   .setInt(11, 968)
								   .setLong(12, 66875L)
								   .setShort(13, (short)43)
								   .setShort(14, (short)68)
								   .setString(15, "someotherstring")
								   .setTime(16, new Time(15, 26, 14));
						   }
					   }));
	}

	public void testDistinctDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.distinct()
			.where("propertyByte = 89")
			.orderBy("propertyDouble")
			.orderBy("propertyShort")
			.orderBy("propertyTime");
		assertEquals(query.getSql(), "SELECT DISTINCT * FROM tablename WHERE propertyByte = 89 ORDER BY propertyDouble ASC, propertyShort ASC, propertyTime ASC");
		assertTrue(execute(query));
	}

	public void testDistinctOnDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.distinctOn("propertyDouble")
			.distinctOn("propertyShort")
			.distinctOn("propertyTime")
			.where("propertyByte = 89")
			.orderBy("propertyDouble")
			.orderBy("propertyShort")
			.orderBy("propertyTime");
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
	}

	public void testComplexDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.field("field1")
			.field("field2")
			.field("field3")
			.join("table2")
			.join("table3")
			.where("this = that")
			.groupBy("gexpr1")
			.groupBy("gexpr2")
			.having("hexpr1")
			.having("hexpr2")
			.distinct()
			.unionAll("uexpr1")
			.union("uexpr2");
//			.limit(3)
//			.offset(1);
		assertEquals(query.getSql(), "SELECT DISTINCT field1, field2, field3 FROM tablename, table2, table3 WHERE this = that GROUP BY gexpr1, gexpr2 HAVING hexpr1, hexpr2 UNION ALL uexpr1 UNION uexpr2");
	}

	public void testGroupByBeanDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.fields(BeanImpl.class)
			.groupBy(BeanImpl.class);
		assertEquals(query.getSql(), "SELECT propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyCalendar, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyEnum, propertyFloat, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShort, propertyShortObject, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp FROM tablename GROUP BY propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyCalendar, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyEnum, propertyFloat, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShort, propertyShortObject, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp");
		assertTrue(execute(query));
	}

	public void testGroupByBeanExcludedDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.fieldsExcluded(BeanImpl.class, new String[] {"propertyCalendar", "propertyFloat", "propertyShort"})
			.groupByExcluded(BeanImpl.class, new String[] {"propertyCalendar", "propertyFloat", "propertyShort"});
		assertEquals(query.getSql(), "SELECT propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyEnum, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShortObject, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp FROM tablename GROUP BY propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyEnum, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShortObject, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp");
		assertTrue(execute(query));
	}

	public void testJoinDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.join("table2")
			.join("table3");
		assertEquals(query.getSql(), "SELECT * FROM tablename, table2, table3");
		assertTrue(execute(query));
	}

	public void testJoinCustomDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.joinCustom("INNER JOIN table3 ON (tablename.propertyInt = table3.propertyInt)")
			.joinCustom("LEFT OUTER JOIN table2 ON (table3.propertyInt = table2.propertyInt)");
		assertEquals(query.getSql(), "SELECT * FROM tablename INNER JOIN table3 ON (tablename.propertyInt = table3.propertyInt) LEFT OUTER JOIN table2 ON (table3.propertyInt = table2.propertyInt)");
		assertTrue(execute(query));
	}

	public void testJoinCrossDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.joinCross("table2")
			.joinCross("table3");
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
	}

	public void testJoinInnerDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.joinInner("table2", Select.NATURAL, null);
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
		query.clear();
		query.from("tablename")
			.joinInner("table2", Select.ON, "tablename.propertyInt = table2.propertyInt");
		assertEquals(query.getSql(), "SELECT * FROM tablename INNER JOIN table2 ON (tablename.propertyInt = table2.propertyInt)");
		assertTrue(execute(query));
		query.clear();
		query.from("tablename")
			.joinInner("table2", Select.USING, "propertyInt");
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
	}

	public void testJoinOuterDerby()
	{
		Select query = new Select(mDerby);

		query.from("tablename")
			.joinOuter("table2", Select.FULL, Select.NATURAL, null);
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
		query.clear();
		query.from("tablename")
			.joinOuter("table2", Select.LEFT, Select.NATURAL, null);
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
		query.clear();
		query.from("tablename")
			.joinOuter("table2", Select.RIGHT, Select.NATURAL, null);
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
		query.clear();

		query.from("tablename")
			.joinOuter("table2", Select.FULL, Select.ON, "tablename.propertyInt = table2.propertyInt");
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
		query.clear();
		query.from("tablename")
			.joinOuter("table2", Select.LEFT, Select.ON, "tablename.propertyInt = table2.propertyInt");
		assertEquals(query.getSql(), "SELECT * FROM tablename LEFT OUTER JOIN table2 ON (tablename.propertyInt = table2.propertyInt)");
		assertTrue(execute(query));
		query.clear();
		query.from("tablename")
			.joinOuter("table2", Select.RIGHT, Select.ON, "tablename.propertyInt = table2.propertyInt");
		assertEquals(query.getSql(), "SELECT * FROM tablename RIGHT OUTER JOIN table2 ON (tablename.propertyInt = table2.propertyInt)");
		assertTrue(execute(query));
		query.clear();

		query.from("tablename")
			.joinOuter("table2", Select.FULL, Select.USING, "propertyInt");
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
		query.clear();
		query.from("tablename")
			.joinOuter("table2", Select.LEFT, Select.USING, "propertyInt");
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
		query.clear();

		query.from("tablename")
			.joinOuter("table2", Select.RIGHT, Select.USING, "propertyInt");
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
		query.clear();
	}

	public void testLimitDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.limit(3);
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
		query.offset(1);
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}
		query.clear();
		query.from("tablename")
			.offset(10);
		assertEquals(query.getSql(), "SELECT * FROM tablename");
		assertTrue(execute(query));
	}

	public void testLimitParameterDerby()
	{
		Select query = new Select(mDerby);
		query.from("tablename")
			.limitParameter("limit");
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}

		query.offsetParameter("offset");
		try
		{
			query.getSql();
			fail();
		}
		catch (UnsupportedSqlFeatureException e)
		{
			assertTrue(true);
		}

		query.clear();
		query.from("tablename")
			.offsetParameter("offset");
		assertEquals(query.getSql(), "SELECT * FROM tablename");
		assertTrue(execute(query));
	}

	public void testSubselectParamsDerby()
	{
		Select fieldquery = new Select(mDerby);
		fieldquery
			.from("table2")
			.field("max(propertyLong)")
			.whereParameter("propertyInt", ">");
		Select tablequery = new Select(mDerby);
		tablequery
			.from("table2")
			.whereParameter("propertyLong", "<");
		Select wherequery = new Select(mDerby);
		wherequery
			.from("table3")
			.field("max(propertyShort)")
			.whereParameter("propertyShort", "!=");
		Select unionquery1 = new Select(mDerby);
		unionquery1
			.from("table2")
			.field("propertyString")
			.field("max(propertyByte)")
			.whereParameter("propertyByte", "=")
			.groupBy("propertyString");
		Select unionquery2 = new Select(mDerby);
		unionquery2
			.from("table2")
			.field("propertyStringbuffer")
			.field("min(propertyByte)")
			.whereParameter("propertyByte", ">")
			.groupBy("propertyStringbuffer");

		// Manual subselect creation
		Select query = new Select(mDerby);
		// shuffled the structure around a bit to test the correct order usage
		query
			.unionAll(unionquery1)
			.union(unionquery2)
			.where("tablename.propertyShort >= ("+wherequery+")")
			.whereSubselect(wherequery)
			.whereParameterOr("tablename.propertyString", "propertyString", "=")
			.from("tablename")
			.join("("+tablequery+") AS tablesubselect")
			.tableSubselect(tablequery)
			.field("tablename.propertyString")
			.field("("+fieldquery+") AS propertyLong")
			.fieldSubselect(fieldquery);
		assertEquals(query.getSql(), "SELECT tablename.propertyString, (SELECT max(propertyLong) FROM table2 WHERE propertyInt > ?) AS propertyLong FROM tablename, (SELECT * FROM table2 WHERE propertyLong < ?) AS tablesubselect WHERE tablename.propertyShort >= (SELECT max(propertyShort) FROM table3 WHERE propertyShort != ?) OR tablename.propertyString = ? UNION ALL SELECT propertyString, max(propertyByte) FROM table2 WHERE propertyByte = ? GROUP BY propertyString UNION SELECT propertyStringbuffer, min(propertyByte) FROM table2 WHERE propertyByte > ? GROUP BY propertyStringbuffer");
		String[] parameters = query.getParameters().getOrderedNamesArray();
		assertEquals(6, parameters.length);
		assertEquals(parameters[0], "propertyInt");
		assertEquals(parameters[1], "propertyLong");
		assertEquals(parameters[2], "propertyShort");
		assertEquals(parameters[3], "propertyString");
		assertEquals(parameters[4], "propertyByte");
		assertEquals(parameters[5], "propertyByte");

		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setInt("propertyInt", 1)
						.setLong("propertyLong", 99999999)
						.setShort("propertyShort", (short)5)
						.setString("propertyString", "thestring")
						.setByte("propertyByte", (byte)4);
				}
			}));

		//Automated subselect creation
		query = new Select(mDerby);
		query
			.union(unionquery1)
			.union(unionquery2)
			.where("tablename.propertyShort", ">=", wherequery)
			.whereParameterOr("tablename.propertyString", "propertyString", "=")
			.whereOr("tablename.propertyFloat", ">", new Select(mDerby)
				.from("table3")
				.field("max(propertyLong)")
				.whereParameter("propertyLong", "!="))
			.from("tablename", new Select(mDerby)
				.from("tablename"))
			.join("tablesubselect", tablequery)
			.whereAnd("tablename.propertyDouble", "<=", new Select(mDerby)
				.from("table2")
				.field("max(propertyFloat)")
				.whereParameter("propertyFloat", "!="))
			.field("tablename.propertyString")
			.field("propertyLong", fieldquery);
		assertEquals(query.getSql(), "SELECT tablename.propertyString, (SELECT max(propertyLong) FROM table2 WHERE propertyInt > ?) AS propertyLong FROM (SELECT * FROM tablename) tablename, (SELECT * FROM table2 WHERE propertyLong < ?) tablesubselect WHERE tablename.propertyShort >= (SELECT max(propertyShort) FROM table3 WHERE propertyShort != ?) OR tablename.propertyString = ? OR tablename.propertyFloat > (SELECT max(propertyLong) FROM table3 WHERE propertyLong != ?) AND tablename.propertyDouble <= (SELECT max(propertyFloat) FROM table2 WHERE propertyFloat != ?) UNION SELECT propertyString, max(propertyByte) FROM table2 WHERE propertyByte = ? GROUP BY propertyString UNION SELECT propertyStringbuffer, min(propertyByte) FROM table2 WHERE propertyByte > ? GROUP BY propertyStringbuffer");
		parameters = query.getParameters().getOrderedNamesArray();
		assertEquals(8, parameters.length);
		assertEquals(parameters[0], "propertyInt");
		assertEquals(parameters[1], "propertyLong");
		assertEquals(parameters[2], "propertyShort");
		assertEquals(parameters[3], "propertyString");
		assertEquals(parameters[4], "propertyLong");
		assertEquals(parameters[5], "propertyFloat");
		assertEquals(parameters[6], "propertyByte");
		assertEquals(parameters[7], "propertyByte");

		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setInt("propertyInt", 1)
						.setLong("propertyLong", 99999999)
						.setShort("propertyShort", (short)5)
						.setString("propertyString", "thestring")
						.setFloat("propertyFloat", -1f)
						.setByte("propertyByte", (byte)4);
				}
			}));
	}

	public void testCloneDerby()
	{
		Select fieldquery = new Select(mDerby);
		fieldquery
			.from("table2")
			.field("max(propertyLong)")
			.whereParameter("propertyInt", ">");
		Select tablequery = new Select(mDerby);
		tablequery
			.from("table2")
			.whereParameter("propertyLong", "<");
		Select wherequery = new Select(mDerby);
		wherequery
			.from("table3")
			.field("max(propertyShort)")
			.whereParameter("propertyShort", "!=");
		Select unionquery1 = new Select(mDerby);
		unionquery1
			.from("table2")
			.field("propertyString")
			.field("max(propertyByte)")
			.whereParameter("propertyByte", "=")
			.groupBy("propertyString");
		Select unionquery2 = new Select(mDerby);
		unionquery2
			.from("table2")
			.field("propertyStringbuffer")
			.field("min(propertyByte)")
			.whereParameter("propertyByte", ">")
			.groupBy("propertyStringbuffer");
		Select query = new Select(mDerby);
		query
			.from("tablename")
			.join("("+tablequery+") AS tablesubselect")
			.tableSubselect(tablequery)
			.join("table3")
			.joinOuter("table2", Select.RIGHT, Select.ON, "table3.propertyInt = table2.propertyInt")
			.distinct()
//			.distinctOn("tablename.propertyShort")
			.field("tablename.propertyString")
			.field("("+fieldquery+") AS propertyLong")
			.fieldSubselect(fieldquery)
			.where("tablename.propertyShort >= ("+wherequery+")")
			.whereSubselect(wherequery)
			.whereParameterOr("tablename.propertyString", "propertyString", "=")
			.whereOr("tablename.propertyByte", "=", (byte)54)
			.whereAnd("tablename.propertyDouble", "!=", 73453.71d)
			.whereParameterOr("tablename.propertyInt", "propertyInt", "=")
			.whereParameterAnd("tablename.propertyLong", "propertyLong", "<")
			.whereParameterOr("tablename.propertyChar", "propertyChar", "=")
//			.groupBy("tablename.propertyShort")
//			.groupBy("tablename.propertyLong")
//			.groupBy("tablename.propertyString")
//			.having("tablename.propertyLong = 1")
			.unionAll(unionquery1)
			.union(unionquery2);
//			.limit(3)
//			.offset(1);
		Select query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setInt("propertyInt", 1)
						.setLong("propertyLong", 99999999)
						.setShort("propertyShort", (short)5)
						.setString("propertyString", "thestring")
						.setByte("propertyByte", (byte)4)
						.setString("propertyChar", "c");
				}
			});
	}
}

