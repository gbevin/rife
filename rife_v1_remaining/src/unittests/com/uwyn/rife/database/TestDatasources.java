/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatasources.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.exceptions.DatasourcesException;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.ExceptionUtils;
import java.io.File;
import junit.framework.TestCase;

public class TestDatasources extends TestCase
{
	public TestDatasources(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		Datasources datasources = null;
		try
		{
			datasources = new Datasources("xml/test_xml2datasources.xml", ResourceFinderClasspath.getInstance());
		}
		catch (DatasourcesException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertNotNull(datasources);
	}

	public void testRepInstance()
	{
		assertTrue(Datasources.hasRepInstance());
		assertNotNull(Datasources.getRepInstance());
	}

	public void testToXml()
	{
		try
		{
			Datasources datasources = new Datasources("xml/test_xml2datasources.xml", ResourceFinderClasspath.getInstance());
			assertTrue(datasources.toXml().length() > 0);
			assertEquals(datasources.toXml(), "<datasources>\n"+
				"\t<datasource name=\"datasource1\">\n"+
				"\t\t<driver>org.postgresql.Driver</driver>\n"+
				"\t\t<url>jdbc:postgresql://localhost:5432/thedb</url>\n"+
				"\t\t<user>unittests1</user>\n"+
				"\t\t<password>password1</password>\n"+
				"\t\t<poolsize>5</poolsize>\n"+
				"\t</datasource>\n"+
				"\t<datasource name=\"datasource2\">\n"+
				"\t\t<driver>com.mysql.jdbc.Driver</driver>\n"+
				"\t\t<url>jdbc:mysql://localhost:3306/thedb</url>\n"+
				"\t\t<user>user2</user>\n"+
				"\t\t<password>pw2</password>\n"+
				"\t\t<poolsize>15</poolsize>\n"+
				"\t</datasource>\n"+
				"\t<datasource name=\"datasource3\">\n"+
				"\t\t<driver>oracle.jdbc.driver.OracleDriver</driver>\n"+
				"\t\t<url>jdbc:oracle:thin:@10.1.1.2:1521:database</url>\n"+
				"\t\t<user>unittests</user>\n"+
				"\t\t<password>password</password>\n"+
				"\t</datasource>\n"+
				"\t<datasource name=\"datasource4\">\n"+
				"\t\t<driver>org.gjt.mm.mysql.Driver</driver>\n"+
				"\t\t<url>jdbc:mysql://localhost:3306/thedb</url>\n"+
				"\t\t<user>user2</user>\n"+
				"\t\t<password>pw2</password>\n"+
				"\t\t<poolsize>15</poolsize>\n"+
				"\t</datasource>\n"+
				"</datasources>\n");
		}
		catch (DatasourcesException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testStoreXml()
	{
		try
		{
			Datasources datasources = new Datasources();
			
			Datasource datasource1 = new Datasource("driver1", "url1", "user1", "password1", 7);
			Datasource datasource2 = new Datasource();
			datasource2.setDriver("driver2");
			datasource2.setUrl("url2");
			datasource2.setUser("user2");
			datasource2.setPassword("password2");
			Datasource datasource3 = new Datasource();
			datasource3.setDriver("driver3");
			datasource3.setUrl("url3");
			datasource3.setPoolsize(3);
			
			datasources.setDatasource("datasource1", datasource1);
			datasources.setDatasource("datasource2", datasource2);
			datasources.setDatasource("datasource3", datasource3);
			
			String	xml_filename = "datasources_storexml_test.xml";
			String	xml_path = RifeConfig.Global.getTempPath()+File.separator+xml_filename;
			File	xml_file = new File(xml_path);
			datasources.storeToXml(xml_file);
			
			Datasources datasources_stored = new Datasources(xml_filename, ResourceFinderClasspath.getInstance());
			assertEquals(datasource1, datasources_stored.getDatasource("datasource1"));
			assertEquals(datasource2, datasources_stored.getDatasource("datasource2"));
			assertEquals(datasource3, datasources_stored.getDatasource("datasource3"));
			Datasource datasource4 = new Datasource("driver4", "url4", "user4", "password4", 11);
			datasources_stored.setDatasource("datasource4", datasource4);
			datasources_stored.storeToXml();
			
			Datasources datasources_stored2 = new Datasources(xml_filename, ResourceFinderClasspath.getInstance());
			assertEquals(datasource1, datasources_stored2.getDatasource("datasource1"));
			assertEquals(datasource2, datasources_stored2.getDatasource("datasource2"));
			assertEquals(datasource3, datasources_stored2.getDatasource("datasource3"));
			assertEquals(datasource4, datasources_stored2.getDatasource("datasource4"));
			
			xml_file.delete();
		}
		catch (DatasourcesException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
}
