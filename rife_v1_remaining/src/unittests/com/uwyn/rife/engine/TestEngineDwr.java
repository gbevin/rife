/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineDwr.java 3928 2008-04-22 16:25:18Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.TestCaseServerside;

public class TestEngineDwr extends TestCaseServerside
{
	public TestEngineDwr(int siteType, String name)
	{
		super(siteType, name);
	}
	
	public void testDwrHello()
	throws Throwable
	{
		setupSite("site/dwr.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/hello");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertTrue(response.getText().indexOf("src=\"http://localhost:8181/dwr/hello/interface/Hello.js\"") != -1);
		assertTrue(response.getText().indexOf("src=\"http://localhost:8181/dwr/hello/engine.js\"") != -1);
		assertTrue(response.getText().indexOf("src=\"http://localhost:8181/dwr/hello/util.js\"") != -1);

		// assert that the correct remote function has been generated
		request = new GetMethodWebRequest("http://localhost:8181/dwr/hello/interface/Hello.js");
		response = conversation.getResponse(request);
		String interface_text = response.getText();
		assertTrue(interface_text.indexOf("Hello.echo = function(p0, callback)") != -1);
		
		// assert that the remoting works properly
		GetMethodWebRequest remoting_request = new GetMethodWebRequest("http://localhost:8181/dwr/hello/call/plaincall/Hello.echo.dwr");
		remoting_request.setParameter("callCount", "1");
		remoting_request.setParameter("page", "/hello");
		remoting_request.setParameter("httpSessionId", "");
		remoting_request.setParameter("scriptSessionId", "");
		remoting_request.setParameter("c0-scriptName", "Hello");
		remoting_request.setParameter("c0-methodName", "echo");
		remoting_request.setParameter("c0-id", "0");
		remoting_request.setParameter("c0-param0", "string:honorable visitor");
		remoting_request.setParameter("batchId", "1");
		response = conversation.getResponse(remoting_request);
		String remoting_text = response.getText();
		
		assertEquals(remoting_text,
					 "//#DWR-INSERT"+System.getProperty("line.separator")+
					 "//#DWR-REPLY"+System.getProperty("line.separator")+
					 "dwr.engine._remoteHandleCallback('1','0',\"I got: honorable visitor\");"+System.getProperty("line.separator"));
	}
}

