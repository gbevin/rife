/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestUpdateOracle.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.BeanImpl;
import com.uwyn.rife.database.BeanImplConstrained;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.exceptions.FieldsRequiredException;
import com.uwyn.rife.database.exceptions.TableNameRequiredException;
import com.uwyn.rife.database.types.SqlNull;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;

public class TestUpdateOracle extends TestUpdate
{
	public TestUpdateOracle(String name)
	{
		super(name);
	}

	public void testInstantiationOracle()
	{
		Update query = new Update(mOracle);
		assertNotNull(query);
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "Update");
		}
	}

	public void testIncompleteQueryOracle()
	{
		Update query = new Update(mOracle);
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "Update");
		}
		query.table("tablename4");
		try
		{
			query.getSql();
			fail();
		}
		catch (FieldsRequiredException e)
		{
			assertEquals(e.getQueryName(), "Update");
		}
		query.field("col1", "val1");
		assertNotNull(query.getSql());
	}

	public void testClearOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename4")
			.field("col1", "val1");
		assertNotNull(query.getSql());
		query.clear();
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "Update");
		}
	}

	public void testHintOracle()
	{
		Update query = new Update(mOracle)
			.hint("NO_INDEX")
			.table("tablename")
			.field("propertyInt", 34);
		assertEquals(query.getSql(), "UPDATE /*+ NO_INDEX */ tablename SET propertyInt = 34");
		assertTrue(execute(query));
	}

	public void testFieldOracle()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2002, 7, 19, 12, 17, 52);
		Update query = new Update(mOracle);
		query.table("tablename")
			.where("propertyByte = 89")
			.field("nullColumn", SqlNull.NULL)
			.field("propertyBigDecimal", new BigDecimal("98347.876438637"))
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16)
			.field("propertyCalendar", cal.getTime())
			.field("propertyChar", 'M')
			.field("propertyDate", cal)
			.field("propertyDouble", 12.3d)
			.field("propertyFloat", 13.4f)
			.field("propertyInt", 34)
			.field("propertyLong", 45L)
			.field("propertyShort", (short)12)
			.field("propertySqlDate", new java.sql.Date(cal.getTime().getTime()))
			.field("propertyString", "string'value")
			.field("propertyStringbuffer", new StringBuffer("stringbuffer'value"))
			.field("propertyTime", new Time(cal.getTime().getTime()))
			.field("propertyTimestamp", new Timestamp(cal.getTime().getTime()));
		assertEquals(query.getSql(), "UPDATE tablename SET nullColumn = NULL, propertyBigDecimal = 98347.876438637, propertyBoolean = 1, propertyByte = 16, propertyCalendar = TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS'), propertyChar = 'M', propertyDate = TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS'), propertyDouble = 12.3, propertyFloat = 13.4, propertyInt = 34, propertyLong = 45, propertyShort = 12, propertySqlDate = TO_DATE('2002/08/19 00:00:00', 'YYYY/MM/DD HH24:MI:SS'), propertyString = 'string''value', propertyStringbuffer = 'stringbuffer''value', propertyTime = TO_DATE('12:17:52', 'HH24:MI:SS'), propertyTimestamp = TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS') WHERE propertyByte = 89");
		assertTrue(execute(query));
	}

	public void testFieldCustomOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.fieldCustom("propertySqlDate", "(SELECT sysdate FROM dual)");
		assertEquals(query.getSql(), "UPDATE tablename SET propertySqlDate = (SELECT sysdate FROM dual)");
		assertTrue(execute(query));
	}

	public void testFieldParametersOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename");

		assertNull(query.getParameters());

		query.fieldParameter("nullColumn")
			.fieldParameter("propertyBigDecimal")
			.fieldParameter("propertyBoolean")
			.fieldParameter("propertyByte")
			.fieldParameter("propertyCalendar")
			.fieldParameter("propertyChar")
			.fieldParameter("propertyDate")
			.fieldParameter("propertyDouble")
			.fieldParameter("propertyFloat")
			.fieldParameter("propertyInt")
			.fieldParameter("propertyLong")
			.fieldParameter("propertyShort")
			.fieldParameter("propertySqlDate")
			.fieldParameter("propertyString")
			.fieldParameter("propertyStringbuffer")
			.fieldParameter("propertyTime")
			.fieldParameter("propertyTimestamp");

		assertEquals(query.getParameters().getOrderedNames().size(), 17);
		assertEquals(query.getParameters().getOrderedNames().get(0), "nullColumn");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyBigDecimal");
		assertEquals(query.getParameters().getOrderedNames().get(2), "propertyBoolean");
		assertEquals(query.getParameters().getOrderedNames().get(3), "propertyByte");
		assertEquals(query.getParameters().getOrderedNames().get(4), "propertyCalendar");
		assertEquals(query.getParameters().getOrderedNames().get(5), "propertyChar");
		assertEquals(query.getParameters().getOrderedNames().get(6), "propertyDate");
		assertEquals(query.getParameters().getOrderedNames().get(7), "propertyDouble");
		assertEquals(query.getParameters().getOrderedNames().get(8), "propertyFloat");
		assertEquals(query.getParameters().getOrderedNames().get(9), "propertyInt");
		assertEquals(query.getParameters().getOrderedNames().get(10), "propertyLong");
		assertEquals(query.getParameters().getOrderedNames().get(11), "propertyShort");
		assertEquals(query.getParameters().getOrderedNames().get(12), "propertySqlDate");
		assertEquals(query.getParameters().getOrderedNames().get(13), "propertyString");
		assertEquals(query.getParameters().getOrderedNames().get(14), "propertyStringbuffer");
		assertEquals(query.getParameters().getOrderedNames().get(15), "propertyTime");
		assertEquals(query.getParameters().getOrderedNames().get(16), "propertyTimestamp");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {
			"nullColumn",
			"propertyBigDecimal",
			"propertyBoolean",
			"propertyByte",
			"propertyCalendar",
			"propertyChar",
			"propertyDate",
			"propertyDouble",
			"propertyFloat",
			"propertyInt",
			"propertyLong",
			"propertyShort",
			"propertySqlDate",
			"propertyString",
			"propertyStringbuffer",
			"propertyTime",
			"propertyTimestamp"}));

		assertEquals(query.getSql(), "UPDATE tablename SET nullColumn = ?, propertyBigDecimal = ?, propertyBoolean = ?, propertyByte = ?, propertyCalendar = ?, propertyChar = ?, propertyDate = ?, propertyDouble = ?, propertyFloat = ?, propertyInt = ?, propertyLong = ?, propertyShort = ?, propertySqlDate = ?, propertyString = ?, propertyStringbuffer = ?, propertyTime = ?, propertyTimestamp = ?");

		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					Calendar cal = Calendar.getInstance();
					cal.set(2002, 7, 19, 12, 17, 52);
					cal.set(Calendar.MILLISECOND, 462);
					statement
						.setString(1, null)
						.setBigDecimal(2, new BigDecimal("98347.876438637"))
						.setBoolean(3, true)
						.setByte(4, (byte)16)
						.setDate(5, new java.sql.Date(cal.getTime().getTime()))
						.setString(6, "M")
						.setDate(7, new java.sql.Date(cal.getTime().getTime()))
						.setDouble(8, 12.3d)
						.setFloat(9, 13.4f)
						.setInt(10, 34)
						.setLong(11, 45L)
						.setShort(12, (short)12)
						.setDate(13, new java.sql.Date(cal.getTime().getTime()))
						.setString(14, "string'value")
						.setString(15, "string'value2")
						.setTime(16, new Time(cal.getTime().getTime()))
						.setTimestamp(17, new Timestamp(cal.getTime().getTime()));
				}
			}));
	}

	public void testFieldParametersMixedOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename");

		assertNull(query.getParameters());

		final Calendar cal = Calendar.getInstance();
		cal.set(2002, 7, 19, 12, 17, 52);
		cal.set(Calendar.MILLISECOND, 462);
		query.fieldParameter("nullColumn")
			.field("propertyBigDecimal", new BigDecimal("98347.876438637"))
			.fieldParameter("propertyBoolean")
			.fieldParameter("propertyByte")
			.field("propertyCalendar", cal.getTime())
			.fieldParameter("propertyChar")
			.field("propertyDate", cal)
			.field("propertyDouble", 12.3d)
			.fieldParameter("propertyFloat")
			.fieldParameter("propertyInt")
			.field("propertyLong", 45L)
			.field("propertyShort", (short)12)
			.fieldParameter("propertySqlDate")
			.fieldParameter("propertyString")
			.field("propertyStringbuffer", new StringBuffer("stringbuffer'value"))
			.field("propertyTime", new Time(cal.getTime().getTime()))
			.fieldParameter("propertyTimestamp");

		assertEquals(query.getParameters().getOrderedNames().size(), 9);
		assertEquals(query.getParameters().getOrderedNames().get(0), "nullColumn");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyBoolean");
		assertEquals(query.getParameters().getOrderedNames().get(2), "propertyByte");
		assertEquals(query.getParameters().getOrderedNames().get(3), "propertyChar");
		assertEquals(query.getParameters().getOrderedNames().get(4), "propertyFloat");
		assertEquals(query.getParameters().getOrderedNames().get(5), "propertyInt");
		assertEquals(query.getParameters().getOrderedNames().get(6), "propertySqlDate");
		assertEquals(query.getParameters().getOrderedNames().get(7), "propertyString");
		assertEquals(query.getParameters().getOrderedNames().get(8), "propertyTimestamp");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {
			"nullColumn",
			"propertyBoolean",
			"propertyByte",
			"propertyChar",
			"propertyFloat",
			"propertyInt",
			"propertySqlDate",
			"propertyString",
			"propertyTimestamp"}));

		assertEquals(query.getSql(), "UPDATE tablename SET nullColumn = ?, propertyBigDecimal = 98347.876438637, propertyBoolean = ?, propertyByte = ?, propertyCalendar = TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS'), propertyChar = ?, propertyDate = TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS'), propertyDouble = 12.3, propertyFloat = ?, propertyInt = ?, propertyLong = 45, propertyShort = 12, propertySqlDate = ?, propertyString = ?, propertyStringbuffer = 'stringbuffer''value', propertyTime = TO_DATE('12:17:52', 'HH24:MI:SS'), propertyTimestamp = ?");

		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setString(1, null)
						.setBoolean(2, true)
						.setByte(3, (byte)16)
						.setString(4, "M")
						.setFloat(5, 13.4f)
						.setInt(6, 34)
						.setDate(7, new java.sql.Date(cal.getTime().getTime()))
						.setString(8, "string'value")
						.setTimestamp(9, new Timestamp(cal.getTime().getTime()));
				}
			}));
	}

	public void testFieldsOracle()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2002, 7, 19, 12, 17, 52);
		Update query = new Update(mOracle);
		query.table("tablename")
			.where("propertyByte = 89")
			.fields(new Object[] {
				"nullColumn", SqlNull.NULL,
				"propertyBigDecimal", new BigDecimal("98347.876438637"),
				"propertyBoolean", new Boolean(true),
				"propertyByte", new Byte((byte)16),
				"propertyCalendar", cal.getTime(),
				"propertyChar", new Character('M'),
				"propertyDate", cal,
				"propertyDouble", new Double(12.3d),
				"propertyFloat", new Float(13.4f),
				"propertyInt", new Integer(34),
				"propertyLong", new Long(45L),
				"propertyShort", new Short((short)12),
				"propertySqlDate", new java.sql.Date(cal.getTime().getTime()),
				"propertyString", new String("string'value"),
				"propertyStringbuffer", new StringBuffer("stringbuffer'value"),
				"propertyTime", new Time(cal.getTime().getTime()),
				"propertyTimestamp", new Timestamp(cal.getTime().getTime())
			});
		assertEquals(query.getSql(), "UPDATE tablename SET nullColumn = NULL, propertyBigDecimal = 98347.876438637, propertyBoolean = 1, propertyByte = 16, propertyCalendar = TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS'), propertyChar = 'M', propertyDate = TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS'), propertyDouble = 12.3, propertyFloat = 13.4, propertyInt = 34, propertyLong = 45, propertyShort = 12, propertySqlDate = TO_DATE('2002/08/19 00:00:00', 'YYYY/MM/DD HH24:MI:SS'), propertyString = 'string''value', propertyStringbuffer = 'stringbuffer''value', propertyTime = TO_DATE('12:17:52', 'HH24:MI:SS'), propertyTimestamp = TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS') WHERE propertyByte = 89");
		assertTrue(execute(query));
	}

	public void testWhereConstructionOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16)
			.where("propertyInt = 545")
			.whereAnd("propertyLong < 50000")
			.whereOr("propertyChar = 'v'");
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyInt = 545 AND propertyLong < 50000 OR propertyChar = 'v'");
		assertTrue(execute(query));
	}

	public void testWhereConstructionGroupOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16)
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

		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyInt = 545 AND propertyLong < 50000 OR (propertyString = ? AND propertyByte <= 0 AND (propertyBoolean != 1 OR propertyStringbuffer LIKE ?)) OR propertyChar = 'v'");

		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setString("propertyString", "someotherstring")
						.setString("propertyStringbuffer", "stringbuff");
				}
			}));
	}

	public void testWhereTypedOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16);

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

		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyBigDecimal >= 53443433.9784567 AND propertyBoolean = 0 OR propertyByte = 54 AND propertyCalendar <= TO_DATE('2003/03/03 10:01:28', 'YYYY/MM/DD HH24:MI:SS') OR propertyChar = 'f' AND propertyDate = TO_DATE('2003/03/03 10:01:28', 'YYYY/MM/DD HH24:MI:SS') AND propertyDouble != 73453.71 OR propertyFloat >= 1987.14 AND propertyInt = 973 AND propertyLong < 347678 AND propertyShort = 78 OR propertySqlDate = TO_DATE('2003/03/03 00:00:00', 'YYYY/MM/DD HH24:MI:SS') AND propertyString LIKE 'someotherstring%' AND propertyStringbuffer = 'someotherstringbuff' OR propertyTime = TO_DATE('10:01:28', 'HH24:MI:SS') AND propertyTimestamp <= TO_DATE('2003/03/03 10:01:28', 'YYYY/MM/DD HH24:MI:SS')");
		assertFalse(execute(query));
	}

	public void testWhereTypedMixedOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16);

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

		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyBigDecimal >= 53443433.9784567 AND propertyBoolean = 0 OR propertyByte = 54 AND propertyCalendar <= TO_DATE('2003/03/03 10:01:28', 'YYYY/MM/DD HH24:MI:SS') OR propertyChar = 'f' AND propertyDate = TO_DATE('2003/03/03 10:01:28', 'YYYY/MM/DD HH24:MI:SS') AND propertyDouble != 73453.71 OR propertyFloat >= 1987.14 AND propertyInt = 973 AND propertyLong < 347678 AND propertyShort = 78 OR propertySqlDate = ? AND propertyString LIKE 'someotherstring%' AND propertyStringbuffer = 'someotherstringbuff' OR propertyTime = TO_DATE('10:01:28', 'HH24:MI:SS') AND propertyTimestamp <= TO_DATE('2003/03/03 10:01:28', 'YYYY/MM/DD HH24:MI:SS')");

		assertFalse(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setDate("propertySqlDate", new java.sql.Date(cal.getTime().getTime()));
				}
			}));
	}

	public void testWhereParametersOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16);

		assertNull(query.getParameters());

		query.whereParameter("propertyInt", "=")
			.whereParameterAnd("propertyLong", "<")
			.whereParameterOr("propertyChar", "=");

		assertEquals(query.getParameters().getOrderedNames().size(), 3);
		assertEquals(query.getParameters().getOrderedNames().get(0), "propertyInt");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyLong");
		assertEquals(query.getParameters().getOrderedNames().get(2), "propertyChar");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {"propertyInt", "propertyLong", "propertyChar"}));

		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyInt = ? AND propertyLong < ? OR propertyChar = ?");
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
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyInt = 545");
	}

	public void testWhereParametersMixedOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16)
			.where("propertyInt = 545")
			.whereParameterAnd("propertyLong", "<")
			.whereParameterOr("propertyChar", "=");

		assertEquals(query.getParameters().getOrderedNames().get(0), "propertyLong");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyChar");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {"propertyLong", "propertyChar"}));

		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyInt = 545 AND propertyLong < ? OR propertyChar = ?");
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
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyInt = 545");
	}

	public void testFieldWhereParametersOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename");

		assertNull(query.getParameters());

		query.fieldParameter("propertyBoolean")
			.fieldParameter("propertyByte");

		assertEquals(query.getParameters().getOrderedNames().size(), 2);
		assertEquals(query.getParameters().getOrderedNames().get(0), "propertyBoolean");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyByte");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {"propertyBoolean", "propertyByte"}));

		query.whereParameter("propertyInt", "=")
			.whereParameterAnd("propertyLong", "<")
			.whereParameterOr("propertyChar", "=");

		assertEquals(query.getParameters().getOrderedNames().size(), 5);
		assertEquals(query.getParameters().getOrderedNames().get(0), "propertyBoolean");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyByte");
		assertEquals(query.getParameters().getOrderedNames().get(2), "propertyInt");
		assertEquals(query.getParameters().getOrderedNames().get(3), "propertyLong");
		assertEquals(query.getParameters().getOrderedNames().get(4), "propertyChar");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {"propertyBoolean", "propertyByte", "propertyInt", "propertyLong", "propertyChar"}));

		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = ?, propertyByte = ? WHERE propertyInt = ? AND propertyLong < ? OR propertyChar = ?");
		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setBoolean(1, true)
						.setByte(2, (byte)16)
						.setInt(3, 545)
						.setLong(4, 50000)
						.setString(5, "v");
				}
			}));

		query.where("propertyInt = 545");

		assertEquals(query.getParameters().getOrderedNames().size(), 2);
		assertEquals(query.getParameters().getOrderedNamesArray().length, 2);
		assertEquals(query.getParameters().getOrderedNames().get(0), "propertyBoolean");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyByte");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {"propertyBoolean", "propertyByte"}));
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = ?, propertyByte = ? WHERE propertyInt = 545");
	}

	public void testFieldsBeanOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.where("propertyInt = 545")
			.fields(BeanImpl.getPopulatedBean());
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBigDecimal = 219038743.392874, propertyBoolean = 1, propertyBooleanObject = 0, propertyByte = 89, propertyByteObject = 34, propertyCalendar = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'), propertyChar = 'v', propertyCharacterObject = 'r', propertyDate = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'), propertyDouble = 53348.34, propertyDoubleObject = 143298.692, propertyEnum = 'VALUE_THREE', propertyFloat = 98634.2, propertyFloatObject = 8734.7, propertyInt = 545, propertyIntegerObject = 968, propertyLong = 34563, propertyLongObject = 66875, propertyShort = 43, propertyShortObject = 68, propertySqlDate = TO_DATE('2002/06/18 00:00:00', 'YYYY/MM/DD HH24:MI:SS'), propertyString = 'someotherstring', propertyStringbuffer = 'someotherstringbuff', propertyTime = TO_DATE('15:26:14', 'HH24:MI:SS'), propertyTimestamp = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS') WHERE propertyInt = 545");
		assertTrue(execute(query));
	}

	public void testFieldsBeanConstrainedOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.where("propertyInt = 545")
			.fields(BeanImplConstrained.getPopulatedBean());
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBigDecimal = 219038743.392874, propertyBoolean = 1, propertyBooleanObject = 0, propertyByteObject = 34, propertyCalendar = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'), propertyChar = 'v', propertyCharacterObject = 'r', propertyDate = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'), propertyDouble = 53348.34, propertyDoubleObject = 143298.692, propertyFloat = 98634.2, propertyFloatObject = 8734.7, propertyInt = 545, propertyIntegerObject = 968, propertyLongObject = 66875, propertyShort = 43, propertySqlDate = TO_DATE('2002/06/18 00:00:00', 'YYYY/MM/DD HH24:MI:SS'), propertyString = 'someotherstring', propertyStringbuffer = 'someotherstringbuff', propertyTime = TO_DATE('15:26:14', 'HH24:MI:SS'), propertyTimestamp = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS') WHERE propertyInt = 545");
		assertTrue(execute(query));
	}

	public void testFieldsBeanNullValuesOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.where("propertyInt = 545")
			.fields(BeanImpl.getNullBean());
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 0, propertyBooleanObject = 0, propertyByte = 0, propertyByteObject = 0, propertyDouble = 0.0, propertyDoubleObject = 0.0, propertyFloat = 0.0, propertyFloatObject = 0.0, propertyInt = 0, propertyIntegerObject = 0, propertyLong = 0, propertyLongObject = 0, propertyShort = 0, propertyShortObject = 0 WHERE propertyInt = 545");
		assertTrue(execute(query));
	}

	public void testFieldsBeanIncludedOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.where("propertyInt = 545")
			.fieldsIncluded(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"});
		assertEquals(query.getSql(), "UPDATE tablename SET propertyByte = 89, propertyDouble = 53348.34, propertyShort = 43, propertyStringbuffer = 'someotherstringbuff', propertyTime = TO_DATE('15:26:14', 'HH24:MI:SS') WHERE propertyInt = 545");
		assertTrue(execute(query));
	}

	public void testFieldsBeanExcludedOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.where("propertyInt = 545")
			.fieldsExcluded(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"});
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBigDecimal = 219038743.392874, propertyBoolean = 1, propertyBooleanObject = 0, propertyByteObject = 34, propertyCalendar = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'), propertyChar = 'v', propertyCharacterObject = 'r', propertyDate = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'), propertyDoubleObject = 143298.692, propertyEnum = 'VALUE_THREE', propertyFloat = 98634.2, propertyFloatObject = 8734.7, propertyInt = 545, propertyIntegerObject = 968, propertyLong = 34563, propertyLongObject = 66875, propertyShortObject = 68, propertySqlDate = TO_DATE('2002/06/18 00:00:00', 'YYYY/MM/DD HH24:MI:SS'), propertyString = 'someotherstring', propertyTimestamp = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS') WHERE propertyInt = 545");
		assertTrue(execute(query));
	}

	public void testFieldsBeanFilteredOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.where("propertyInt = 545")
			.fieldsFiltered(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"}, new String[] {"propertyByte", "propertyShort", "propertyTime"});
		assertEquals(query.getSql(), "UPDATE tablename SET propertyDouble = 53348.34, propertyStringbuffer = 'someotherstringbuff' WHERE propertyInt = 545");
		assertTrue(execute(query));
	}

	public void testFieldsParametersBeanOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.fieldsParameters(BeanImpl.class);
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBigDecimal = ?, propertyBoolean = ?, propertyBooleanObject = ?, propertyByte = ?, propertyByteObject = ?, propertyCalendar = ?, propertyChar = ?, propertyCharacterObject = ?, propertyDate = ?, propertyDouble = ?, propertyDoubleObject = ?, propertyEnum = ?, propertyFloat = ?, propertyFloatObject = ?, propertyInt = ?, propertyIntegerObject = ?, propertyLong = ?, propertyLongObject = ?, propertyShort = ?, propertyShortObject = ?, propertySqlDate = ?, propertyString = ?, propertyStringbuffer = ?, propertyTime = ?, propertyTimestamp = ?");
		
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
							   cal.set(2002, 7, 19, 12, 17, 52);
							   cal.set(Calendar.MILLISECOND, 462);
							   statement
								   .setBigDecimal(1, new BigDecimal("98347.876438637"))
								   .setBoolean(2, false)
								   .setBoolean(3, true)
								   .setByte(4, (byte)16)
								   .setByte(5, (byte)72)
								   .setTimestamp(6, new java.sql.Timestamp(cal.getTime().getTime()))
								   .setString(7, "M")
								   .setString(8, "p")
								   .setTimestamp(9, new java.sql.Timestamp(cal.getTime().getTime()))
								   .setDouble(10, 12.3d)
								   .setDouble(11, 68.7d)
								   .setString(12, "VALUE_THREE")
								   .setFloat(13, 13.4f)
								   .setFloat(14, 42.1f)
								   .setInt(15, 92)
								   .setInt(16, 34)
								   .setLong(17, 687L)
								   .setLong(18, 92)
								   .setShort(19, (short)7)
								   .setShort(20, (short)12)
								   .setDate(21, new java.sql.Date(cal.getTime().getTime()))
								   .setString(22, "string'value")
								   .setString(23, "string'value2")
								   .setTime(24, new Time(cal.getTime().getTime()))
								   .setTimestamp(25, new Timestamp(cal.getTime().getTime()));
						   }
					   }));
	}

	public void testFieldsParametersBeanConstrainedOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.fieldsParameters(BeanImplConstrained.class);
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBigDecimal = ?, propertyBoolean = ?, propertyBooleanObject = ?, propertyByteObject = ?, propertyCalendar = ?, propertyChar = ?, propertyCharacterObject = ?, propertyDate = ?, propertyDouble = ?, propertyDoubleObject = ?, propertyFloat = ?, propertyFloatObject = ?, propertyInt = ?, propertyIntegerObject = ?, propertyLongObject = ?, propertyShort = ?, propertySqlDate = ?, propertyString = ?, propertyStringbuffer = ?, propertyTime = ?, propertyTimestamp = ?");

		assertEquals(query.getParameters().getOrderedNames().size(), 21);
		assertEquals(query.getParameters().getOrderedNames().get(0), "propertyBigDecimal");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyBoolean");
		assertEquals(query.getParameters().getOrderedNames().get(2), "propertyBooleanObject");
		assertEquals(query.getParameters().getOrderedNames().get(3), "propertyByteObject");
		assertEquals(query.getParameters().getOrderedNames().get(4), "propertyCalendar");
		assertEquals(query.getParameters().getOrderedNames().get(5), "propertyChar");
		assertEquals(query.getParameters().getOrderedNames().get(6), "propertyCharacterObject");
		assertEquals(query.getParameters().getOrderedNames().get(7), "propertyDate");
		assertEquals(query.getParameters().getOrderedNames().get(8), "propertyDouble");
		assertEquals(query.getParameters().getOrderedNames().get(9), "propertyDoubleObject");
		assertEquals(query.getParameters().getOrderedNames().get(10), "propertyFloat");
		assertEquals(query.getParameters().getOrderedNames().get(11), "propertyFloatObject");
		assertEquals(query.getParameters().getOrderedNames().get(12), "propertyInt");
		assertEquals(query.getParameters().getOrderedNames().get(13), "propertyIntegerObject");
		assertEquals(query.getParameters().getOrderedNames().get(14), "propertyLongObject");
		assertEquals(query.getParameters().getOrderedNames().get(15), "propertyShort");
		assertEquals(query.getParameters().getOrderedNames().get(16), "propertySqlDate");
		assertEquals(query.getParameters().getOrderedNames().get(17), "propertyString");
		assertEquals(query.getParameters().getOrderedNames().get(18), "propertyStringbuffer");
		assertEquals(query.getParameters().getOrderedNames().get(19), "propertyTime");
		assertEquals(query.getParameters().getOrderedNames().get(20), "propertyTimestamp");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {"propertyBigDecimal", "propertyBoolean", "propertyBooleanObject", "propertyByteObject", "propertyCalendar", "propertyChar", "propertyCharacterObject", "propertyDate", "propertyDouble", "propertyDoubleObject", "propertyFloat", "propertyFloatObject", "propertyInt", "propertyIntegerObject", "propertyLongObject", "propertyShort", "propertySqlDate", "propertyString", "propertyStringbuffer", "propertyTime", "propertyTimestamp"}));

		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					Calendar cal = Calendar.getInstance();
					cal.set(2002, 7, 19, 12, 17, 52);
					cal.set(Calendar.MILLISECOND, 462);
					statement
						.setBigDecimal(1, new BigDecimal("98347.876438637"))
						.setBoolean(2, false)
						.setBoolean(3, true)
						.setByte(4, (byte)72)
						.setTimestamp(5, new java.sql.Timestamp(cal.getTime().getTime()))
						.setString(6, "M")
						.setString(7, "p")
						.setTimestamp(8, new java.sql.Timestamp(cal.getTime().getTime()))
						.setDouble(9, 12.3d)
						.setDouble(10, 68.7d)
						.setFloat(11, 13.4f)
						.setFloat(12, 42.1f)
						.setInt(13, 92)
						.setInt(14, 34)
						.setLong(15, 92)
						.setShort(16, (short)7)
						.setDate(17, new java.sql.Date(cal.getTime().getTime()))
						.setString(18, "string'value")
						.setString(19, "string'value2")
						.setTime(20, new Time(cal.getTime().getTime()))
						.setTimestamp(21, new Timestamp(cal.getTime().getTime()));
				}
			}));
	}

	public void testFieldsParametersBeanExcludedOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.fieldsParametersExcluded(BeanImpl.class,
				new String[] {"propertyBoolean", "propertyByte", "propertyChar",
							  "propertyDouble", "propertyInt", "propertyLong",
							  "propertySqlDate", "propertyStringbuffer", "propertyTimestamp"});
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBigDecimal = ?, propertyBooleanObject = ?, propertyByteObject = ?, propertyCalendar = ?, propertyCharacterObject = ?, propertyDate = ?, propertyDoubleObject = ?, propertyEnum = ?, propertyFloat = ?, propertyFloatObject = ?, propertyIntegerObject = ?, propertyLongObject = ?, propertyShort = ?, propertyShortObject = ?, propertyString = ?, propertyTime = ?");
		
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
							   cal.set(2002, 7, 19, 12, 17, 52);
							   cal.set(Calendar.MILLISECOND, 462);
							   statement
								   .setBigDecimal(1, new BigDecimal("98347.876438637"))
								   .setBoolean(2, true)
								   .setByte(3, (byte)72)
								   .setTimestamp(4, new java.sql.Timestamp(cal.getTime().getTime()))
								   .setString(5, "o")
								   .setTimestamp(6, new java.sql.Timestamp(cal.getTime().getTime()))
								   .setDouble(7, 86.7d)
								   .setString(8, "VALUE_THREE")
								   .setFloat(9, 13.4f)
								   .setFloat(10, 32.8f)
								   .setInt(11, 358)
								   .setLong(12, 9680L)
								   .setShort(13, (short)12)
								   .setShort(14, (short)78)
								   .setString(15, "string'value")
								   .setTime(16, new Time(cal.getTime().getTime()));
						   }
					   }));
	}

	public void testWhereBeanOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16)
			.where(BeanImpl.getPopulatedBean());
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyBigDecimal = 219038743.392874 AND propertyBoolean = 1 AND propertyBooleanObject = 0 AND propertyByte = 89 AND propertyByteObject = 34 AND propertyCalendar = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS') AND propertyChar = 'v' AND propertyCharacterObject = 'r' AND propertyDate = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS') AND propertyDouble = 53348.34 AND propertyDoubleObject = 143298.692 AND propertyEnum = 'VALUE_THREE' AND propertyFloat = 98634.2 AND propertyFloatObject = 8734.7 AND propertyInt = 545 AND propertyIntegerObject = 968 AND propertyLong = 34563 AND propertyLongObject = 66875 AND propertyShort = 43 AND propertyShortObject = 68 AND propertySqlDate = TO_DATE('2002/06/18 00:00:00', 'YYYY/MM/DD HH24:MI:SS') AND propertyString = 'someotherstring' AND propertyStringbuffer = 'someotherstringbuff' AND propertyTime = TO_DATE('15:26:14', 'HH24:MI:SS') AND propertyTimestamp = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS')");
		assertTrue(execute(query));
	}

	public void testWhereBeanConstrainedOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16)
			.where(BeanImplConstrained.getPopulatedBean());
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyBigDecimal = 219038743.392874 AND propertyBoolean = 1 AND propertyBooleanObject = 0 AND propertyByte = 89 AND propertyByteObject = 34 AND propertyCalendar = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS') AND propertyChar = 'v' AND propertyCharacterObject = 'r' AND propertyDate = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS') AND propertyDouble = 53348.34 AND propertyDoubleObject = 143298.692 AND propertyFloat = 98634.2 AND propertyFloatObject = 8734.7 AND propertyInt = 545 AND propertyIntegerObject = 968 AND propertyLongObject = 66875 AND propertyShort = 43 AND propertySqlDate = TO_DATE('2002/06/18 00:00:00', 'YYYY/MM/DD HH24:MI:SS') AND propertyString = 'someotherstring' AND propertyStringbuffer = 'someotherstringbuff' AND propertyTime = TO_DATE('15:26:14', 'HH24:MI:SS') AND propertyTimestamp = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS')");
		assertTrue(execute(query));
	}

	public void testWhereBeanNullValuesOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16)
			.where(BeanImpl.getNullBean());
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyBoolean = 0 AND propertyBooleanObject = 0 AND propertyByte = 0 AND propertyByteObject = 0 AND propertyDouble = 0.0 AND propertyDoubleObject = 0.0 AND propertyFloat = 0.0 AND propertyFloatObject = 0.0 AND propertyInt = 0 AND propertyIntegerObject = 0 AND propertyLong = 0 AND propertyLongObject = 0 AND propertyShort = 0 AND propertyShortObject = 0");
		assertTrue(execute(query));
	}

	public void testWhereBeanIncludedOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16)
			.whereIncluded(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"});
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyByte = 89 AND propertyDouble = 53348.34 AND propertyShort = 43 AND propertyStringbuffer = 'someotherstringbuff' AND propertyTime = TO_DATE('15:26:14', 'HH24:MI:SS')");
		assertTrue(execute(query));
	}

	public void testWhereBeanExcludedOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16)
			.whereExcluded(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"});
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyBigDecimal = 219038743.392874 AND propertyBoolean = 1 AND propertyBooleanObject = 0 AND propertyByteObject = 34 AND propertyCalendar = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS') AND propertyChar = 'v' AND propertyCharacterObject = 'r' AND propertyDate = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS') AND propertyDoubleObject = 143298.692 AND propertyEnum = 'VALUE_THREE' AND propertyFloat = 98634.2 AND propertyFloatObject = 8734.7 AND propertyInt = 545 AND propertyIntegerObject = 968 AND propertyLong = 34563 AND propertyLongObject = 66875 AND propertyShortObject = 68 AND propertySqlDate = TO_DATE('2002/06/18 00:00:00', 'YYYY/MM/DD HH24:MI:SS') AND propertyString = 'someotherstring' AND propertyTimestamp = TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS')");
		assertTrue(execute(query));
	}

	public void testWhereBeanFilteredOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16)
			.whereFiltered(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"}, new String[] {"propertyByte", "propertyShort", "propertyTime"});
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyDouble = 53348.34 AND propertyStringbuffer = 'someotherstringbuff'");
		assertTrue(execute(query));
	}

	public void testWhereParametersBeanOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16)
			.whereParameters(BeanImpl.class);
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyBigDecimal = ? AND propertyBoolean = ? AND propertyBooleanObject = ? AND propertyByte = ? AND propertyByteObject = ? AND propertyCalendar = ? AND propertyChar = ? AND propertyCharacterObject = ? AND propertyDate = ? AND propertyDouble = ? AND propertyDoubleObject = ? AND propertyEnum = ? AND propertyFloat = ? AND propertyFloatObject = ? AND propertyInt = ? AND propertyIntegerObject = ? AND propertyLong = ? AND propertyLongObject = ? AND propertyShort = ? AND propertyShortObject = ? AND propertySqlDate = ? AND propertyString = ? AND propertyStringbuffer = ? AND propertyTime = ? AND propertyTimestamp = ?");
		
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
		
		// don't check if actual rows were returned, since Oracle doesn't
		// match on the date types
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
					   });
	}

	public void testWhereParametersBeanConstrainedOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16)
			.whereParameters(BeanImplConstrained.class);
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyBigDecimal = ? AND propertyBoolean = ? AND propertyBooleanObject = ? AND propertyByte = ? AND propertyByteObject = ? AND propertyCalendar = ? AND propertyChar = ? AND propertyCharacterObject = ? AND propertyDate = ? AND propertyDouble = ? AND propertyDoubleObject = ? AND propertyFloat = ? AND propertyFloatObject = ? AND propertyInt = ? AND propertyIntegerObject = ? AND propertyLongObject = ? AND propertyShort = ? AND propertySqlDate = ? AND propertyString = ? AND propertyStringbuffer = ? AND propertyTime = ? AND propertyTimestamp = ?");

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

		// don't check if actual rows were returned, since Oracle doesn't
		// match on the date types
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

	public void testWhereParametersBeanExcludedOracle()
	{
		Update query = new Update(mOracle);
		query.table("tablename")
			.field("propertyBoolean", true)
			.field("propertyByte", (byte)16)
			.whereParametersExcluded(BeanImpl.class,
				new String[] {"propertyBoolean", "propertyByte", "propertyChar",
							  "propertyDouble", "propertyInt", "propertyLong",
							  "propertySqlDate", "propertyStringbuffer", "propertyTimestamp",
							  "propertyCalendar", "propertyDate", "propertyTime"});
		assertEquals(query.getSql(), "UPDATE tablename SET propertyBoolean = 1, propertyByte = 16 WHERE propertyBigDecimal = ? AND propertyBooleanObject = ? AND propertyByteObject = ? AND propertyCharacterObject = ? AND propertyDoubleObject = ? AND propertyEnum = ? AND propertyFloat = ? AND propertyFloatObject = ? AND propertyIntegerObject = ? AND propertyLongObject = ? AND propertyShort = ? AND propertyShortObject = ? AND propertyString = ?");

		assertEquals(query.getParameters().getOrderedNames().size(), 13);
		assertEquals(query.getParameters().getOrderedNames().get(0), "propertyBigDecimal");
		assertEquals(query.getParameters().getOrderedNames().get(1), "propertyBooleanObject");
		assertEquals(query.getParameters().getOrderedNames().get(2), "propertyByteObject");
		assertEquals(query.getParameters().getOrderedNames().get(3), "propertyCharacterObject");
		assertEquals(query.getParameters().getOrderedNames().get(4), "propertyDoubleObject");
		assertEquals(query.getParameters().getOrderedNames().get(5), "propertyEnum");
		assertEquals(query.getParameters().getOrderedNames().get(6), "propertyFloat");
		assertEquals(query.getParameters().getOrderedNames().get(7), "propertyFloatObject");
		assertEquals(query.getParameters().getOrderedNames().get(8), "propertyIntegerObject");
		assertEquals(query.getParameters().getOrderedNames().get(9), "propertyLongObject");
		assertEquals(query.getParameters().getOrderedNames().get(10), "propertyShort");
		assertEquals(query.getParameters().getOrderedNames().get(11), "propertyShortObject");
		assertEquals(query.getParameters().getOrderedNames().get(12), "propertyString");
		assertTrue(Arrays.equals(query.getParameters().getOrderedNamesArray(), new String[] {"propertyBigDecimal", "propertyBooleanObject", "propertyByteObject", "propertyCharacterObject", "propertyDoubleObject", "propertyEnum", "propertyFloat", "propertyFloatObject", "propertyIntegerObject", "propertyLongObject", "propertyShort", "propertyShortObject", "propertyString"}));

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
						.setString(4, "r")
						.setDouble(5, 143298.692d)
						.setString(6, "VALUE_THREE")						
						.setFloat(7, 98634.2f)
						.setFloat(8, 8734.7f)
						.setInt(9, 968)
						.setLong(10, 66875L)
						.setShort(11, (short)43)
						.setShort(12, (short)68)
						.setString(13, "someotherstring");
				}
			}));
	}

	public void testSubselectParamsOracle()
	{
		Select fieldquery = new Select(mOracle);
		fieldquery
			.from("table2")
			.field("max(propertyLong)")
			.whereParameter("propertyInt", ">");
		Select wherequery = new Select(mOracle);
		wherequery
			.from("table2")
			.field("max(propertyShort)")
			.whereParameter("propertyShort", "!=");
		Update query = new Update(mOracle);
		// shuffled the structure around a bit to test the correct order usage
		query
			.where("propertyShort >= ("+wherequery+")")
			.whereSubselect(wherequery)
			.whereParameterOr("propertyString", "propertyString2", "=")
			.table("tablename")
			.fieldParameter("propertyString")
			.fieldCustom("propertyLong", "("+fieldquery+")")
			.fieldSubselect(fieldquery);
		assertEquals(query.getSql(), "UPDATE tablename SET propertyString = ?, propertyLong = (SELECT max(propertyLong) FROM table2 WHERE propertyInt > ?) WHERE propertyShort >= (SELECT max(propertyShort) FROM table2 WHERE propertyShort != ?) OR propertyString = ?");
		String[] parameters = query.getParameters().getOrderedNamesArray();
		assertEquals(4, parameters.length);
		assertEquals(parameters[0], "propertyString");
		assertEquals(parameters[1], "propertyInt");
		assertEquals(parameters[2], "propertyShort");
		assertEquals(parameters[3], "propertyString2");

		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setString("propertyString", "thestring")
						.setInt("propertyInt", 1)
						.setShort("propertyShort", (short)5)
						.setString("propertyString2", "anotherstring");
				}
			}));

		// Automated subselect creation
		query = new Update(mOracle);
		// shuffled the structure around a bit to test the correct order usage
		query
			.where("propertyShort", ">=", wherequery)
			.whereParameterOr("propertyString", "propertyString2", "=")
			.whereOr("tablename.propertyFloat", ">", new Select(mOracle)
				.from("table2")
				.field("max(propertyLong)")
				.whereParameter("propertyLong", "!="))
			.whereAnd("tablename.propertyDouble", "<=", new Select(mOracle)
				.from("table2")
				.field("max(propertyFloat)")
				.whereParameter("propertyFloat", "!="))
			.table("tablename")
			.fieldParameter("propertyString")
			.field("propertyLong", fieldquery);
		assertEquals(query.getSql(), "UPDATE tablename SET propertyString = ?, propertyLong = (SELECT max(propertyLong) FROM table2 WHERE propertyInt > ?) WHERE propertyShort >= (SELECT max(propertyShort) FROM table2 WHERE propertyShort != ?) OR propertyString = ? OR tablename.propertyFloat > (SELECT max(propertyLong) FROM table2 WHERE propertyLong != ?) AND tablename.propertyDouble <= (SELECT max(propertyFloat) FROM table2 WHERE propertyFloat != ?)");
		parameters = query.getParameters().getOrderedNamesArray();
		assertEquals(6, parameters.length);
		assertEquals(parameters[0], "propertyString");
		assertEquals(parameters[1], "propertyInt");
		assertEquals(parameters[2], "propertyShort");
		assertEquals(parameters[3], "propertyString2");

		assertTrue(execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setString("propertyString", "thestring")
						.setInt("propertyInt", 1)
						.setShort("propertyShort", (short)5)
						.setString("propertyString2", "anotherstring")
						.setLong("propertyLong", 99999999)
						.setFloat("propertyFloat", -1f);
				}
			}));
	}

	public void testCloneOracle()
	{
		Select fieldquery = new Select(mOracle);
		fieldquery
			.from("table2")
			.field("max(propertyLong)")
			.whereParameter("propertyInt", ">");
		Select wherequery = new Select(mOracle);
		wherequery
			.from("table2")
			.field("max(propertyShort)")
			.whereParameter("propertyShort", "!=");

		final Calendar cal = Calendar.getInstance();
		cal.set(2002, 7, 19, 12, 17, 52);
		cal.set(Calendar.MILLISECOND, 462);
		Update query = new Update(mOracle);
		query.table("tablename")
			.hint("NO_INDEX")
			.fieldParameter("nullColumn")
			.field("propertyBigDecimal", new BigDecimal("98347.876438637"))
			.fieldParameter("propertyBoolean")
			.fieldParameter("propertyByte")
			.field("propertyCalendar", cal.getTime())
			.fieldParameter("propertyChar")
			.field("propertyDate", cal)
			.field("propertyDouble", 12.3d)
			.fieldParameter("propertyFloat")
			.fieldParameter("propertyInt")
			.field("propertyShort", (short)12)
			.fieldParameter("propertySqlDate")
			.fieldParameter("propertyString")
			.field("propertyStringbuffer", new StringBuffer("stringbuffer'value"))
			.field("propertyTime", new Time(cal.getTime().getTime()))
			.fieldParameter("propertyTimestamp")
			.fieldCustom("propertyLong", "("+fieldquery+")")
			.fieldSubselect(fieldquery)
			.where("tablename.propertyShort >= ("+wherequery+")")
			.whereSubselect(wherequery)
			.whereParameterOr("tablename.propertyString", "propertyString", "=")
			.whereOr("tablename.propertyByte", "=", (byte)54)
			.whereAnd("tablename.propertyDouble", "!=", 73453.71d)
			.whereParameterOr("tablename.propertyInt", "propertyInt", "=")
			.whereParameterAnd("tablename.propertyLong", "propertyLong", "<")
			.whereParameterOr("tablename.propertyChar", "propertyChar", "=");

		Update query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		execute(query, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setString("nullColumn", null)
						.setBoolean("propertyBoolean", true)
						.setByte("propertyByte", (byte)16)
						.setString("propertyChar", "M")
						.setFloat("propertyFloat", 13.4f)
						.setInt("propertyInt", 34)
						.setDate("propertySqlDate", new java.sql.Date(cal.getTime().getTime()))
						.setString("propertyString", "string'value")
						.setTimestamp("propertyTimestamp", new Timestamp(cal.getTime().getTime()))
						.setShort("propertyShort", (short)4)
						.setLong("propertyLong", 34543);
				}
			});
	}
}
