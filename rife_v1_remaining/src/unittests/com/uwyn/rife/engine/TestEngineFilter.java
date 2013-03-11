/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineFilter.java 3933 2008-04-25 20:41:45Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.tools.HttpUtils;
import java.net.InetAddress;

public class TestEngineFilter extends TestCaseServerside
{
	public TestEngineFilter(String name)
	{
		super(SITE_FILTER, name);
	}

	public void testServedThroughTheFilter()
	throws Exception
	{
		setupSite("site/filter.xml");

		HttpUtils.Page page = HttpUtils.retrievePage("http://localhost:8181/simple/html");
		
        // Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();
		
		assertEquals("text/html; charset=utf-8", page.getContentType());
		assertEquals("Just some text 127.0.0.1:"+hostname+":.SIMPLEHTML:", page.getContent());
	}

	public void testDefaultServletFallthrough()
	throws Exception
	{
		setupSite("site/filter.xml");

		HttpUtils.Page page = HttpUtils.retrievePage("http://localhost:8181/simple/served_by_default_servlet.txt");
		assertEquals("This is in it, served by the default file servlet.", page.getContent());
	}

	public void testDefer()
	throws Exception
	{
		setupSite("site/filter.xml");

		HttpUtils.Page page = HttpUtils.retrievePage("http://localhost:8181/defer");
		assertEquals("This 'defer', served by the default file servlet.\n", page.getContent());
	}
}

