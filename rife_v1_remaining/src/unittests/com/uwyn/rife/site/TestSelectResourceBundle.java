/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSelectResourceBundle.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import java.util.Enumeration;
import java.util.HashMap;
import junit.framework.TestCase;

public class TestSelectResourceBundle extends TestCase
{
	public TestSelectResourceBundle(String name)
	{
		super(name);
	}

	public void testIllegalArguments()
	{
		try
		{
			new SelectResourceBundle(null, new HashMap<String, String>());
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertNotNull(e);
		}

		try
		{
			new SelectResourceBundle("property", null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertNotNull(e);
		}
	}
	
	public void testInstantiation()
	{
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		
		SelectResourceBundle resource_bundle = new SelectResourceBundle("myProperty", map);
		Enumeration<String> keys_enum = resource_bundle.getKeys();
		int count = 0;
		while (keys_enum.hasMoreElements())
		{
			count++;
			
			String key = keys_enum.nextElement();
			assertTrue(key.equals("myProperty:key1") ||
				key.equals("myProperty:key2") ||
				key.equals("myProperty:key3"));
		}
		assertEquals(3, count);
		
		assertEquals(resource_bundle.getObject("myProperty:key1"), "value1");
		assertEquals(resource_bundle.getObject("myProperty:key2"), "value2");
		assertEquals(resource_bundle.getObject("myProperty:key3"), "value3");
	}
}
