/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineOutputs.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.*;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.engine.exceptions.OutputUnknownException;

public class TestEngineOutputs extends TestCaseServerside
{
	public TestEngineOutputs(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testOutputsValid()
	throws Exception
	{
		setupSite("site/outputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outputs/valid");
		WebResponse response = conversation.getResponse(request);
		assertEquals("the response", response.getText());
	}

	public void testOutputsNormalOutjection()
	throws Exception
	{
		setupSite("site/outputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outputs/normal/outjection");
		try
		{
			WebResponse response = conversation.getResponse(request);
			assertEquals("11124\n"+
						 "value2\n"+
						 "value3a\n"+
						 "870\n"+
						 "programmatic value5\n"+
						 "default value6\n", response.getText());
		}
		catch (Exception e)
		{getLogSink().getInternalException().printStackTrace();}
	}

	public void testOutputsBeanNormal()
	throws Exception
	{
		setupSite("site/outputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outputs/bean/normal");
		WebResponse response = null;

		response = conversation.getResponse(request);
		// adapt to serialization in different JDK versions
		assertTrue(("the string,the stringbuffer,23154,893749,u,false,true,false,false,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12,H4sIAAAAAAAAAFvzloG1uIhBOCuxLFGvtCQzR8+xqCix0iezuKSi8ZLszOOJc5kZGD0ZWIozq1IrChgYGFjLWYAkVwkDc35eKlCrIFhrTmJeup5nXklqemqR0KMFS743tlswgTSyliXmlKZWFDEIINT5leYmpRa1rZkqyz3lQTcTAwPYYKYSBtaSjKJUkKH8CMVuOfmJJbfenlx02+bDG6CRbjAjCxnqGFicDA/1A9WLIdQHlxRl5qU7laalpRbps7PffHXi1WUmBmagS5LzS/NKohjYijMSi1JToqHmlDAwRTuDHcDCUFoE4mxQS9vwKLZlDcxhIgxpDJkMZQypDARABQA/lGfcTgEAAA=="+
					"the string;the stringbuffer;23154;893749;u;null;true;false;0;21;34878.34;25435.98;3434.76;6534.8;34347897;2335454;32;12;[one, 2, three, 44.44, five]").equals(response.getText()) ||
				   ("the string,the stringbuffer,23154,893749,u,false,true,false,false,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12,H4sIAAAAAAAAAFvzloG1uIhBOCuxLFGvtCQzR8+xqCix0iezuKSi8ZLszOOJc5kZGD0ZWIozq1IrChgYGFjLWYAkVwkDc35eKlCrIFhrTmJeup5nXklqemqR0KMFS743tlswgTSyliXmlKZWFDEIINT5leYmpRa1rZkqyz3lQTcTAwPYYKYSBtaSjKJUkKH8CMVuOfmJJbfenlx02+bDG6CRbjAjCxnqGFicDA/1A9WLIdQHlxRl5qU7laalpRbps7PffHXi1WVmBmagS5LzS/NKohjYijMSi1JToqHmlDAwRTuDHcDCUFoE4mxQS9vwKLZlDcxhIgxpDJkMZQypDARARQUAL3zR5E8BAAA="+
					"the string;the stringbuffer;23154;893749;u;null;true;false;0;21;34878.34;25435.98;3434.76;6534.8;34347897;2335454;32;12;[one, 2, three, 44.44, five]").equals(response.getText()));
	}
	
	public void testOutputsBeanNormalOutjection()
	throws Exception
	{
		setupSite("site/outputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outputs/bean/normal/outjection");
		WebResponse response = null;
		
		response = conversation.getResponse(request);
		// adapt to serialization in different JDK versions
		assertTrue(("the string,the stringbuffer,23154,893749,u,false,true,false,false,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12,H4sIAAAAAAAAAFvzloG1uIhBOCuxLFGvtCQzR8+xqCix0iezuKSi8ZLszOOJc5kZGD0ZWIozq1IrChgYGFjLWYAkVwkDc35eKlCrIFhrTmJeup5nXklqemqR0KMFS743tlswgTSyliXmlKZWFDEIINT5leYmpRa1rZkqyz3lQTcTAwPYYKYSBtaSjKJUkKH8CMVuOfmJJbfenlx02+bDG6CRbjAjCxnqGFicDA/1A9WLIdQHlxRl5qU7laalpRbps7PffHXi1WUmBmagS5LzS/NKohjYijMSi1JToqHmlDAwRTuDHcDCUFoE4mxQS9vwKLZlDcxhIgxpDJkMZQypDARABQA/lGfcTgEAAA=="+
				   "the string;the stringbuffer;23154;893749;u;null;true;false;0;21;34878.34;25435.98;3434.76;6534.8;34347897;2335454;32;12;[one, 2, three, 44.44, five]").equals(response.getText()) ||
				   ("the string,the stringbuffer,23154,893749,u,false,true,false,false,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12,H4sIAAAAAAAAAFvzloG1uIhBOCuxLFGvtCQzR8+xqCix0iezuKSi8ZLszOOJc5kZGD0ZWIozq1IrChgYGFjLWYAkVwkDc35eKlCrIFhrTmJeup5nXklqemqR0KMFS743tlswgTSyliXmlKZWFDEIINT5leYmpRa1rZkqyz3lQTcTAwPYYKYSBtaSjKJUkKH8CMVuOfmJJbfenlx02+bDG6CRbjAjCxnqGFicDA/1A9WLIdQHlxRl5qU7laalpRbps7PffHXi1WVmBmagS5LzS/NKohjYijMSi1JToqHmlDAwRTuDHcDCUFoE4mxQS9vwKLZlDcxhIgxpDJkMZQypDARARQUAL3zR5E8BAAA="+
				   "the string;the stringbuffer;23154;893749;u;null;true;false;0;21;34878.34;25435.98;3434.76;6534.8;34347897;2335454;32;12;[one, 2, three, 44.44, five]").equals(response.getText()));
	}
	
	public void testOutputsBeanPrefix()
	throws Exception
	{
		setupSite("site/outputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outputs/bean/prefix");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals("the string,the stringbuffer,23154,893749,u,false,true,false,false,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());
	}
	
	public void testOutputsBeanPrefixOutjection()
	throws Exception
	{
		setupSite("site/outputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outputs/bean/prefix/outjection");
		WebResponse response = null;
		
		response = conversation.getResponse(request);
		assertEquals("the string,the stringbuffer,23154,893749,u,false,true,false,false,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());
	}
	
	public void testOutputsBeanNormalClear()
	throws Exception
	{
		setupSite("site/outputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outputs/bean/normal/clear");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals("ok,null,null,null,null,false,null,null,false,null,null,null,null,null,null,null,null,null", response.getText());
	}

	public void testOutputsBeanNormalNamedClear()
	throws Exception
	{
		setupSite("site/outputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outputs/bean/normal/named/clear");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals("ok,null,null,null,null,false,null,null,false,null,null,null,null,null,null,null,null,null", response.getText());
	}

	public void testOutputsBeanPrefixClear()
	throws Exception
	{
		setupSite("site/outputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outputs/bean/prefix/clear");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals("ok,null,null,null,null,false,null,null,false,null,null,null,null,null,null,null,null,null", response.getText());
	}

	public void testOutputsBeanPrefixNamedClear()
	throws Exception
	{
		setupSite("site/outputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outputs/bean/prefix/named/clear");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals("ok,null,null,null,null,false,null,null,false,null,null,null,null,null,null,null,null,null", response.getText());
	}

	public void testOutputsTyped()
	throws Exception
	{
		setupSite("site/outputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outputs/typed");
		WebResponse response = conversation.getResponse(request);
		assertEquals("astring"+","+"astring2"+","+"astring3"+","+"astring4"+
			"U"+","+"V"+
			"bko"+","+"kkl"+
			Integer.MAX_VALUE+","+78327+
			Long.MAX_VALUE+","+83764987398L+
			34798.43+","+893749.56+
			43.18f+","+87.34f, response.getText());
	}

	public void testOutputsGenerated()
	throws Exception
	{
		setupSite("site/outputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outputs/generated");
		WebResponse response = conversation.getResponse(request);
		assertEquals("Welcome Geert Bevin\n", response.getText());
	}

	public void testOutputsInvalid()
	throws Exception
	{
		setupSite("site/outputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outputs/invalid");
		try
		{
			WebResponse response = conversation.getResponse(request);
			fail();
			assertNotNull(response);
		}
		catch (HttpInternalErrorException e)
		{
			assertTrue(getLogSink().getInternalException() instanceof OutputUnknownException);

			OutputUnknownException	e2 = (OutputUnknownException)getLogSink().getInternalException();
			assertEquals("output1", e2.getOutputName());
			assertEquals(e2.getDeclarationName(), "element/outputs/invalid.xml");
		}
	}

	public void testOutputsDefaults()
	throws Exception
	{
		setupSite("site/outputs.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outputs/defaults");
		WebResponse response = conversation.getResponse(request);
		assertEquals("the first value"+
			"2rdb-2rda-2rdc-2rdd"+
			"the element config value", response.getText());
	}
}

