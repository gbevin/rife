/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestElements.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.util.Arrays;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.TestCaseServerside;

public class TestElements extends TestCaseServerside
{
	public TestElements(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testPrintTemplate()
	throws Exception
	{
		setupSite("site/elements.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/print_template_staticproperties");
		response = conversation.getResponse(request);
		assertEquals("<html>\n\t<head>\n\t\t<title>PrintTemplate elements test</title>\n\t</head>\n\n\t<body>\n\t\t<h1>This template should be printed by the PrintTemplate element.</h1>\n\t</body>\n</html>\n", response.getText());
	}

	public void testPrintTemplateXhtml()
	throws Exception
	{
		setupSite("site/elements.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/print_template_staticproperties_xhtml");
		response = conversation.getResponse(request);
		assertEquals("<html>\n\t<head>\n\t\t<title>PrintTemplate elements test</title>\n\t</head>\n\n\t<body>\n\t\t<h1>This xhtml template should be printed by the PrintTemplate element.</h1>\n\t</body>\n</html>\n", response.getText());
	}

	public void testPrintTemplateProperties()
	throws Exception
	{
		setupSite("site/elements.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/print_template_properties");
		response = conversation.getResponse(request);
		assertEquals("<html>\n\t<head>\n\t\t<title>PrintTemplate elements test</title>\n\t</head>\n\n\t<body>\n\t\t<h1>This template should be printed by the PrintTemplate element.</h1>\n\t</body>\n</html>\n", response.getText());
	}

	public void testRedirectStaticProperties()
	throws Exception
	{
		setupSite("site/elements.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/redirect_staticproperties");
		response = conversation.getResponse(request);
		assertEquals("<html>\n\t<head>\n\t\t<title>PrintTemplate elements test</title>\n\t</head>\n\n\t<body>\n\t\t<h1>This template should be printed by the PrintTemplate element.</h1>\n\t</body>\n</html>\n", response.getText());
	}

	public void testRedirectProperties()
	throws Exception
	{
		setupSite("site/elements.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/redirect_properties");
		response = conversation.getResponse(request);
		assertEquals("<html>\n\t<head>\n\t\t<title>PrintTemplate elements test</title>\n\t</head>\n\n\t<body>\n\t\t<h1>This template should be printed by the PrintTemplate element.</h1>\n\t</body>\n</html>\n", response.getText());
	}

	public void testRedirectStaticPropertiesType()
	throws Exception
	{
		setupSite("site/elements.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/redirect_staticproperties_type");
		response = conversation.getResponse(request);
		assertEquals("<html>\n\t<head>\n\t\t<title>PrintTemplate elements test</title>\n\t</head>\n\n\t<body>\n\t\t<h1>This template should be printed by the PrintTemplate element.</h1>\n\t</body>\n</html>\n", response.getText());
	}

	public void testRedirectPropertiesType()
	throws Exception
	{
		setupSite("site/elements.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/redirect_properties_type");
		response = conversation.getResponse(request);
		assertEquals("<html>\n\t<head>\n\t\t<title>PrintTemplate elements test</title>\n\t</head>\n\n\t<body>\n\t\t<h1>This xhtml template should be printed by the PrintTemplate element.</h1>\n\t</body>\n</html>\n", response.getText());
	}

	public void testUncached()
	throws Exception
	{
		setupSite("site/elements.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/uncached");
		response = conversation.getResponse(request);
		assertEquals(response.getHeaderFields("Cache-Control").length, 3);
		assertTrue(Arrays.asList(response.getHeaderFields("Cache-Control")).contains("no-cache"));
		assertTrue(Arrays.asList(response.getHeaderFields("Cache-Control")).contains("no-store"));
		assertTrue(Arrays.asList(response.getHeaderFields("Cache-Control")).contains("must-revalidate"));
		assertEquals(response.getHeaderFields("Pragma").length, 1);
		assertEquals(response.getHeaderField("Pragma"), "no-cache");
		assertEquals(response.getHeaderFields("Expires").length, 1);
		assertEquals(response.getHeaderField("Expires"), "1");

		request = new GetMethodWebRequest("http://localhost:8181/cached");
		response = conversation.getResponse(request);
		assertNull(response.getHeaderField("Cache-Control"));
		assertNull(response.getHeaderField("Pragma"));
		assertNull(response.getHeaderField("Expires"));
	}
}

