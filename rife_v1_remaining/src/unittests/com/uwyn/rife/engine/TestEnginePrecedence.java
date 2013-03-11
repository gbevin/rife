/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEnginePrecedence.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.TestCaseServerside;

public class TestEnginePrecedence extends TestCaseServerside
{
	public TestEnginePrecedence(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testSimple()
	throws Exception
	{
		setupSite("site/precedence.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/simple");
		response = conversation.getResponse(request);
		assertEquals("This is the simple pre content"+
			"This is the simple target content", response.getText());
	}

	public void testMultiple()
	throws Exception
	{
		setupSite("site/precedence.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/multiple");
		response = conversation.getResponse(request);
		assertEquals("This is the multiple pre3 content"+
			"This is the multiple pre2 content"+
			"This is the multiple pre1 content"+
			"This is the multiple target content", response.getText());
	}

	public void testTargetinheritance()
	throws Exception
	{
		setupSite("site/precedence.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/targetinheritance");
		response = conversation.getResponse(request);
		assertEquals("This is the target inheritance parent content", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/targetinheritance");
		request.setParameter("trigger", "ok");
		response = conversation.getResponse(request);
		assertEquals("This is the target inheritance pre content"+
			"This is the target inheritance target content", response.getText());
	}

	public void testPreinheritance()
	throws Exception
	{
		setupSite("site/precedence.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/preinheritance");
		response = conversation.getResponse(request);
		assertEquals("This is the pre inheritance parent content"+
			"This is the pre inheritance target content", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/preinheritance");
		request.setParameter("trigger", "ok");
		response = conversation.getResponse(request);
		assertEquals("This is the pre inheritance pre content"+
			"This is the pre inheritance target content", response.getText());
	}

	public void testGlobalvars()
	throws Exception
	{
		setupSite("site/precedence.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/globalvars");
		request.setParameter("globalvar1", "globalvar1_requestvalue");
		request.setParameter("globalvar2", "globalvar2_requestvalue");
		request.setParameter("globalvar3", "globalvar3_requestvalue");
		request.setParameter("globalvar4", "globalvar4_requestvalue");
		request.setParameter("globalvar5", "globalvar5_requestvalue");
		request.setParameter("globalvar6", "globalvar6_requestvalue");
		response = conversation.getResponse(request);
		assertEquals("This is the globalvars target content"+
			":globalvar1_prevalue"+
			":null"+
			":globalvar3_requestvalue"+
			":null"+
			":globalvar5_prevalue"+
			":globalvar6_requestvalue", response.getText());
	}

	public void testCookies()
	throws Exception
	{
		setupSite("site/precedence.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/cookies");
		conversation.addCookie("cookie1", "cookie1_requestvalue");
		conversation.addCookie("cookie2", "cookie2_requestvalue");
		conversation.addCookie("cookie3", "cookie3_requestvalue");
		conversation.addCookie("cookie4", "cookie4_requestvalue");
		conversation.addCookie("cookie5", "cookie5_requestvalue");
		conversation.addCookie("cookie6", "cookie6_requestvalue");
		response = conversation.getResponse(request);
		assertEquals("This is the cookies target content"+
			":cookie1_prevalue"+
			":cookie2_requestvalue"+
			":cookie3_prevalue"+
			":cookie4_prevalue"+
			":cookie5_prevalue", response.getText());
	}

	public void testInheritanceprecedence()
	throws Exception
	{
		setupSite("site/precedence.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritanceprecedence");
		response = conversation.getResponse(request);
		assertEquals("This is the inheritance precedence pre content"+
			"This is the inheritance precedence parent content", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritanceprecedence");
		request.setParameter("trigger", "ok");
		response = conversation.getResponse(request);
		assertEquals("This is the inheritance precedence target content", response.getText());
	}

	public void testGlobalScopeAbsolute()
	throws Exception
	{
		setupSite("site/precedence.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/globalscope/absolute");
		response = conversation.getResponse(request);
		assertEquals("This is the simple pre content"+
			"This is the simple target content", response.getText());
	}

	public void testGlobalScopeRelative()
	throws Exception
	{
		setupSite("site/precedence.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/globalscope/relative");
		response = conversation.getResponse(request);
		assertEquals("This is the simple pre content"+
			"This is the simple target content", response.getText());
	}
}

