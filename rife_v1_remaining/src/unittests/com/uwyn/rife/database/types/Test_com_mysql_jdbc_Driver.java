/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Test_com_mysql_jdbc_Driver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.types;

import com.uwyn.rife.database.SomeEnum;
import com.uwyn.rife.database.types.databasedrivers.com_mysql_jdbc_Driver;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;

public class Test_com_mysql_jdbc_Driver extends TestCase
{
	public Test_com_mysql_jdbc_Driver(String name)
	{
		super(name);
	}

	public void testGetSqlValue()
	{
		SqlConversion dbtypes = null;
		dbtypes = new com_mysql_jdbc_Driver();

		assertNotNull(dbtypes);
		assertEquals(dbtypes.getSqlValue(null), "NULL");
		assertEquals(dbtypes.getSqlValue("fdjh'kjhsdf'"), "'fdjh''kjhsdf'''");
		assertEquals(dbtypes.getSqlValue(new StringBuffer("kkdfkj'jfoodf")), "'kkdfkj''jfoodf'");
		assertEquals(dbtypes.getSqlValue(new Character('K')), "'K'");
		assertEquals(dbtypes.getSqlValue(new Character('\'')), "''''");
		Calendar cal = Calendar.getInstance();
		cal.set(2002, 05, 18, 18, 45, 40);
		assertEquals(dbtypes.getSqlValue(new Time(cal.getTime().getTime())), "'18:45:40'");
		assertEquals(dbtypes.getSqlValue(new Timestamp(cal.getTime().getTime())), "'2002-06-18 18:45:40.0'");
		assertEquals(dbtypes.getSqlValue(new java.sql.Date(cal.getTime().getTime())), "'2002-06-18'");
		assertEquals(dbtypes.getSqlValue(new Date(cal.getTime().getTime())), "'2002-06-18 18:45:40.0'");
		assertEquals(dbtypes.getSqlValue(cal), "'2002-06-18 18:45:40.0'");
		assertEquals(dbtypes.getSqlValue(new String[] {"kjkdf", "fdfdf", "ljkldfd"}), "{'kjkdf','fdfdf','ljkldfd'}");
		assertEquals(dbtypes.getSqlValue(new Boolean(true)), "1");
		assertEquals(dbtypes.getSqlValue(SomeEnum.VALUE_TWO), "'VALUE_TWO'");
	}
}
