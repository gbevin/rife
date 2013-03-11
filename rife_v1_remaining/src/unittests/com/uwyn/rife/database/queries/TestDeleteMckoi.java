/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDeleteMckoi.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.BeanImpl;
import com.uwyn.rife.database.BeanImplConstrained;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.exceptions.TableNameRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;

public class TestDeleteMckoi extends TestDelete
{
	public TestDeleteMckoi(String name)
	{
		super(name);
	}

	public void testInstantiationMckoi()
	{
		Delete query = new Delete(mMckoi);
		assertNotNull(query);
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "Delete");
		}
	}

	public void testIncompleteQueryMckoi()
	{
		Delete query = new Delete(mMckoi);
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "Delete");
		}
		query.where("this = that");
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "Delete");
		}
		query.from("tablename");
		assertNotNull(query.getSql());
	}

	public void testClearMckoi()
	{
		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.where("this = that");
		assertNotNull(query.getSql());
		query.clear();
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "Delete");
		}
	}

	public void testHintMckoi()
	{
		Delete query = new Delete(mMckoi)
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

	public void testFromMckoi()
	{
		Delete query = new Delete(mMckoi);
		query.from("tablename");
		assertEquals(query.getSql(), "DELETE FROM tablename");
		assertTrue(execute(query));
	}

	public void testWhereMckoi()
	{
		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.where("propertyByte = 89");
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyByte = 89");
		assertTrue(execute(query));
	}

	public void testWhereTypedMckoi()
	{
		Delete query = new Delete(mMckoi);
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

		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyBigDecimal >= 53443433.9784567 AND propertyBoolean = false OR propertyByte = 54 AND propertyCalendar <= TIMESTAMP '2003-03-03 10:01:28.154' OR propertyChar = 'f' AND propertyDate = TIMESTAMP '2003-03-03 10:01:28.154' AND propertyDouble != 73453.71 OR propertyFloat >= 1987.14 AND propertyInt = 973 AND propertyLong < 347678 AND propertyShort = 78 OR propertySqlDate = DATE '2003-03-03' AND propertyString LIKE 'someotherstring%' AND propertyStringbuffer = 'someotherstringbuff' OR propertyTime = TIME '10:01:28' AND propertyTimestamp <= TIMESTAMP '2003-03-03 10:01:28.154'");
		assertFalse(execute(query));
	}

	public void testWhereTypedMixedMckoi()
	{
		Delete query = new Delete(mMckoi);
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

		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyBigDecimal >= 53443433.9784567 AND propertyBoolean = false OR propertyByte = 54 AND propertyCalendar <= TIMESTAMP '2003-03-03 10:01:28.154' OR propertyChar = 'f' AND propertyDate = TIMESTAMP '2003-03-03 10:01:28.154' AND propertyDouble != 73453.71 OR propertyFloat >= 1987.14 AND propertyInt = 973 AND propertyLong < 347678 AND propertyShort = 78 OR propertySqlDate = ? AND propertyString LIKE 'someotherstring%' AND propertyStringbuffer = 'someotherstringbuff' OR propertyTime = TIME '10:01:28' AND propertyTimestamp <= TIMESTAMP '2003-03-03 10:01:28.154'");

		assertFalse(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setDate("propertySqlDate", new java.sql.Date(cal.getTime().getTime()));
				}
			}));
	}

	public void testWhereParametersMckoi()
	{
		Delete query = new Delete(mMckoi);
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

		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyInt = ? AND propertyLong < ? OR propertyChar = ?");
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
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyInt = 545");
	}

	public void testWhereParametersMixedMckoi()
	{
		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.where("propertyInt = 545")
			.whereParameterAnd("propertyLong", "<")
			.whereParameterOr("propertyChar", "=");

		assertEquals(query.getParameters().getOrderedNames().get(0), "propertyLong");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyChar");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {"propertyLong", "propertyChar"}));

		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyInt = 545 AND propertyLong < ? OR propertyChar = ?");
		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setLong(1, 50000)
						.setString(2, "v");
				}
			}));

		query.where("propertyInt = 545");

		assertNull(query.getParameters());
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyInt = 545");
	}

	public void testWhereConstructionMckoi()
	{
		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.where("propertyInt = 545")
			.whereAnd("propertyLong < 50000")
			.whereOr("propertyChar = 'v'");
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyInt = 545 AND propertyLong < 50000 OR propertyChar = 'v'");
		assertTrue(execute(query));
	}

	public void testWhereBeanMckoi()
	{
		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.where(BeanImpl.getPopulatedBean());
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyBigDecimal = 219038743.392874 AND propertyBoolean = true AND propertyBooleanObject = false AND propertyByte = 89 AND propertyByteObject = 34 AND propertyCalendar = TIMESTAMP '2002-06-18 15:26:14.764' AND propertyChar = 'v' AND propertyCharacterObject = 'r' AND propertyDate = TIMESTAMP '2002-06-18 15:26:14.764' AND propertyDouble = 53348.34 AND propertyDoubleObject = 143298.692 AND propertyEnum = 'VALUE_THREE' AND propertyFloat = 98634.2 AND propertyFloatObject = 8734.7 AND propertyInt = 545 AND propertyIntegerObject = 968 AND propertyLong = 34563 AND propertyLongObject = 66875 AND propertyShort = 43 AND propertyShortObject = 68 AND propertySqlDate = DATE '2002-06-18' AND propertyString = 'someotherstring' AND propertyStringbuffer = 'someotherstringbuff' AND propertyTime = TIME '15:26:14' AND propertyTimestamp = TIMESTAMP '2002-06-18 15:26:14.764'");
		assertTrue(execute(query));
	}

	public void testWhereBeanConstrainedMckoi()
	{
		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.where(BeanImplConstrained.getPopulatedBean());
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyBigDecimal = 219038743.392874 AND propertyBoolean = true AND propertyBooleanObject = false AND propertyByte = 89 AND propertyByteObject = 34 AND propertyCalendar = TIMESTAMP '2002-06-18 15:26:14.764' AND propertyChar = 'v' AND propertyCharacterObject = 'r' AND propertyDate = TIMESTAMP '2002-06-18 15:26:14.764' AND propertyDouble = 53348.34 AND propertyDoubleObject = 143298.692 AND propertyFloat = 98634.2 AND propertyFloatObject = 8734.7 AND propertyInt = 545 AND propertyIntegerObject = 968 AND propertyLongObject = 66875 AND propertyShort = 43 AND propertySqlDate = DATE '2002-06-18' AND propertyString = 'someotherstring' AND propertyStringbuffer = 'someotherstringbuff' AND propertyTime = TIME '15:26:14' AND propertyTimestamp = TIMESTAMP '2002-06-18 15:26:14.764'");
		assertTrue(execute(query));
	}

	public void testWhereConstructionGroupMckoi()
	{
		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.where("propertyInt = 545")
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

		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyInt = 545 AND propertyLong < 50000 OR (propertyString = ? AND propertyByte <= 0 AND (propertyBoolean != true OR propertyStringbuffer LIKE ?)) OR propertyChar = 'v'");

		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setString("propertyString", "someotherstring")
						.setString("propertyStringbuffer", "stringbuff");
				}
			}));
	}

	public void testWhereBeanNullValuesMckoi()
	{
		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.where(BeanImpl.getNullBean());
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyBoolean = false AND propertyBooleanObject = false AND propertyByte = 0 AND propertyByteObject = 0 AND propertyDouble = 0.0 AND propertyDoubleObject = 0.0 AND propertyFloat = 0.0 AND propertyFloatObject = 0.0 AND propertyInt = 0 AND propertyIntegerObject = 0 AND propertyLong = 0 AND propertyLongObject = 0 AND propertyShort = 0 AND propertyShortObject = 0");
		assertTrue(execute(query));
	}

	public void testWhereBeanIncludedMckoi()
	{
		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.whereIncluded(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"});
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyByte = 89 AND propertyDouble = 53348.34 AND propertyShort = 43 AND propertyStringbuffer = 'someotherstringbuff' AND propertyTime = TIME '15:26:14'");
		assertTrue(execute(query));
	}

	public void testWhereBeanExcludedMckoi()
	{
		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.whereExcluded(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"});
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyBigDecimal = 219038743.392874 AND propertyBoolean = true AND propertyBooleanObject = false AND propertyByteObject = 34 AND propertyCalendar = TIMESTAMP '2002-06-18 15:26:14.764' AND propertyChar = 'v' AND propertyCharacterObject = 'r' AND propertyDate = TIMESTAMP '2002-06-18 15:26:14.764' AND propertyDoubleObject = 143298.692 AND propertyEnum = 'VALUE_THREE' AND propertyFloat = 98634.2 AND propertyFloatObject = 8734.7 AND propertyInt = 545 AND propertyIntegerObject = 968 AND propertyLong = 34563 AND propertyLongObject = 66875 AND propertyShortObject = 68 AND propertySqlDate = DATE '2002-06-18' AND propertyString = 'someotherstring' AND propertyTimestamp = TIMESTAMP '2002-06-18 15:26:14.764'");
		assertTrue(execute(query));
	}

	public void testWhereBeanFilteredMckoi()
	{
		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.whereFiltered(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"}, new String[] {"propertyByte", "propertyShort", "propertyTime"});
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyDouble = 53348.34 AND propertyStringbuffer = 'someotherstringbuff'");
		assertTrue(execute(query));
	}

	public void testWhereParametersBeanMckoi()
	{
		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.whereParameters(BeanImpl.class);
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyBigDecimal = ? AND propertyBoolean = ? AND propertyBooleanObject = ? AND propertyByte = ? AND propertyByteObject = ? AND propertyCalendar = ? AND propertyChar = ? AND propertyCharacterObject = ? AND propertyDate = ? AND propertyDouble = ? AND propertyDoubleObject = ? AND propertyEnum = ? AND propertyFloat = ? AND propertyFloatObject = ? AND propertyInt = ? AND propertyIntegerObject = ? AND propertyLong = ? AND propertyLongObject = ? AND propertyShort = ? AND propertyShortObject = ? AND propertySqlDate = ? AND propertyString = ? AND propertyStringbuffer = ? AND propertyTime = ? AND propertyTimestamp = ?");
		
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
		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.whereParameters(BeanImplConstrained.class);
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyBigDecimal = ? AND propertyBoolean = ? AND propertyBooleanObject = ? AND propertyByte = ? AND propertyByteObject = ? AND propertyCalendar = ? AND propertyChar = ? AND propertyCharacterObject = ? AND propertyDate = ? AND propertyDouble = ? AND propertyDoubleObject = ? AND propertyFloat = ? AND propertyFloatObject = ? AND propertyInt = ? AND propertyIntegerObject = ? AND propertyLongObject = ? AND propertyShort = ? AND propertySqlDate = ? AND propertyString = ? AND propertyStringbuffer = ? AND propertyTime = ? AND propertyTimestamp = ?");

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
		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.whereParametersExcluded(BeanImpl.class,
				new String[] {"propertyBoolean", "propertyByte", "propertyChar",
							  "propertyDouble", "propertyInt", "propertyLong",
							  "propertySqlDate", "propertyStringbuffer", "propertyTimestamp"});
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyBigDecimal = ? AND propertyBooleanObject = ? AND propertyByteObject = ? AND propertyCalendar = ? AND propertyCharacterObject = ? AND propertyDate = ? AND propertyDoubleObject = ? AND propertyEnum = ? AND propertyFloat = ? AND propertyFloatObject = ? AND propertyIntegerObject = ? AND propertyLongObject = ? AND propertyShort = ? AND propertyShortObject = ? AND propertyString = ? AND propertyTime = ?");
		
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

	public void testDeleteSubselectParamsMckoi()
	{
		Select wherequery = new Select(mMckoi);
		wherequery
			.from("table2")
			.field("max(propertyShort)")
			.whereParameter("propertyShort", "!=");

		// Manual subselect creation
		Delete query = new Delete(mMckoi);
		query
			.where("propertyShort >= ("+wherequery+")")
			.whereSubselect(wherequery)
			.whereParameterOr("propertyString", "=")
			.from("tablename");
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyShort >= (SELECT max(propertyShort) FROM table2 WHERE propertyShort != ?) OR propertyString = ?");
		String[] parameters = query.getParameters().getOrderedNamesArray();
		assertEquals(2, parameters.length);
		assertEquals(parameters[0], "propertyShort");
		assertEquals(parameters[1], "propertyString");
		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setShort("propertyShort", (short)5)
						.setString("propertyString", "thestring");
				}
			}));

		// Automated subselect creation
		query = new Delete(mMckoi);
		query
			.where("propertyShort", ">=", wherequery)
			.whereParameterOr("propertyString", "=")
			.whereOr("tablename.propertyFloat", ">", new Select(mMckoi)
				.from("table2")
				.field("max(propertyLong)")
				.whereParameter("propertyLong", "!="))
			.whereAnd("tablename.propertyDouble", "<=", new Select(mMckoi)
				.from("table2")
				.field("max(propertyFloat)")
				.whereParameter("propertyFloat", "!="))
			.from("tablename");
		assertEquals(query.getSql(), "DELETE FROM tablename WHERE propertyShort >= (SELECT max(propertyShort) FROM table2 WHERE propertyShort != ?) OR propertyString = ? OR tablename.propertyFloat > (SELECT max(propertyLong) FROM table2 WHERE propertyLong != ?) AND tablename.propertyDouble <= (SELECT max(propertyFloat) FROM table2 WHERE propertyFloat != ?)");
		parameters = query.getParameters().getOrderedNamesArray();
		assertEquals(4, parameters.length);
		assertEquals(parameters[0], "propertyShort");
		assertEquals(parameters[1], "propertyString");
		assertEquals(parameters[2], "propertyLong");
		assertEquals(parameters[3], "propertyFloat");
		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setShort("propertyShort", (short)5)
						.setString("propertyString", "thestring")
						.setLong("propertyLong", 99999999)
						.setFloat("propertyFloat", -1f);
				}
			}));
	}

	public void testCloneMckoi()
	{
		Select wherequery = new Select(mMckoi);
		wherequery
			.from("table2")
			.field("max(propertyShort)")
			.whereParameter("propertyShort", "!=");

		Delete query = new Delete(mMckoi);
		query.from("tablename")
			.where("propertyShort >= ("+wherequery+")")
			.whereSubselect(wherequery)
			.whereParameterOr("propertyString", "=")
			.whereOr("propertyByte", "=", (byte)54)
			.whereAnd("propertyDouble", "!=", 73453.71d)
			.whereParameterOr("propertyInt", "=")
			.whereParameterAnd("propertyLong", "<")
			.whereParameterOr("propertyChar", "=");

		Delete query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setString("propertyChar", "M")
						.setInt("propertyInt", 34)
						.setString("propertyString", "string'value")
						.setLong("propertyLong", 34543)
						.setShort("propertyShort", (short)4);
				}
			});
	}
}
