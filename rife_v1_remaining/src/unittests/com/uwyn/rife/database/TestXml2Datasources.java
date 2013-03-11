/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestXml2Datasources.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.exceptions.DatasourcesException;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.ExceptionUtils;
import junit.framework.TestCase;

public class TestXml2Datasources extends TestCase
{
	public TestXml2Datasources(String name)
	{
		super(name);
	}

	public void testParser()
	{
		Datasources datasources = null;
		try
		{
			datasources = new Datasources("xml/test_xml2datasources.xml", ResourceFinderClasspath.getInstance());
			
			assertEquals(4, datasources.getDatasourceNames().size());
			
			assertTrue(datasources.getDatasourceNames().contains("datasource1"));
			assertTrue(datasources.getDatasourceNames().contains("datasource2"));
			assertTrue(datasources.getDatasourceNames().contains("datasource3"));
			
			Datasource datasource1 = datasources.getDatasource("datasource1");
			assertEquals("org.postgresql.Driver", datasource1.getDriver());
			assertEquals("org.postgresql.Driver", datasource1.getAliasedDriver());
			assertEquals("jdbc:postgresql://localhost:5432/thedb", datasource1.getUrl());
			assertEquals("unittests1", datasource1.getUser());
			assertEquals("password1", datasource1.getPassword());
			assertEquals(5, datasource1.getPoolsize());
			assertTrue(datasource1.isPooled());
			
			Datasource datasource2 = datasources.getDatasource("datasource2");
			assertEquals("com.mysql.jdbc.Driver", datasource2.getDriver());
			assertEquals("com.mysql.jdbc.Driver", datasource2.getAliasedDriver());
			assertEquals("jdbc:mysql://localhost:3306/thedb", datasource2.getUrl());
			assertEquals("user2", datasource2.getUser());
			assertEquals("pw2", datasource2.getPassword());
			assertEquals(15, datasource2.getPoolsize());
			assertTrue(datasource2.isPooled());
			
			Datasource datasource3 = datasources.getDatasource("datasource3");
			assertEquals("oracle.jdbc.driver.OracleDriver", datasource3.getDriver());
			assertEquals("oracle.jdbc.driver.OracleDriver", datasource3.getAliasedDriver());
			assertEquals("jdbc:oracle:thin:@10.1.1.2:1521:database", datasource3.getUrl());
			assertEquals("unittests", datasource3.getUser());
			assertEquals("password", datasource3.getPassword());
			assertEquals(0, datasource3.getPoolsize());
			assertTrue(!datasource3.isPooled());
			
			Datasource datasource4 = datasources.getDatasource("datasource4");
			assertEquals("org.gjt.mm.mysql.Driver", datasource4.getDriver());
			assertEquals("com.mysql.jdbc.Driver", datasource4.getAliasedDriver());
			assertEquals("jdbc:mysql://localhost:3306/thedb", datasource4.getUrl());
			assertEquals("user2", datasource4.getUser());
			assertEquals("pw2", datasource4.getPassword());
			assertEquals(15, datasource4.getPoolsize());
			assertTrue(datasource4.isPooled());
		}
		catch (DatasourcesException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testSelectedShortClassname()
	{
		Datasources datasources = null;
		
		try
		{
			datasources = new Datasources("TestSelectorDatasources", ResourceFinderClasspath.getInstance());
		}
		catch (DatasourcesException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertTrue(null != datasources.getDatasource("datasource1"));
	}
	
	public void testSelectedFullClassname()
	{
		Datasources datasources = null;
		
		try
		{
			datasources = new Datasources("com.uwyn.rife.selector.TestSelectorDatasources", ResourceFinderClasspath.getInstance());
		}
		catch (DatasourcesException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertTrue(null != datasources.getDatasource("datasource1"));
	}
	
	public void testUnavailableXmlFile()
	{
		Datasources datasources = null;
		
		try
		{
			datasources = new Datasources("xml/this_file_is_not_there.xml", ResourceFinderClasspath.getInstance());
			fail();
			assertNotNull(datasources);
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		catch (DatasourcesException e)
		{
			assertTrue(true);
		}
	}
}
