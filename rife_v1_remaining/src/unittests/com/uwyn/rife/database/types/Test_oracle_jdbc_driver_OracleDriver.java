/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Test_oracle_jdbc_driver_OracleDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.types;

import com.uwyn.rife.database.SomeEnum;
import com.uwyn.rife.database.types.databasedrivers.oracle_jdbc_driver_OracleDriver;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;

public class Test_oracle_jdbc_driver_OracleDriver extends TestCase
{
	public Test_oracle_jdbc_driver_OracleDriver(String name)
	{
		super(name);
	}

	public void testGetSqlValue()
	{
		SqlConversion dbtypes = null;
		dbtypes = new oracle_jdbc_driver_OracleDriver();

		assertNotNull(dbtypes);
		assertEquals(dbtypes.getSqlValue(null), "NULL");
		assertEquals(dbtypes.getSqlValue("fdjh'kjhsdf'"), "'fdjh''kjhsdf'''");
		assertEquals(dbtypes.getSqlValue(new StringBuffer("kkdfkj'jfoodf")), "'kkdfkj''jfoodf'");
		assertEquals(dbtypes.getSqlValue(new Character('K')), "'K'");
		assertEquals(dbtypes.getSqlValue(new Character('\'')), "''''");
		Calendar cal = Calendar.getInstance();
		cal.set(2002, 05, 18, 18, 45, 40);
		assertEquals(dbtypes.getSqlValue(new Time(cal.getTime().getTime())), "TO_DATE('18:45:40', 'HH24:MI:SS')");
		assertEquals(dbtypes.getSqlValue(new Timestamp(cal.getTime().getTime())), "TO_DATE('2002/06/18 18:45:40', 'YYYY/MM/DD HH24:MI:SS')");
		assertEquals(dbtypes.getSqlValue(new java.sql.Date(cal.getTime().getTime())), "TO_DATE('2002/06/18 00:00:00', 'YYYY/MM/DD HH24:MI:SS')");
		assertEquals(dbtypes.getSqlValue(new Date(cal.getTime().getTime())), "TO_DATE('2002/06/18 18:45:40', 'YYYY/MM/DD HH24:MI:SS')");
		assertEquals(dbtypes.getSqlValue(cal), "TO_DATE('2002/06/18 18:45:40', 'YYYY/MM/DD HH24:MI:SS')");
		assertEquals(dbtypes.getSqlValue(new String[] {"kjkdf", "fdfdf", "ljkldfd"}), "{'kjkdf','fdfdf','ljkldfd'}");
		assertEquals(dbtypes.getSqlValue(new Boolean(true)), "1");
		assertEquals(dbtypes.getSqlValue(SomeEnum.VALUE_TWO), "'VALUE_TWO'");
	}
}
