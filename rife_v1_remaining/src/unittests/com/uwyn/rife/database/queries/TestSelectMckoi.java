/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSelectMckoi.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;

import com.uwyn.rife.database.*;
import com.uwyn.rife.database.exceptions.TableNameOrFieldsRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;

public class TestSelectMckoi extends TestSelect
{
	public TestSelectMckoi(String name)
	{
		super(name);
	}

	public void testInstantiationMckoi()
	{
		Select query = new Select(mMckoi);
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

	public void testIncompleteQueryMckoi()
	{
		Select query = new Select(mMckoi);
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

		query = new Select(mMckoi);
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

	public void testClearMckoi()
	{
		Select query = new Select(mMckoi);
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

	public void testBasicMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename");
		assertEquals(query.getSql(), "SELECT * FROM tablename");
		assertTrue(execute(query));
	}

	public void testHintMckoi()
	{
		Select query = new Select(mMckoi);
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

	public void testOrderByAscendingMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.orderBy("propertyInt", Select.ASC);
		assertEquals(query.getSql(), "SELECT * FROM tablename ORDER BY propertyInt ASC");
		assertTrue(execute(query));
	}

	public void testOrderByDescendingMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.orderBy("propertyInt", Select.DESC);
		assertEquals(query.getSql(), "SELECT * FROM tablename ORDER BY propertyInt DESC");
		assertTrue(execute(query));
	}

	public void testBeanMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.fields(BeanImpl.class);
		assertEquals(query.getSql(), "SELECT propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyCalendar, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyEnum, propertyFloat, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShort, propertyShortObject, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp FROM tablename");
		assertTrue(execute(query));
	}

	public void testBeanConstrainedMckoi()
	{
		Select query = new Select(mMckoi, BeanImplConstrained.class);
		query.from("tablename");
		assertEquals(query.getSql(), "SELECT * FROM tablename ORDER BY propertyString ASC, propertyInt DESC");
		assertTrue(execute(query));

		query = new Select(mMckoi, BeanImplConstrained.class);
		query.from("tablename")
			.orderBy("propertyByte");
		assertEquals(query.getSql(), "SELECT * FROM tablename ORDER BY propertyByte ASC");
		assertTrue(execute(query));
	}

