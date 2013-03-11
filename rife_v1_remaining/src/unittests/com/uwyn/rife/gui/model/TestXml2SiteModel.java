/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestXml2SiteModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.resources.ResourceFinderClasspath;
import junit.framework.TestCase;

public class TestXml2SiteModel extends TestCase
{
	public TestXml2SiteModel(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		Xml2SiteModel xml2sitemodel = new Xml2SiteModel();
		
		assertNotNull(xml2sitemodel);
	}

	public void testParser()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		Xml2SiteModel			xml2sitemodel = new Xml2SiteModel();
		
		xml2sitemodel.processXml("xml/test_xml2sitemodel.xml", resourcefinder);
	}
}

