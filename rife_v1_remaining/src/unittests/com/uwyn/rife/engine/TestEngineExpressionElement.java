/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineExpressionElement.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.TestCaseServerside;

public class TestEngineExpressionElement extends TestCaseServerside
{
	public TestEngineExpressionElement(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testInputsOgnl()
	throws Exception
	{
		setupSite("site/expressionelement.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/inputs_ognl?input1=value1");
		response = conversation.getResponse(request);
		assertEquals("this is value 1\n\n\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inputs_ognl?input1=value2");
		response = conversation.getResponse(request);
		assertEquals("this is value 2\n\n\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inputs_ognl?input1=value3");
		response = conversation.getResponse(request);
		assertEquals("<!--V 'OGNL:value'/-->\n\n\n", response.getText());
	}

	public void testInputsMvel()
	throws Exception
	{
		setupSite("site/expressionelement.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/inputs_mvel?input1=value1");
		try
		{
			response = conversation.getResponse(request);
		}
		catch (Throwable e)
		{getLogSink().getInternalException().printStackTrace();}
		assertEquals("this is value 1\n\n\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inputs_mvel?input1=value2");
		response = conversation.getResponse(request);
		assertEquals("this is value 2\n\n\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inputs_mvel?input1=value3");
		response = conversation.getResponse(request);
		assertEquals("<!--V 'MVEL:value'/-->\n\n\n", response.getText());
	}
	
	public void testInputsGroovy()
	throws Exception
	{
		setupSite("site/expressionelement.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inputs_groovy?input1=value1");
		response = conversation.getResponse(request);
		assertEquals("this is value 1\n\n\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inputs_groovy?input1=value2");
		response = conversation.getResponse(request);
		assertEquals("this is value 2\n\n\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inputs_groovy?input1=value3");
		response = conversation.getResponse(request);
		assertEquals("<!--V 'GROOVY:value'/-->\n\n\n", response.getText());
	}
	
	public void testInputsJanino()
	throws Exception
	{
		setupSite("site/expressionelement.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inputs_janino?input1=value1");
		try
		{
			response = conversation.getResponse(request);
		}
		catch (Throwable e) {getLogSink().getInternalException().printStackTrace();}
		assertEquals("this is value 1\n\n\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inputs_janino?input1=value2");
		response = conversation.getResponse(request);
		assertEquals("this is value 2\n\n\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inputs_janino?input1=value3");
		response = conversation.getResponse(request);
		assertEquals("<!--V 'JANINO:value'/-->\n\n\n", response.getText());
	}
}
