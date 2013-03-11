/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestXml2Config.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.config;

import com.uwyn.rife.config.exceptions.ConfigErrorException;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.ExceptionUtils;
import java.util.Collection;
import java.util.Iterator;
import junit.framework.TestCase;

public class TestXml2Config extends TestCase
{
	public TestXml2Config(String name)
	{
		super(name);
	}
	
	public void testInstantiation()
	{
		Xml2Config xml2config = new Xml2Config();
		
		assertNotNull(xml2config);
	}
	
	public void testParse()
	{
		Config config = null;
		
		try
		{
			config = new Config("xml/test_xml2config.xml", ResourceFinderClasspath.getInstance());
		}
		catch (ConfigErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertTrue(config.hasParameter("paramstring"));
		assertTrue(config.hasParameter("parambool"));
		assertTrue(config.hasParameter("paramchar"));
		assertTrue(config.hasParameter("paramint"));
		assertTrue(config.hasParameter("paramlong"));
		assertTrue(config.hasParameter("paramfloat"));
		assertTrue(config.hasParameter("paramdouble"));
		assertTrue(config.hasParameter("paramproperty"));
		assertTrue(config.hasParameter("paramfinal"));
		assertEquals(config.countParameters(), 9);
		
		assertEquals(config.getString("paramstring"), "astring");
		assertEquals(config.getBool("parambool"), true);
		assertEquals(config.getChar("paramchar"), 'C');
		assertEquals(config.getInt("paramint"), 5133);
		assertEquals(config.getLong("paramlong"), 8736478L);
		assertEquals(config.getFloat("paramfloat"), 545.2546f, 0);
		assertEquals(config.getDouble("paramdouble"), 7863.3434353d, 0);
		assertEquals(config.getString("paramproperty"), "begin:property_test_value:end");
		assertEquals(config.getString("paramfinal"), "initial value");

		assertFalse(config.isFinalParameter("paramstring"));
		assertFalse(config.isFinalParameter("parambool"));
		assertFalse(config.isFinalParameter("paramchar"));
		assertFalse(config.isFinalParameter("paramint"));
		assertFalse(config.isFinalParameter("paramlong"));
		assertFalse(config.isFinalParameter("paramfloat"));
		assertFalse(config.isFinalParameter("paramdouble"));
		assertFalse(config.isFinalParameter("paramproperty"));
		assertTrue(config.isFinalParameter("paramfinal"));

		assertTrue(config.hasList("list1"));
		assertTrue(config.hasList("list2"));
		assertTrue(config.hasList("listfinal"));
		assertEquals(config.countLists(), 3);

		assertFalse(config.isFinalList("list1"));
		assertFalse(config.isFinalList("list2"));
		assertTrue(config.isFinalList("listfinal"));

		Collection<String>	items = null;
		Iterator<String>	item_it = null;
		
		items = config.getStringItems("list1");
		assertEquals(items.size(), 3);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item1");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item2");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item3");
		assertEquals(item_it.hasNext(), false);

		items = config.getStringItems("list2");
		assertEquals(items.size(), 3);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item4");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item5");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "start:property_test_value:finish");
		assertEquals(item_it.hasNext(), false);
		
		items = config.getStringItems("listfinal");
		assertEquals(items.size(), 2);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item6");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item7");
		assertEquals(item_it.hasNext(), false);
	}
	
	public void testIncluding()
	{
		Config config = null;
		
		try
		{
			config = new Config("xml/test_xml2config_including.xml", ResourceFinderClasspath.getInstance());
		}
		catch (ConfigErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertTrue(config.hasParameter("includingstring"));
		assertTrue(config.hasParameter("paramstring"));
		assertTrue(config.hasParameter("parambool"));
		assertTrue(config.hasParameter("paramchar"));
		assertTrue(config.hasParameter("paramint"));
		assertTrue(config.hasParameter("paramlong"));
		assertTrue(config.hasParameter("paramfloat"));
		assertTrue(config.hasParameter("paramdouble"));
		assertTrue(config.hasParameter("paramincluding"));
		assertTrue(config.hasParameter("paramproperty"));
		assertTrue(config.hasParameter("paramfinal"));
		assertEquals(config.countParameters(), 11);
		
		assertEquals(config.getString("includingstring"), "the including value goes to the included file : ");
		assertEquals(config.getString("paramstring"), "the including value goes to the included file : astring");
		assertEquals(config.getBool("parambool"), true);
		assertEquals(config.getChar("paramchar"), 'C');
		assertEquals(config.getInt("paramint"), 9999);
		assertEquals(config.getLong("paramlong"), 8736478L);
		assertEquals(config.getFloat("paramfloat"), 545.2546f, 0);
		assertEquals(config.getDouble("paramdouble"), 7863.3434353d, 0);
		assertEquals(config.getString("paramincluding"), "it's including : C");
		assertEquals(config.getString("paramproperty"), "begin:property_test_value:end");
		assertEquals(config.getString("paramfinal"), "initial value");

		assertFalse(config.isFinalParameter("paramstring"));
		assertFalse(config.isFinalParameter("parambool"));
		assertFalse(config.isFinalParameter("paramchar"));
		assertTrue(config.isFinalParameter("paramint"));
		assertFalse(config.isFinalParameter("paramlong"));
		assertFalse(config.isFinalParameter("paramfloat"));
		assertFalse(config.isFinalParameter("paramdouble"));
		assertFalse(config.isFinalParameter("paramproperty"));
		assertTrue(config.isFinalParameter("paramfinal"));

		assertTrue(config.hasList("list1"));
		assertTrue(config.hasList("list2"));
		assertTrue(config.hasList("listfinal"));
		assertEquals(config.countLists(), 3);

		Collection<String>	items = null;
		Iterator<String>	item_it = null;
		
		items = config.getStringItems("list1");
		assertEquals(items.size(), 1);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item10");
		assertEquals(item_it.hasNext(), false);

		items = config.getStringItems("list2");
		assertEquals(items.size(), 4);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item13");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item14");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item15");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item16");
		assertEquals(item_it.hasNext(), false);
		
		items = config.getStringItems("listfinal");
		assertEquals(items.size(), 2);
		item_it = items.iterator();
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item6");
		assertEquals(item_it.hasNext(), true);
		assertEquals(item_it.next(), "item7");
		assertEquals(item_it.hasNext(), false);
	}
	
	public void testSelectedShortClassname()
	{
		Config config = null;
		
		try
		{
			config = new Config("TestSelectorConfig", ResourceFinderClasspath.getInstance());
		}
		catch (ConfigErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertTrue(config.hasParameter("selectedparamstring"));
	}
	
	public void testSelectedFullClassname()
	{
		Config config = null;
		
		try
		{
			config = new Config("com.uwyn.rife.selector.TestSelectorConfig", ResourceFinderClasspath.getInstance());
		}
		catch (ConfigErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertTrue(config.hasParameter("selectedparamstring"));
	}
	
	public void testUnavailableXmlFile()
	{
		Config config = null;
		
		try
		{
			config = new Config("xml/this_file_is_not_there.xml", ResourceFinderClasspath.getInstance());
			fail();
			assertNotNull(config);
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		catch (ConfigErrorException e)
		{
			assertTrue(true);
		}
	}
}