	public void testBeanExcludedMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.fieldsExcluded(BeanImpl.class, new String[] {"propertyCalendar", "propertyFloat", "propertyShort"});
		assertEquals(query.getSql(), "SELECT propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyEnum, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShortObject, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp FROM tablename");
		assertTrue(execute(query));
	}

	public void testWhereTypedMckoi()
	{
		Select query = new Select(mMckoi);
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

		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBigDecimal >= 53443433.9784567 AND propertyBoolean = false OR propertyByte = 54 AND propertyCalendar <= TIMESTAMP '2003-03-03 10:01:28.154' OR propertyChar = 'f' AND propertyDate = TIMESTAMP '2003-03-03 10:01:28.154' AND propertyDouble != 73453.71 OR propertyFloat >= 1987.14 AND propertyInt = 973 AND propertyLong < 347678 AND propertyShort = 78 OR propertySqlDate = DATE '2003-03-03' AND propertyString LIKE 'someotherstring%' AND propertyStringbuffer = 'someotherstringbuff' OR propertyTime = TIME '10:01:28' AND propertyTimestamp <= TIMESTAMP '2003-03-03 10:01:28.154'");
		assertFalse(execute(query));
	}

	public void testWhereTypedMixedMckoi()
	{
		Select query = new Select(mMckoi);
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

		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBigDecimal >= 53443433.9784567 AND propertyBoolean = false OR propertyByte = 54 AND propertyCalendar <= TIMESTAMP '2003-03-03 10:01:28.154' OR propertyChar = 'f' AND propertyDate = TIMESTAMP '2003-03-03 10:01:28.154' AND propertyDouble != 73453.71 OR propertyFloat >= 1987.14 AND propertyInt = 973 AND propertyLong < 347678 AND propertyShort = 78 OR propertySqlDate = ? AND propertyString LIKE 'someotherstring%' AND propertyStringbuffer = 'someotherstringbuff' OR propertyTime = TIME '10:01:28' AND propertyTimestamp <= TIMESTAMP '2003-03-03 10:01:28.154'");

		assertFalse(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement.setDate("propertySqlDate", new java.sql.Date(cal.getTime().getTime()));
				}
			}));
	}

	public void testWhereParametersMckoi()
	{
		Select query = new Select(mMckoi);
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

	public void testWhereParametersMixedMckoi()
	{
		Select query = new Select(mMckoi);
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

	public void testWhereConstructionMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.where("propertyInt = 545")
			.whereAnd("propertyLong < 50000")
			.whereOr("propertyChar = 'v'");
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyInt = 545 AND propertyLong < 50000 OR propertyChar = 'v'");
		assertTrue(execute(query));
	}

	public void testWhereConstructionGroupMckoi()
	{
		Select query = new Select(mMckoi);
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

		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE (propertyInt = 545 AND propertyByte = 89) AND propertyLong < 50000 OR (propertyString = ? AND propertyByte <= 0 AND (propertyBoolean != true OR propertyStringbuffer LIKE ?)) OR propertyChar = 'v'");

		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setString("propertyString", "someotherstring")
						.setString("propertyStringbuffer", "stringbuff");
				}
			}));
	}

	public void testWhereBeanMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.where(BeanImpl.getPopulatedBean());
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBigDecimal = 219038743.392874 AND propertyBoolean = true AND propertyBooleanObject = false AND propertyByte = 89 AND propertyByteObject = 34 AND propertyCalendar = TIMESTAMP '2002-06-18 15:26:14.764' AND propertyChar = 'v' AND propertyCharacterObject = 'r' AND propertyDate = TIMESTAMP '2002-06-18 15:26:14.764' AND propertyDouble = 53348.34 AND propertyDoubleObject = 143298.692 AND propertyEnum = 'VALUE_THREE' AND propertyFloat = 98634.2 AND propertyFloatObject = 8734.7 AND propertyInt = 545 AND propertyIntegerObject = 968 AND propertyLong = 34563 AND propertyLongObject = 66875 AND propertyShort = 43 AND propertyShortObject = 68 AND propertySqlDate = DATE '2002-06-18' AND propertyString = 'someotherstring' AND propertyStringbuffer = 'someotherstringbuff' AND propertyTime = TIME '15:26:14' AND propertyTimestamp = TIMESTAMP '2002-06-18 15:26:14.764'");
		assertTrue(execute(query));
	}

	public void testWhereBeanConstrainedMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.where(BeanImplConstrained.getPopulatedBean());
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBigDecimal = 219038743.392874 AND propertyBoolean = true AND propertyBooleanObject = false AND propertyByte = 89 AND propertyByteObject = 34 AND propertyCalendar = TIMESTAMP '2002-06-18 15:26:14.764' AND propertyChar = 'v' AND propertyCharacterObject = 'r' AND propertyDate = TIMESTAMP '2002-06-18 15:26:14.764' AND propertyDouble = 53348.34 AND propertyDoubleObject = 143298.692 AND propertyFloat = 98634.2 AND propertyFloatObject = 8734.7 AND propertyInt = 545 AND propertyIntegerObject = 968 AND propertyLongObject = 66875 AND propertyShort = 43 AND propertySqlDate = DATE '2002-06-18' AND propertyString = 'someotherstring' AND propertyStringbuffer = 'someotherstringbuff' AND propertyTime = TIME '15:26:14' AND propertyTimestamp = TIMESTAMP '2002-06-18 15:26:14.764'");
		assertTrue(execute(query));
	}

	public void testWhereBeanNullValuesMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.where(BeanImpl.getNullBean());
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBoolean = false AND propertyBooleanObject = false AND propertyByte = 0 AND propertyByteObject = 0 AND propertyDouble = 0.0 AND propertyDoubleObject = 0.0 AND propertyFloat = 0.0 AND propertyFloatObject = 0.0 AND propertyInt = 0 AND propertyIntegerObject = 0 AND propertyLong = 0 AND propertyLongObject = 0 AND propertyShort = 0 AND propertyShortObject = 0");
		assertTrue(execute(query));
	}

	public void testWhereBeanIncludedMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.whereIncluded(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"});
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyByte = 89 AND propertyDouble = 53348.34 AND propertyShort = 43 AND propertyStringbuffer = 'someotherstringbuff' AND propertyTime = TIME '15:26:14'");
		assertTrue(execute(query));
	}

	public void testWhereBeanExcludedMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.whereExcluded(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"});
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyBigDecimal = 219038743.392874 AND propertyBoolean = true AND propertyBooleanObject = false AND propertyByteObject = 34 AND propertyCalendar = TIMESTAMP '2002-06-18 15:26:14.764' AND propertyChar = 'v' AND propertyCharacterObject = 'r' AND propertyDate = TIMESTAMP '2002-06-18 15:26:14.764' AND propertyDoubleObject = 143298.692 AND propertyEnum = 'VALUE_THREE' AND propertyFloat = 98634.2 AND propertyFloatObject = 8734.7 AND propertyInt = 545 AND propertyIntegerObject = 968 AND propertyLong = 34563 AND propertyLongObject = 66875 AND propertyShortObject = 68 AND propertySqlDate = DATE '2002-06-18' AND propertyString = 'someotherstring' AND propertyTimestamp = TIMESTAMP '2002-06-18 15:26:14.764'");
		assertTrue(execute(query));
	}

	public void testWhereBeanFilteredMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.whereFiltered(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"}, new String[] {"propertyByte", "propertyShort", "propertyTime"});
		assertEquals(query.getSql(), "SELECT * FROM tablename WHERE propertyDouble = 53348.34 AND propertyStringbuffer = 'someotherstringbuff'");
		assertTrue(execute(query));
	}

	public void testWhereParametersBeanMckoi()
	{
		Select query = new Select(mMckoi);
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
							   java.sql.Date date = new java.sql.Date(102, 5, 18);
							   Time time = new Time(15, 26, 14);
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
								   .setFloat(13, 98634.2f)
								   .setFloat(14, 8734.7f)
								   .setInt(15, 545)
								   .setInt(16, 968)
								   .setLong(17, 34563L)
								   .setLong(18, 66875L)
								   .setShort(19, (short)43)
								   .setShort(20, (short)68)
								   .setDate(21, date)
								   .setString(22, "someotherstring")
								   .setString(23, "someotherstringbuff")
								   .setTime(24, time)
								   .setTimestamp(25, new Timestamp(cal.getTime().getTime()));
						   }
					   }));
	}

	public void testWhereParametersBeanConstrainedMckoi()
	{
		Select query = new Select(mMckoi);
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

		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					Calendar cal = Calendar.getInstance();
					cal.set(2002, 5, 18, 15, 26, 14);
					cal.set(Calendar.MILLISECOND, 764);
					java.sql.Date date = new java.sql.Date(102, 5, 18);
					Time time = new Time(15, 26, 14);
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
						.setDate(18, date)
						.setString(19, "someotherstring")
						.setString(20, "someotherstringbuff")
						.setTime(21, time)
						.setTimestamp(22, new Timestamp(cal.getTime().getTime()));
				}
			}));
	}

	public void testWhereParametersBeanExcludedMckoi()
	{
		Select query = new Select(mMckoi);
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
					Time time = new Time(15, 26, 14);
					statement
						.setBigDecimal(1, new BigDecimal("219038743.392874"))
						.setBoolean(2, false)
						.setByte(3, (byte)34)
						.setTimestamp(4, new java.sql.Timestamp(cal.getTime().getTime()))
						.setString(5, "r")
						.setTimestamp(6, new java.sql.Timestamp(cal.getTime().getTime()))
						.setDouble(7, 143298.692d)
						.setString(8, "VALUE_THREE")
						.setFloat(9, 98634.2f)
						.setFloat(10, 8734.7f)
						.setInt(11, 968)
						.setLong(12, 66875L)
						.setShort(13, (short)43)
						.setShort(14, (short)68)
						.setString(15, "someotherstring")
						.setTime(16, time);
				}
			}));
	}

	public void testDistinctMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.distinct()
			.where("propertyByte = 89")
			.orderBy("propertyDouble")
			.orderBy("propertyShort")
			.orderBy("propertyTime");
		assertEquals(query.getSql(), "SELECT DISTINCT * FROM tablename WHERE propertyByte = 89 ORDER BY propertyDouble ASC, propertyShort ASC, propertyTime ASC");
		assertTrue(execute(query));
	}

	public void testDistinctOnMckoi()
	{
		Select query = new Select(mMckoi);
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

	public void testComplexMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.field("field1")
			.field("field2")
			.field("field3")
			.join("table2")
			.joinCross("table3")
			.where("this = that")
			.groupBy("gexpr1")
			.groupBy("gexpr2")
			.having("hexpr1")
			.having("hexpr2")
			.distinct()
			.unionAll("uexpr1")
			.union("uexpr2")
			.limit(3)
			.offset(1);
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

	public void testGroupByBeanMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.fields(BeanImpl.class)
			.groupBy(BeanImpl.class);
		assertEquals(query.getSql(), "SELECT propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyCalendar, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyEnum, propertyFloat, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShort, propertyShortObject, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp FROM tablename GROUP BY propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyCalendar, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyEnum, propertyFloat, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShort, propertyShortObject, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp");
		assertTrue(execute(query));
	}

	public void testGroupByBeanExcludedMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.fieldsExcluded(BeanImpl.class, new String[] {"propertyCalendar", "propertyFloat", "propertyShort"})
			.groupByExcluded(BeanImpl.class, new String[] {"propertyCalendar", "propertyFloat", "propertyShort"});
		assertEquals(query.getSql(), "SELECT propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyEnum, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShortObject, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp FROM tablename GROUP BY propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyEnum, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShortObject, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp");
		assertTrue(execute(query));
	}

	public void testJoinMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.join("table2")
			.join("table3");
		assertEquals(query.getSql(), "SELECT * FROM tablename, table2, table3");
		assertTrue(execute(query));
	}

	public void testJoinCustomMckoi()
	{
		Select query = new Select(mMckoi);
		query.from("tablename")
			.joinCustom("JOIN table3 ON (tablename.propertyInt = table3.propertyInt)")
			.joinCustom("JOIN table2 ON (table3.propertyInt = table2.propertyInt)");
		assertEquals(query.getSql(), "SELECT * FROM tablename JOIN table3 ON (tablename.propertyInt = table3.propertyInt) JOIN table2 ON (table3.propertyInt = table2.propertyInt)");
		assertTrue(execute(query));
	}

	public void testJoinCrossMckoi()
	{
		Select query = new Select(mMckoi);
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

	public void testJoinInnerMckoi()
	{
		Select query = new Select(mMckoi);
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
		assertEquals(query.getSql(), "SELECT * FROM tablename JOIN table2 ON (tablename.propertyInt = table2.propertyInt)");
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

	public void testJoinOuterMckoi()
	{
		Select query = new Select(mMckoi);

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

	public void testLimitMckoi()
	{
		Select query = new Select(mMckoi);
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

	public void testLimitCapabilitiesMckoi()
	{
		DbQueryManager manager = setupQuery(mMckoi);
		try
		{
			Select query = new Select(mMckoi)
				.from("tablename")
				.limit(3);

			final int[] count1 = new int[] {0};
			final int[] integers1 = new int[] {0, 0, 0};
			manager.executeFetchAll(query, new DbRowProcessor() {
					public boolean processRow(ResultSet resultSet) throws SQLException
					{
						integers1[count1[0]] = resultSet.getInt("propertyInt");
						count1[0]++;

						return true;
					}
				});
			assertEquals(query.getSql(), "SELECT * FROM tablename");
			assertEquals(count1[0], 3);
			assertEquals(integers1[0], 545);
			assertEquals(integers1[1], 0);
			assertEquals(integers1[2], 3);

			query.offset(1);

			final int[] count2 = new int[] {0};
			final int[] integers2 = new int[] {0, 0, 0};
			manager.executeFetchAll(query, new DbRowProcessor() {
					public boolean processRow(ResultSet resultSet) throws SQLException
					{
						integers2[count2[0]] = resultSet.getInt("propertyInt");
						count2[0]++;

						return true;
					}
				});
			assertEquals(query.getSql(), "SELECT * FROM tablename");
			assertEquals(count2[0], 3);
			assertEquals(integers2[0], 0);
			assertEquals(integers2[1], 3);
			assertEquals(integers2[2], 4);

			query.clear();
			query.from("tablename")
				.offset(10);
			final int[] count3 = new int[] {0};
			final int[] integers3 = new int[] {0, 0, 0, 0, 0};
			manager.executeFetchAll(query, new DbRowProcessor() {
					public boolean processRow(ResultSet resultSet) throws SQLException
					{
						integers3[count3[0]] = resultSet.getInt("propertyInt");
						count3[0]++;

						return true;
					}
				});
			assertEquals(query.getSql(), "SELECT * FROM tablename");
			assertEquals(count3[0], 5);
			assertEquals(integers3[0], 545);
			assertEquals(integers3[1], 0);
			assertEquals(integers3[2], 3);
			assertEquals(integers3[3], 4);
			assertEquals(integers3[4], 5);
		}
		finally
		{
			cleanupQuery(manager);
		}
	}

	public void testLimitParameterMckoi()
	{
		Select query = new Select(mMckoi);
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

	public void testSubselectParamsMckoi()
	{
		Select fieldquery = new Select(mMckoi);
		fieldquery
			.from("table2")
			.field("max(propertyLong)")
			.whereParameter("propertyInt", ">");
		Select tablequery = new Select(mMckoi);
		tablequery
			.from("table2")
			.whereParameter("propertyLong", "<");
		Select wherequery = new Select(mMckoi);
		wherequery
			.from("table3")
			.field("max(propertyShort)")
			.whereParameter("propertyShort", "!=");
		Select unionquery1 = new Select(mMckoi);
		unionquery1
			.from("table2")
			.field("propertyString")
			.field("max(propertyByte)")
			.whereParameter("propertyByte", "=")
			.groupBy("propertyString");
		Select unionquery2 = new Select(mMckoi);
		unionquery2
			.from("table2")
			.field("propertyStringbuffer")
			.field("min(propertyByte)")
			.whereParameter("propertyByte", ">")
			.groupBy("propertyStringbuffer");

		// Manual subselect creation
		Select query = new Select(mMckoi);
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
			.field("tablename.propertyString");
			// McKoi doesn't support field subselects
//			.field("("+fieldquery+") AS propertyLong")
//			.fieldSubselect(fieldquery);
		assertEquals(query.getSql(), "SELECT tablename.propertyString FROM tablename, (SELECT * FROM table2 WHERE propertyLong < ?) AS tablesubselect WHERE tablename.propertyShort >= (SELECT max(propertyShort) FROM table3 WHERE propertyShort != ?) OR tablename.propertyString = ? UNION ALL SELECT propertyString, max(propertyByte) FROM table2 WHERE propertyByte = ? GROUP BY propertyString UNION ALL SELECT propertyStringbuffer, min(propertyByte) FROM table2 WHERE propertyByte > ? GROUP BY propertyStringbuffer");
		String[] parameters = query.getParameters().getOrderedNamesArray();
		assertEquals(5, parameters.length);
		assertEquals(parameters[0], "propertyLong");
		assertEquals(parameters[1], "propertyShort");
		assertEquals(parameters[2], "propertyString");
		assertEquals(parameters[3], "propertyByte");
		assertEquals(parameters[4], "propertyByte");

		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setLong("propertyLong", 99999999)
						.setShort("propertyShort", (short)5)
						.setString("propertyString", "thestring")
						.setByte("propertyByte", (byte)4);
				}
			}));

		//Automated subselect creation
		query = new Select(mMckoi);
		query
			.union(unionquery1)
			.union(unionquery2)
			.where("tablename.propertyShort", ">=", wherequery)
			.whereParameterOr("tablename.propertyString", "propertyString", "=")
			.whereOr("tablename.propertyFloat", ">", new Select(mMckoi)
				.from("table3")
				.field("max(propertyLong)")
				.whereParameter("propertyLong", "!="))
			.from("tablename", new Select(mMckoi)
				.from("tablename"))
			.join("tablesubselect", tablequery)
			.whereAnd("tablename.propertyDouble", "<=", new Select(mMckoi)
				.from("table2")
				.field("max(propertyFloat)")
				.whereParameter("propertyFloat", "!="))
			.field("tablename.propertyString");
			// McKoi doesn't support field subselects
//			.field("propertyLong", fieldquery);
		assertEquals(query.getSql(), "SELECT tablename.propertyString FROM (SELECT * FROM tablename) tablename, (SELECT * FROM table2 WHERE propertyLong < ?) tablesubselect WHERE tablename.propertyShort >= (SELECT max(propertyShort) FROM table3 WHERE propertyShort != ?) OR tablename.propertyString = ? OR tablename.propertyFloat > (SELECT max(propertyLong) FROM table3 WHERE propertyLong != ?) AND tablename.propertyDouble <= (SELECT max(propertyFloat) FROM table2 WHERE propertyFloat != ?) UNION ALL SELECT propertyString, max(propertyByte) FROM table2 WHERE propertyByte = ? GROUP BY propertyString UNION ALL SELECT propertyStringbuffer, min(propertyByte) FROM table2 WHERE propertyByte > ? GROUP BY propertyStringbuffer");
		parameters = query.getParameters().getOrderedNamesArray();
		assertEquals(7, parameters.length);
		assertEquals(parameters[0], "propertyLong");
		assertEquals(parameters[1], "propertyShort");
		assertEquals(parameters[2], "propertyString");
		assertEquals(parameters[3], "propertyLong");
		assertEquals(parameters[4], "propertyFloat");
		assertEquals(parameters[5], "propertyByte");
		assertEquals(parameters[6], "propertyByte");

		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setLong("propertyLong", 99999999)
						.setShort("propertyShort", (short)5)
						.setString("propertyString", "thestring")
						.setFloat("propertyFloat", -1f)
						.setByte("propertyByte", (byte)4);
				}
			}));
	}

	public void testCloneMckoi()
	{
		Select fieldquery = new Select(mMckoi);
		fieldquery
			.from("table2")
			.field("propertyLong")
			.whereParameter("propertyInt", ">")
			.limit(1);
		Select tablequery = new Select(mMckoi);
		tablequery
			.from("table2")
			.whereParameter("propertyLong", "<");
		Select wherequery = new Select(mMckoi);
		wherequery
			.from("table3")
			.field("max(propertyShort)")
			.whereParameter("propertyShort", "!=");
		Select unionquery1 = new Select(mMckoi);
		unionquery1
			.from("table2")
			.field("propertyString")
			.field("max(propertyByte)")
			.whereParameter("propertyByte", "=")
			.groupBy("propertyString");
		Select unionquery2 = new Select(mMckoi);
		unionquery2
			.from("table2")
			.field("propertyStringbuffer")
			.field("min(propertyByte)")
			.whereParameter("propertyByte", ">")
			.groupBy("propertyStringbuffer");
		Select query = new Select(mMckoi);
		query
			.from("tablename")
			.join("("+tablequery+") AS tablesubselect")
			.tableSubselect(tablequery)
			.join("table3")
			.joinOuter("table2", Select.RIGHT, Select.ON, "table3.propertyInt = table2.propertyInt")
			.distinct()
//			.distinctOn("tablename.propertyShort")
			.field("tablename.propertyString")
//			.field("("+fieldquery+") AS propertyLong")
//			.fieldSubselect(fieldquery)
			.where("tablename.propertyShort >= ("+wherequery+")")
			.whereSubselect(wherequery)
			.whereParameterOr("tablename.propertyString", "propertyString", "=")
			.whereOr("tablename.propertyByte", "=", (byte)54)
			.whereAnd("tablename.propertyDouble", "!=", 73453.71d)
			.whereParameterOr("tablename.propertyInt", "propertyInt", "=")
			.whereParameterAnd("tablename.propertyLong", "propertyLong", "<")
			.whereParameterOr("tablename.propertyChar", "propertyChar", "=")
			.groupBy("tablename.propertyShort")
			.groupBy("tablename.propertyLong")
			.groupBy("tablename.propertyString")
			.having("tablename.propertyLong = 1")
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

