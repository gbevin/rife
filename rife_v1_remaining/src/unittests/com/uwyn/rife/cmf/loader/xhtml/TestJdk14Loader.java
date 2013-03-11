/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestJdk14Loader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.loader.xhtml;

import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;

public class TestJdk14Loader extends TestCase
{
	public TestJdk14Loader(String name)
	{
		super(name);
	}

	public void testIsBackendPresent()
	{
	    Jdk14Loader loader = new Jdk14Loader();
		assertTrue(loader.isBackendPresent());
	}

	public void testLoadSuccess()
	throws Exception
	{
	    Jdk14Loader loader = new Jdk14Loader();
		Set<String> errors = new HashSet<String>(); 

		String xhtml = loader.load("<p>some <b>html</b> here</p>", true, errors);

		assertNotNull(xhtml);
		assertEquals(0, errors.size());
	}

	public void testLoadUnsupportedType()
	throws Exception
	{
	    Jdk14Loader loader = new Jdk14Loader();
		Set<String> errors = new HashSet<String>(); 

		String xhtml = loader.load(new Object(), true, errors);

		assertNull(xhtml);
		assertEquals(0, errors.size());
	}

	public void testLoadFromStringSuccessFragment()
	throws Exception
	{
	    Jdk14Loader loader = new Jdk14Loader();
		Set<String> errors = new HashSet<String>(); 

		String xhtml = loader.loadFromString("<p>some <b>html</b> here</p>", true, errors);

		assertNotNull(xhtml);
		assertEquals(0, errors.size());
	}

	public void testLoadFromStringSuccessComplete()
	throws Exception
	{
	    Jdk14Loader loader = new Jdk14Loader();
		Set<String> errors = new HashSet<String>(); 

		String xhtml = loader.loadFromString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
											 "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
											 "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title></title></head><body>\n" +
											 "<p>body</p>\n" +
											 "</body></html>", false, errors);

		assertNotNull(xhtml);
		assertEquals(0, errors.size());
	}

	public void testLoadFromStringError()
	throws Exception
	{
	    Jdk14Loader loader = new Jdk14Loader();
		Set<String> errors = new HashSet<String>(); 

		String xhtml = loader.loadFromString("<i><b>error</i>", true, errors);

		assertNull(xhtml);
		assertEquals(1, errors.size());
	}

	public void testLoadFromStringErrorNoList()
	throws Exception
	{
	    Jdk14Loader loader = new Jdk14Loader();

		String xhtml = loader.loadFromString("<i><test>error</test></i>", true, null);

		assertNull(xhtml);
	}
}
