/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestInsertOracle.java 3938 2008-04-26 20:08:37Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.BeanImpl;
import com.uwyn.rife.database.BeanImplConstrained;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.exceptions.FieldsRequiredException;
import com.uwyn.rife.database.exceptions.TableNameRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;
import com.uwyn.rife.database.types.SqlNull;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;

public class TestInsertOracle extends TestInsert
{
	public TestInsertOracle(String name)
	{
		super(name);
	}

	public void testInstantiationOracle()
	{
		Insert query = new Insert(mOracle);
		assertNotNull(query);
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "Insert");
		}
	}

	public void testIncompleteQueryOracle()
	{
		Insert query = new Insert(mOracle);
		try
		{
			query.getSql();
			fail();
		}
		catch (TableNameRequiredException e)
		{
			assertEquals(e.getQueryName(), "Insert");
		}
		query.into("tablename");
		try
		{
			query.getSql();
			fail();
		}
		catch (FieldsRequiredException e)
		{
			assertEquals(e.getQueryName(), "Insert");
		}
		query.field("col1", "val1");
		assertNotNull(query.getSql());
	}

	public void testClearOracle()
	{
		Insert query = new Insert(mOracle);
		query.into("tablename")
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
			assertEquals(e.getQueryName(), "Insert");
		}
 	}

	public void testHintOracle()
	{
		Insert query = new Insert(mOracle)
			.hint("APPEND")
			.into("tablename")
			.field("propertyDouble", 12.3d);
		assertEquals(query.getSql(), "INSERT /*+ APPEND */ INTO tablename (propertyDouble) VALUES (12.3)");
		assertTrue(execute(query));
	}

	public void testParameterOracle()
	{
		Insert query = new Insert(mOracle);
		query.into("tablename")
			.fieldParameter("col1");
		assertEquals(query.getSql(), "INSERT INTO tablename (col1) VALUES (?)");
	}

	public void testFieldOracle()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2002, 7, 19, 12, 17, 52);
		Insert query = new Insert(mOracle);
		query.into("tablename")
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
		assertEquals(query.getSql(), "INSERT INTO tablename (nullColumn, propertyBigDecimal, propertyBoolean, propertyByte, propertyCalendar, propertyChar, propertyDate, propertyDouble, propertyFloat, propertyInt, propertyLong, propertyShort, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp) VALUES (NULL, 98347.876438637, 1, 16, TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS'), 'M', TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS'), 12.3, 13.4, 34, 45, 12, TO_DATE('2002/08/19 00:00:00', 'YYYY/MM/DD HH24:MI:SS'), 'string''value', 'stringbuffer''value', TO_DATE('12:17:52', 'HH24:MI:SS'), TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS'))");
		assertTrue(execute(query));
	}

	public void testFieldCustomOracle()
	{
		Insert query = new Insert(mOracle);
		query.into("tablename")
			.fieldCustom("propertySqlDate", "(SELECT sysdate FROM dual)");
		assertEquals(query.getSql(), "INSERT INTO tablename (propertySqlDate) VALUES ((SELECT sysdate FROM dual))");
		assertTrue(execute(query));
	}

	public void testFieldsOracle()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2002, 7, 19, 12, 17, 52);
		Insert query = new Insert(mOracle);
		query.into("tablename")
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
		assertEquals(query.getSql(), "INSERT INTO tablename (nullColumn, propertyBigDecimal, propertyBoolean, propertyByte, propertyCalendar, propertyChar, propertyDate, propertyDouble, propertyFloat, propertyInt, propertyLong, propertyShort, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp) VALUES (NULL, 98347.876438637, 1, 16, TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS'), 'M', TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS'), 12.3, 13.4, 34, 45, 12, TO_DATE('2002/08/19 00:00:00', 'YYYY/MM/DD HH24:MI:SS'), 'string''value', 'stringbuffer''value', TO_DATE('12:17:52', 'HH24:MI:SS'), TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS'))");
		assertTrue(execute(query));
	}

	public void testFieldParametersOracle()
	{
		Insert query = new Insert(mOracle);
		query.into("tablename");

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

		assertEquals(query.getSql(), "INSERT INTO tablename (nullColumn, propertyBigDecimal, propertyBoolean, propertyByte, propertyCalendar, propertyChar, propertyDate, propertyDouble, propertyFloat, propertyInt, propertyLong, propertyShort, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

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
		Insert query = new Insert(mOracle);
		query.into("tablename");

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

		assertEquals(query.getSql(), "INSERT INTO tablename (nullColumn, propertyBigDecimal, propertyBoolean, propertyByte, propertyCalendar, propertyChar, propertyDate, propertyDouble, propertyFloat, propertyInt, propertyLong, propertyShort, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp) VALUES (?, 98347.876438637, ?, ?, TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS'), ?, TO_DATE('2002/08/19 12:17:52', 'YYYY/MM/DD HH24:MI:SS'), 12.3, ?, ?, 45, 12, ?, ?, 'stringbuffer''value', TO_DATE('12:17:52', 'HH24:MI:SS'), ?)");

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

	public void testFieldsBeanOracle()
	{
		Insert query = new Insert(mOracle);
		query.into("tablename")
			.fields(BeanImpl.getPopulatedBean());
		assertEquals(query.getSql(), "INSERT INTO tablename (propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyCalendar, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyEnum, propertyFloat, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShort, propertyShortObject, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp) VALUES (219038743.392874, 1, 0, 89, 34, TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'), 'v', 'r', TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'), 53348.34, 143298.692, 'VALUE_THREE', 98634.2, 8734.7, 545, 968, 34563, 66875, 43, 68, TO_DATE('2002/06/18 00:00:00', 'YYYY/MM/DD HH24:MI:SS'), 'someotherstring', 'someotherstringbuff', TO_DATE('15:26:14', 'HH24:MI:SS'), TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'))");
		assertTrue(execute(query));
	}

	public void testFieldsBeanConstrainedOracle()
	{
		Insert query = new Insert(mOracle);
		query.into("tablename")
			.fields(BeanImplConstrained.getPopulatedBean());
		assertEquals(query.getSql(), "INSERT INTO tablename (propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByteObject, propertyCalendar, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyFloat, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLongObject, propertyShort, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp) VALUES (219038743.392874, 1, 0, 34, TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'), 'v', 'r', TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'), 53348.34, 143298.692, 98634.2, 8734.7, 545, 968, 66875, 43, TO_DATE('2002/06/18 00:00:00', 'YYYY/MM/DD HH24:MI:SS'), 'someotherstring', 'someotherstringbuff', TO_DATE('15:26:14', 'HH24:MI:SS'), TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'))");
		assertTrue(execute(query));
	}

	public void testFieldsBeanNullValuesOracle()
	{
		Insert query = new Insert(mOracle);
		query.into("tablename")
			.fields(BeanImpl.getNullBean());
		assertEquals(query.getSql(), "INSERT INTO tablename (propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyDouble, propertyDoubleObject, propertyFloat, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShort, propertyShortObject) VALUES (0, 0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0)");
		assertTrue(execute(query));
	}

	public void testFieldsBeanIncludedOracle()
	{
		Insert query = new Insert(mOracle);
		query.into("tablename")
			.fieldsIncluded(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"});
		assertEquals(query.getSql(), "INSERT INTO tablename (propertyByte, propertyDouble, propertyShort, propertyStringbuffer, propertyTime) VALUES (89, 53348.34, 43, 'someotherstringbuff', TO_DATE('15:26:14', 'HH24:MI:SS'))");
		assertTrue(execute(query));
	}

	public void testFieldsBeanExcludedOracle()
	{
		Insert query = new Insert(mOracle);
		query.into("tablename")
			.fieldsExcluded(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"});
		assertEquals(query.getSql(), "INSERT INTO tablename (propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByteObject, propertyCalendar, propertyChar, propertyCharacterObject, propertyDate, propertyDoubleObject, propertyEnum, propertyFloat, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShortObject, propertySqlDate, propertyString, propertyTimestamp) VALUES (219038743.392874, 1, 0, 34, TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'), 'v', 'r', TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'), 143298.692, 'VALUE_THREE', 98634.2, 8734.7, 545, 968, 34563, 66875, 68, TO_DATE('2002/06/18 00:00:00', 'YYYY/MM/DD HH24:MI:SS'), 'someotherstring', TO_DATE('2002/06/18 15:26:14', 'YYYY/MM/DD HH24:MI:SS'))");
		assertTrue(execute(query));
	}

	public void testFieldsBeanFilteredOracle()
	{
		Insert query = new Insert(mOracle);
		query.into("tablename")
			.fieldsFiltered(BeanImpl.getPopulatedBean(), new String[] {"propertyByte", "propertyDouble", "propertyShort", "propertyStringbuffer", "propertyTime"}, new String[] {"propertyByte", "propertyShort", "propertyTime"});
		assertEquals(query.getSql(), "INSERT INTO tablename (propertyDouble, propertyStringbuffer) VALUES (53348.34, 'someotherstringbuff')");
		assertTrue(execute(query));
	}

	public void testMultipleRowsOracle()
	{
		Insert query = new Insert(mOracle);
		query.into("tablename")
			.field("propertyChar", 'M')
			.field("propertyDouble", 12.3d)
			.field("propertyFloat", 13.4f)
			.field("propertyInt", 34);
		query.field("propertyChar", 'S')
			.field("propertyDouble", 45.1d)
			.field("propertyFloat", 27.9f);
		query.field("propertyChar", 'T');
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

	public void testFieldsParametersBeanOracle()
	{
		Insert query = new Insert(mOracle);
		query.into("tablename")
			.fieldsParameters(BeanImpl.class);
		assertEquals(query.getSql(), "INSERT INTO tablename (propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByte, propertyByteObject, propertyCalendar, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyEnum, propertyFloat, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLong, propertyLongObject, propertyShort, propertyShortObject, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

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
		Insert query = new Insert(mOracle);
		query.into("tablename")
			.fieldsParameters(BeanImplConstrained.class);
		assertEquals(query.getSql(), "INSERT INTO tablename (propertyBigDecimal, propertyBoolean, propertyBooleanObject, propertyByteObject, propertyCalendar, propertyChar, propertyCharacterObject, propertyDate, propertyDouble, propertyDoubleObject, propertyFloat, propertyFloatObject, propertyInt, propertyIntegerObject, propertyLongObject, propertyShort, propertySqlDate, propertyString, propertyStringbuffer, propertyTime, propertyTimestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		
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
		Insert query = new Insert(mOracle);
		query.into("tablename")
			.fieldsParametersExcluded(BeanImpl.class,
				new String[] {"propertyBoolean", "propertyByte", "propertyChar",
							  "propertyDouble", "propertyInt", "propertyLong",
							  "propertySqlDate", "propertyStringbuffer", "propertyTimestamp"});
		assertEquals(query.getSql(), "INSERT INTO tablename (propertyBigDecimal, propertyBooleanObject, propertyByteObject, propertyCalendar, propertyCharacterObject, propertyDate, propertyDoubleObject, propertyEnum, propertyFloat, propertyFloatObject, propertyIntegerObject, propertyLongObject, propertyShort, propertyShortObject, propertyString, propertyTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

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
	
	public void testInsertSubselectParamsOracle()
	{
		Select fieldquery = new Select(mOracle);
		fieldquery
			.from("table2")
			.field("max(propertyLong)")
			.whereParameter("propertyInt", ">");
		
		// Manual subselect creation
		Insert query = new Insert(mOracle);
		// shuffled the structure around a bit to test the correct order usage
		query
			.into("tablename")
			.fieldParameter("propertyString")
			.fieldCustom("propertyLong", "("+fieldquery+")")
			.fieldSubselect(fieldquery);
		assertEquals(query.getSql(), "INSERT INTO tablename (propertyString, propertyLong) VALUES (?, (SELECT max(propertyLong) FROM table2 WHERE propertyInt > ?))");
		String[] parameters = query.getParameters().getOrderedNamesArray();
		assertEquals(2, parameters.length);
		assertEquals(parameters[0], "propertyString");
		assertEquals(parameters[1], "propertyInt");
		assertTrue(execute(query, new DbPreparedStatementHandler() {
						   public void setParameters(DbPreparedStatement statement)
						   {
							   statement
								   .setString("propertyString", "thestring")
								   .setLong("propertyInt", 90);
						   }
					   }));
		
		// Automated subselect creation
		query = new Insert(mOracle);
		// shuffled the structure around a bit to test the correct order usage
		query
			.into("tablename")
			.fieldParameter("propertyString")
			.field("propertyLong", fieldquery);
		assertEquals(query.getSql(), "INSERT INTO tablename (propertyString, propertyLong) VALUES (?, (SELECT max(propertyLong) FROM table2 WHERE propertyInt > ?))");
		parameters = query.getParameters().getOrderedNamesArray();
		assertEquals(2, parameters.length);
		assertEquals(parameters[0], "propertyString");
		assertEquals(parameters[1], "propertyInt");
		assertTrue(execute(query, new DbPreparedStatementHandler() {
						   public void setParameters(DbPreparedStatement statement)
						   {
							   statement
								   .setString("propertyString", "thestring")
								   .setLong("propertyInt", 90);
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

		final Calendar cal = Calendar.getInstance();
		cal.set(2002, 7, 19, 12, 17, 52);
		cal.set(Calendar.MILLISECOND, 462);
		Insert query = new Insert(mOracle);
		query
			.hint("APPEND")
			.into("tablename")
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
			.fieldSubselect(fieldquery);

		Insert query_clone = query.clone();
		assertEquals(query.getSql(), query_clone.getSql());
		assertTrue(query != query_clone);
		assertTrue(execute(query, new DbPreparedStatementHandler() {
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
						.setTimestamp("propertyTimestamp", new Timestamp(cal.getTime().getTime()));
				}
			}));
	}
}

