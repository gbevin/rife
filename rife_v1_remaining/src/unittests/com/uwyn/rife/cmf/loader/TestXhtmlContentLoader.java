/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestXhtmlContentLoader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.loader;

import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;

public class TestXhtmlContentLoader extends TestCase
{
	public TestXhtmlContentLoader(String name)
	{
		super(name);
	}

	public void testLoad()
	throws Exception
	{
	    XhtmlContentLoader loader = new XhtmlContentLoader();
		Set<String> errors = new HashSet<String>();

		String xhtml = loader.load("<p>some <b>html</b> here</p>", true, errors);

		assertNotNull(xhtml);
		assertEquals(0, errors.size());
	}

	public void testLoadNull()
	throws Exception
	{
	    XhtmlContentLoader loader = new XhtmlContentLoader();
		Set<String> errors = new HashSet<String>();

		String xhtml = loader.load(null, false, errors);

		assertNull(xhtml);
		assertEquals(0, errors.size());
	}

	public void getBackends()
	throws Exception
	{
	    XhtmlContentLoader loader = new XhtmlContentLoader();
		assertTrue(loader.getBackends().size() > 0);
	}
}
