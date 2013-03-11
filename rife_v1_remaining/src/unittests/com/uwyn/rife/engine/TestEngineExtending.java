/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineExtending.java 3958 2008-05-26 12:04:14Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.*;
import com.uwyn.rife.TestCaseServerside;

public class TestEngineExtending extends TestCaseServerside
{
	public TestEngineExtending(int siteType, String name)
	{
		super(siteType, name);
	}
	
	public void testExtendingSimple()
	throws Exception
	{
		setupSite("site/extending.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		conversation.addCookie("incookie1", "incookie1requestvalue");
		request = new PostMethodWebRequest("http://localhost:8181/extending/simple");
		request.setParameter("input1", "input1requestvalue");
		request.setParameter(ReservedParameters.SUBMISSION, "submission1");
		request.setParameter("param1", "param1requestvalue");
		response = conversation.getResponse(request);
		assertEquals("outcookie1elementvalue", conversation.getCookieValue("outcookie1"));
		assertEquals("outcookiedefault", conversation.getCookieValue("outcookie2"));
		assertEquals("Blueprint"+
			"value1"+
			"value2"+
			"input1requestvalue"+
			"inputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"param1requestvalue"+
			"paramdefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/simple");
		request.setParameter("switch", "exit1");
		response = conversation.getResponse(request);
		assertEquals("Blueprint"+
			"value1"+
			"value2"+
			"output1exit1"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/simple");
		request.setParameter("switch", "exit2");
		response = conversation.getResponse(request);
		assertEquals("Blueprint"+
			"value1"+
			"value2"+
			"output1exit2"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault", response.getText());
	}

	public void testExtendingSimpleInSite()
	throws Throwable
	{
		setupSite("site/extending_in_site.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		conversation.addCookie("incookie1", "incookie1requestvalue");
		request = new PostMethodWebRequest("http://localhost:8181/extending/simple");
		request.setParameter("input1", "input1requestvalue");
		request.setParameter(ReservedParameters.SUBMISSION, "submission1");
		request.setParameter("param1", "param1requestvalue");
		response = conversation.getResponse(request);
		assertEquals("outcookie1elementvalue", conversation.getCookieValue("outcookie1"));
		assertEquals("outcookiedefault", conversation.getCookieValue("outcookie2"));
		assertEquals("Blueprint"+
			"value1"+
			"value2"+
			"input1requestvalue"+
			"inputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"param1requestvalue"+
			"paramdefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/simple");
		request.setParameter("switch", "exit1");
		response = conversation.getResponse(request);
		assertEquals("Blueprint"+
			"value1"+
			"value2"+
			"output1exit1"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/simple");
		request.setParameter("switch", "exit2");
		response = conversation.getResponse(request);
		assertEquals("Blueprint"+
			"value1"+
			"value2"+
			"output1exit2"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault", response.getText());
	}

	public void testExtendingClassOverriding()
	throws Exception
	{
		setupSite("site/extending.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		conversation.addCookie("incookie1", "incookie1requestvalue");
		request = new PostMethodWebRequest("http://localhost:8181/extending/classoverriding");
		request.setParameter("input1", "input1requestvalue");
		request.setParameter(ReservedParameters.SUBMISSION, "submission1");
		request.setParameter("param1", "param1requestvalue");
		response = conversation.getResponse(request);
		assertEquals("outcookie1elementvalue", conversation.getCookieValue("outcookie1"));
		assertEquals("outcookiedefault", conversation.getCookieValue("outcookie2"));
		assertEquals("ClassOverriding"+
			"value1"+
			"value2"+
			"input1requestvalue"+
			"inputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"param1requestvalue"+
			"paramdefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/classoverriding");
		request.setParameter("switch", "exit1");
		response = conversation.getResponse(request);
		assertEquals("ClassOverriding"+
			"value1"+
			"value2"+
			"output1exit1"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/classoverriding");
		request.setParameter("switch", "exit2");
		response = conversation.getResponse(request);
		assertEquals("ClassOverriding"+
			"value1"+
			"value2"+
			"output1exit2"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault", response.getText());
	}

	public void testExtendingClassOverridingInSite()
	throws Exception
	{
		setupSite("site/extending_in_site.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		conversation.addCookie("incookie1", "incookie1requestvalue");
		request = new PostMethodWebRequest("http://localhost:8181/extending/classoverriding");
		request.setParameter("input1", "input1requestvalue");
		request.setParameter(ReservedParameters.SUBMISSION, "submission1");
		request.setParameter("param1", "param1requestvalue");
		response = conversation.getResponse(request);
		assertEquals("outcookie1elementvalue", conversation.getCookieValue("outcookie1"));
		assertEquals("outcookiedefault", conversation.getCookieValue("outcookie2"));
		assertEquals("ClassOverriding"+
			"value1"+
			"value2"+
			"input1requestvalue"+
			"inputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"param1requestvalue"+
			"paramdefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/classoverriding");
		request.setParameter("switch", "exit1");
		response = conversation.getResponse(request);
		assertEquals("ClassOverriding"+
			"value1"+
			"value2"+
			"output1exit1"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/classoverriding");
		request.setParameter("switch", "exit2");
		response = conversation.getResponse(request);
		assertEquals("ClassOverriding"+
			"value1"+
			"value2"+
			"output1exit2"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault", response.getText());
	}

	public void testExtendingStaticPropertiesOverriding()
	throws Exception
	{
		setupSite("site/extending.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		conversation.addCookie("incookie1", "incookie1requestvalue");
		request = new PostMethodWebRequest("http://localhost:8181/extending/staticpropertiesoverriding");
		request.setParameter("input1", "input1requestvalue");
		request.setParameter(ReservedParameters.SUBMISSION, "submission1");
		request.setParameter("param1", "param1requestvalue");
		response = conversation.getResponse(request);
		assertEquals("outcookie1elementvalue", conversation.getCookieValue("outcookie1"));
		assertEquals("outcookiedefault", conversation.getCookieValue("outcookie2"));
		assertEquals("Blueprint"+
			"overriddenvalue1"+
			"overriddenvalue2"+
			"input1requestvalue"+
			"inputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"param1requestvalue"+
			"paramdefault", response.getText());
	}

	public void testExtendingStaticPropertiesOverridingInSite()
	throws Exception
	{
		setupSite("site/extending_in_site.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		conversation.addCookie("incookie1", "incookie1requestvalue");
		request = new PostMethodWebRequest("http://localhost:8181/extending/propertiesoverriding");
		request.setParameter("input1", "input1requestvalue");
		request.setParameter(ReservedParameters.SUBMISSION, "submission1");
		request.setParameter("param1", "param1requestvalue");
		response = conversation.getResponse(request);
		assertEquals("outcookie1elementvalue", conversation.getCookieValue("outcookie1"));
		assertEquals("outcookiedefault", conversation.getCookieValue("outcookie2"));
		assertEquals("Blueprint"+
			"overriddenvalue1"+
			"overriddenvalue2"+
			"input1requestvalue"+
			"inputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"param1requestvalue"+
			"paramdefault", response.getText());
	}

	public void testExtendingAdding()
	throws Exception
	{
		setupSite("site/extending.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		conversation.addCookie("incookie1", "incookie1requestvalue");
		conversation.addCookie("incookie3", "incookie3requestvalue");
		request = new PostMethodWebRequest("http://localhost:8181/extending/adding");
		request.setParameter("input1", "input1requestvalue");
		request.setParameter("input3", "input3requestvalue");
		request.setParameter(ReservedParameters.SUBMISSION, "submission2");
		request.setParameter("param1", "param1requestvalue");
		response = conversation.getResponse(request);
		assertEquals("outcookie1elementvalue", conversation.getCookieValue("outcookie1"));
		assertEquals("outcookiedefault", conversation.getCookieValue("outcookie2"));
		assertEquals("outcookie3elementvalue", conversation.getCookieValue("outcookie3"));
		assertEquals("outcookiedefault", conversation.getCookieValue("outcookie4"));
		assertEquals("Adding"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"input1requestvalue"+
			"inputdefault"+
			"input3requestvalue"+
			"inputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault"+
			"param1requestvalue"+
			"submission2paramdefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/adding");
		request.setParameter("switch", "exit1");
		response = conversation.getResponse(request);
		assertEquals("Adding"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit1"+
			"outputdefault"+
			"output3exit1"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/adding");
		request.setParameter("switch", "exit2");
		response = conversation.getResponse(request);
		assertEquals("Adding"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit2"+
			"outputdefault"+
			"output3exit2"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/adding");
		request.setParameter("switch", "exit3");
		response = conversation.getResponse(request);
		assertEquals("Adding"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit3"+
			"outputdefault"+
			"output3exit3"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/adding");
		request.setParameter("switch", "exit4");
		response = conversation.getResponse(request);
		assertEquals("Adding"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit4"+
			"outputdefault"+
			"output3exit4"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());
	}

	public void testExtendingAddingInSite()
	throws Exception
	{
		setupSite("site/extending_in_site.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		conversation.addCookie("incookie1", "incookie1requestvalue");
		conversation.addCookie("incookie3", "incookie3requestvalue");
		request = new PostMethodWebRequest("http://localhost:8181/extending/adding");
		request.setParameter("input1", "input1requestvalue");
		request.setParameter("input3", "input3requestvalue");
		request.setParameter(ReservedParameters.SUBMISSION, "submission2");
		request.setParameter("param1", "param1requestvalue");
		response = conversation.getResponse(request);
		assertEquals("outcookie1elementvalue", conversation.getCookieValue("outcookie1"));
		assertEquals("outcookiedefault", conversation.getCookieValue("outcookie2"));
		assertEquals("outcookie3elementvalue", conversation.getCookieValue("outcookie3"));
		assertEquals("outcookiedefault", conversation.getCookieValue("outcookie4"));
		assertEquals("Adding"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"input1requestvalue"+
			"inputdefault"+
			"input3requestvalue"+
			"inputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault"+
			"param1requestvalue"+
			"submission2paramdefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/adding");
		request.setParameter("switch", "exit1");
		response = conversation.getResponse(request);
		assertEquals("Adding"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit1"+
			"outputdefault"+
			"output3exit1"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/adding");
		request.setParameter("switch", "exit2");
		response = conversation.getResponse(request);
		assertEquals("Adding"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit2"+
			"outputdefault"+
			"output3exit2"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/adding");
		request.setParameter("switch", "exit3");
		response = conversation.getResponse(request);
		assertEquals("Adding"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit3"+
			"outputdefault"+
			"output3exit3"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/adding");
		request.setParameter("switch", "exit4");
		response = conversation.getResponse(request);
		assertEquals("Adding"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit4"+
			"outputdefault"+
			"output3exit4"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());
	}

	public void testExtendingMultipleLevels()
	throws Exception
	{
		setupSite("site/extending.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		conversation.addCookie("incookie1", "incookie1requestvalue");
		conversation.addCookie("incookie3", "incookie3requestvalue");
		request = new PostMethodWebRequest("http://localhost:8181/extending/multiplelevels");
		request.setParameter("input1", "input1requestvalue");
		request.setParameter("input3", "input3requestvalue");
		request.setParameter(ReservedParameters.SUBMISSION, "submission2");
		request.setParameter("param1", "param1requestvalue");
		response = conversation.getResponse(request);
		assertEquals("outcookie1elementvalue", conversation.getCookieValue("outcookie1"));
		assertEquals("outcookiedefault", conversation.getCookieValue("outcookie2"));
		assertEquals("outcookie3elementvalue", conversation.getCookieValue("outcookie3"));
		assertEquals("outcookiedefault", conversation.getCookieValue("outcookie4"));
		assertEquals("MultipleLevels"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"input1requestvalue"+
			"inputdefault"+
			"input3requestvalue"+
			"inputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault"+
			"param1requestvalue"+
			"submission2paramdefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/multiplelevels");
		request.setParameter("switch", "exit1");
		response = conversation.getResponse(request);
		assertEquals("MultipleLevels"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit1"+
			"outputdefault"+
			"output3exit1"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/multiplelevels");
		request.setParameter("switch", "exit2");
		response = conversation.getResponse(request);
		assertEquals("MultipleLevels"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit2"+
			"outputdefault"+
			"output3exit2"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/multiplelevels");
		request.setParameter("switch", "exit3");
		response = conversation.getResponse(request);
		assertEquals("MultipleLevels"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit3"+
			"outputdefault"+
			"output3exit3"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/multiplelevels");
		request.setParameter("switch", "exit4");
		response = conversation.getResponse(request);
		assertEquals("MultipleLevels"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit4"+
			"outputdefault"+
			"output3exit4"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());
	}

	public void testExtendingMultipleLevelsInSite()
	throws Exception
	{
		setupSite("site/extending_in_site.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		conversation.addCookie("incookie1", "incookie1requestvalue");
		conversation.addCookie("incookie3", "incookie3requestvalue");
		request = new PostMethodWebRequest("http://localhost:8181/extending/multiplelevels");
		request.setParameter("input1", "input1requestvalue");
		request.setParameter("input3", "input3requestvalue");
		request.setParameter(ReservedParameters.SUBMISSION, "submission2");
		request.setParameter("param1", "param1requestvalue");
		response = conversation.getResponse(request);
		assertEquals("outcookie1elementvalue", conversation.getCookieValue("outcookie1"));
		assertEquals("outcookiedefault", conversation.getCookieValue("outcookie2"));
		assertEquals("outcookie3elementvalue", conversation.getCookieValue("outcookie3"));
		assertEquals("outcookiedefault", conversation.getCookieValue("outcookie4"));
		assertEquals("MultipleLevels"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"input1requestvalue"+
			"inputdefault"+
			"input3requestvalue"+
			"inputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault"+
			"param1requestvalue"+
			"submission2paramdefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/multiplelevels");
		request.setParameter("switch", "exit1");
		response = conversation.getResponse(request);
		assertEquals("MultipleLevels"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit1"+
			"outputdefault"+
			"output3exit1"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/multiplelevels");
		request.setParameter("switch", "exit2");
		response = conversation.getResponse(request);
		assertEquals("MultipleLevels"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit2"+
			"outputdefault"+
			"output3exit2"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/multiplelevels");
		request.setParameter("switch", "exit3");
		response = conversation.getResponse(request);
		assertEquals("MultipleLevels"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit3"+
			"outputdefault"+
			"output3exit3"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/extending/multiplelevels");
		request.setParameter("switch", "exit4");
		response = conversation.getResponse(request);
		assertEquals("MultipleLevels"+
			"value1"+
			"value2"+
			"value3"+
			"value4"+
			"output1exit4"+
			"outputdefault"+
			"output3exit4"+
			"outputdefault"+
			"incookie1requestvalue"+
			"incookiedefault"+
			"incookie3requestvalue"+
			"incookiedefault", response.getText());
	}
}