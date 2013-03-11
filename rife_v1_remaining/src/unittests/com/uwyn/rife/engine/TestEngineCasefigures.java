/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineCasefigures.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.TestCaseServerside;

public class TestEngineCasefigures extends TestCaseServerside
{
	public TestEngineCasefigures(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testDatatransfer()
	throws Exception
	{
		setupSite("site/casefigures.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/datatransfer/source");
		request.setParameter("switch", "1");
		response = conversation.getResponse(request);
		assertEquals("value4a|value4b,value3", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/datatransfer/source");
		request.setParameter("switch", "2");
		response = conversation.getResponse(request);
		assertEquals("value2a|value2b|value2c,value1", response.getText());
	}

	public void testUrltransfer()
	throws Exception
	{
		setupSite("site/casefigures.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/urltransfer/source");
		response = conversation.getResponse(request);
		link = response.getLinkWith("sourceparent");
		response = link.click();
		link = response.getLinkWith("source");
		response = link.click();
		link = response.getLinkWith("destinationparent");
		response = link.click();
		link = response.getLinkWith("destination");
		response = link.click();
		assertEquals("arrived", response.getText());
	}
}

