/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseContentInfo.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam;

import java.sql.Timestamp;

import junit.framework.TestCase;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentInfo;

public class TestDatabaseContentInfo extends TestCase
{
	public TestDatabaseContentInfo(String name)
	{
		super(name);
	}
	
	public void testInstantiation()
	{
		DatabaseContentInfo contentinfo = new DatabaseContentInfo();
		assertNotNull(contentinfo);

		assertEquals(-1, contentinfo.getContentId());
		assertNull(contentinfo.getPath());
		assertEquals(-1, contentinfo.getVersion());
		assertNull(contentinfo.getCreated());
		assertNull(contentinfo.getMimeType());
		assertFalse(contentinfo.isFragment());
		assertFalse(contentinfo.hasName());
		assertNull(contentinfo.getName());
		assertNull(contentinfo.getAttributes());
		assertFalse(contentinfo.hasAttributes());
		assertFalse(contentinfo.hasAttribute("attr1"));
		assertNull(contentinfo.getAttribute("attr1"));
		assertEquals(-1, contentinfo.getSize());
		assertFalse(contentinfo.hasProperties());
		assertNull(contentinfo.getProperties());
		assertFalse(contentinfo.hasProperty("some prop"));
		assertNull(contentinfo.getProperty("some prop"));
	}

	public void testContentId()
	{
		DatabaseContentInfo contentinfo = new DatabaseContentInfo();
		contentinfo.setContentId(12);
		assertEquals(12, contentinfo.getContentId());
	}


	public void testValidation()
	{
		DatabaseContentInfo contentinfo = new DatabaseContentInfo();

		contentinfo.resetValidation();
		assertFalse(contentinfo.validate());
		assertFalse(contentinfo.isSubjectValid("contentId"));
		assertFalse(contentinfo.isSubjectValid("path"));
		assertFalse(contentinfo.isSubjectValid("mimeType"));
		assertFalse(contentinfo.isSubjectValid("version"));
		assertFalse(contentinfo.isSubjectValid("created"));

		contentinfo.resetValidation();
		contentinfo.setPath("/some/other/path");
		contentinfo.setMimeType(MimeType.APPLICATION_XHTML.toString());
		contentinfo.setVersion(5);
		contentinfo.setCreated(new Timestamp(System.currentTimeMillis()));

		contentinfo.resetValidation();
		contentinfo.setContentId(87);
		assertTrue(contentinfo.validate());
		assertTrue(contentinfo.isSubjectValid("contentId"));

		contentinfo.resetValidation();
		assertTrue(contentinfo.validate());
		assertTrue(contentinfo.isSubjectValid("contentId"));
		assertTrue(contentinfo.isSubjectValid("path"));
		assertTrue(contentinfo.isSubjectValid("mimeType"));
		assertTrue(contentinfo.isSubjectValid("version"));
		assertTrue(contentinfo.isSubjectValid("created"));
	}
}


