/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestImageContentLoader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.loader;

import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.FileUtils;
import java.awt.Image;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;

public class TestImageContentLoader extends TestCase
{
	public TestImageContentLoader(String name)
	{
		super(name);
	}

	public void testLoad()
	throws Exception
	{
	    ImageContentLoader loader = new ImageContentLoader();
		Set<String> errors = new HashSet<String>();
		URL image_resource = ResourceFinderClasspath.getInstance().getResource("uwyn.png");

		byte[] image_bytes = FileUtils.readBytes(image_resource);
		Image image = loader.load(image_bytes, false, errors);

		assertNotNull(image);
		assertEquals(0, errors.size());
	}

	public void testLoadNull()
	throws Exception
	{
	    ImageContentLoader loader = new ImageContentLoader();
		Set<String> errors = new HashSet<String>();

		Image image = loader.load(null, false, errors);

		assertNull(image);
		assertEquals(0, errors.size());
	}

	public void getBackends()
	throws Exception
	{
	    ImageContentLoader loader = new ImageContentLoader();
		assertTrue(loader.getBackends().size() > 0);
	}
}
