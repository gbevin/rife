/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCmfProperty.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf;

import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.transform.ImageContentTransformer;
import junit.framework.TestCase;

import java.awt.*;
import java.util.Map;

public class TestCmfProperty extends TestCase
{
	public TestCmfProperty(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		CmfProperty property = new CmfProperty("property");
		assertFalse(property.isListed());
		assertEquals(-1, property.getPosition());
		assertFalse(property.hasPosition());
		assertNull(property.getMimeType());
		assertFalse(property.hasMimeType());
		assertFalse(property.isAutoRetrieved());
		assertFalse(property.isFragment());
		assertFalse(property.hasName());
		assertNull(property.getName());
		assertFalse(property.isOrdinal());
		assertNull(property.getOrdinalRestriction());
		assertFalse(property.hasOrdinalRestriction());
		assertNull(property.getContentAttributes());
		assertNull(property.getTransformer());
		assertFalse(property.hasTransformer());
		assertNull(property.getCachedLoadedData());
	}

	public void testListed()
	{
		CmfProperty property = new CmfProperty("property");
		assertSame(property, property.listed(true));
		assertTrue(property.isListed());
		property.setListed(false);
		assertFalse(property.isListed());
	}

	public void testPosition()
	{
		CmfProperty property = new CmfProperty("property");
		assertSame(property, property.position(9));
		assertEquals(9, property.getPosition());
		assertTrue(property.hasPosition());
		property.setPosition(-1);
		assertEquals(-1, property.getPosition());
		assertFalse(property.hasPosition());
	}

	public void testMimeType()
	{
		CmfProperty property = new CmfProperty("property");
		property.setPersistent(true);
		property.setDisplayedRaw(false);
		assertSame(property, property.mimeType(MimeType.IMAGE_GIF));
		assertEquals(MimeType.IMAGE_GIF, property.getMimeType());
		assertTrue(property.hasMimeType());
		assertFalse(property.isPersistent());
		assertTrue(property.isDisplayedRaw());
		property.setMimeType(null);
		assertNull(property.getMimeType());
		assertFalse(property.hasMimeType());
	}

	public void testAutoRetrieved()
	{
		CmfProperty property = new CmfProperty("property");
		assertSame(property, property.autoRetrieved(true));
		assertTrue(property.isAutoRetrieved());
		property.setAutoRetrieved(false);
		assertFalse(property.isAutoRetrieved());
	}

	public void testName()
	{
		CmfProperty property = new CmfProperty("property");
		property.setName("myname.gif");
		assertTrue(property.hasName());
		assertEquals("myname.gif", property.getName());
		assertSame(property, property.name("anothername.png"));
		assertTrue(property.hasName());
		assertEquals("anothername.png", property.getName());
		property.name(null);
		assertFalse(property.hasName());
		assertNull(property.getName());
	}

	public void testPersistent()
	{
		CmfProperty property = new CmfProperty("property");
		property.setPersistent(true);
		assertTrue(property.isPersistent());
		property.setPersistent(false);
		assertFalse(property.isPersistent());
		property.mimeType(MimeType.IMAGE_GIF);
		try
		{
			property.setPersistent(true);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertFalse(property.isPersistent());
		}
		property.setPersistent(false);
		assertFalse(property.isPersistent());
	}

	public void testDisplayedRaw()
	{
		CmfProperty property = new CmfProperty("property");
		property.setDisplayedRaw(true);
		assertTrue(property.isDisplayedRaw());
		property.setDisplayedRaw(false);
		assertFalse(property.isDisplayedRaw());
		property.mimeType(MimeType.IMAGE_GIF);
		try
		{
			property.setDisplayedRaw(false);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(property.isDisplayedRaw());
		}
		property.setDisplayedRaw(true);
		assertTrue(property.isDisplayedRaw());
	}

	public void testFragment()
	{
		CmfProperty property = new CmfProperty("property");
		assertSame(property, property.fragment(true));
		assertTrue(property.isFragment());
		property.setFragment(false);
		assertFalse(property.isFragment());
	}

	public void testOrdinal()
	{
		CmfProperty property = new CmfProperty("property");
		assertSame(property, property.ordinal(true));
		assertTrue(property.isOrdinal());
		property.setOrdinal(false);
		assertFalse(property.isOrdinal());
	}

	public void testOrdinalRestriction()
	{
		CmfProperty property = new CmfProperty("property");
		assertSame(property, property.ordinal(true, "restriction"));
		assertTrue(property.isOrdinal());
		assertTrue(property.hasOrdinalRestriction());
		assertEquals("restriction", property.getOrdinalRestriction());
		property.setOrdinal(false);
		assertFalse(property.isOrdinal());
		assertFalse(property.hasOrdinalRestriction());
		assertNull(property.getOrdinalRestriction());
		assertSame(property, property.ordinal(true, "restriction"));
		assertSame(property, property.ordinal(false, "restriction2"));
		assertFalse(property.hasOrdinalRestriction());
		assertNull(property.getOrdinalRestriction());
	}

	public void testContentAttribute()
	{
		CmfProperty property = new CmfProperty("property");
		assertSame(property, property.contentAttribute("attribute", true));
		assertEquals("true", property.getContentAttributes().get("attribute"));
		assertSame(property, property.contentAttribute("attribute", 'K'));
		assertEquals("K", property.getContentAttributes().get("attribute"));
		assertSame(property, property.contentAttribute("attribute", (byte)24));
		assertEquals("24", property.getContentAttributes().get("attribute"));
		assertSame(property, property.contentAttribute("attribute", (short)213));
		assertEquals("213", property.getContentAttributes().get("attribute"));
		assertSame(property, property.contentAttribute("attribute", 1243876));
		assertEquals("1243876", property.getContentAttributes().get("attribute"));
		assertSame(property, property.contentAttribute("attribute", 8692322879L));
		assertEquals("8692322879", property.getContentAttributes().get("attribute"));
		assertSame(property, property.contentAttribute("attribute", 6872.23f));
		assertEquals("6872.23", property.getContentAttributes().get("attribute"));
		assertSame(property, property.contentAttribute("attribute", 9249273.13d));
		assertEquals("9249273.13", property.getContentAttributes().get("attribute"));
		assertSame(property, property.contentAttribute("attribute", "somestring"));
		assertEquals("somestring", property.getContentAttributes().get("attribute"));
	}

	public void testTransformer()
	{
		CmfProperty property = new CmfProperty("property");
		ImageContentTransformer transformer = new ImageContentTransformer() {
				public Image transform(Image data, Map<String, String> attributes)
				throws ContentManagerException
				{
					return data;
				}
			};
		assertSame(property, property.transformer(transformer));
		assertEquals(transformer, property.getTransformer());
		assertTrue(property.hasTransformer());
		property.setTransformer(null);
		assertNull(property.getTransformer());
		assertFalse(property.hasTransformer());
	}

	public void testCachedLoadedData()
	{
		CmfProperty property = new CmfProperty("property");
		Object cache = new Object();
		property.setCachedLoadedData(cache);
		assertSame(cache, property.getCachedLoadedData());
		property.setCachedLoadedData(null);
		assertNull(property.getCachedLoadedData());
	}
}
