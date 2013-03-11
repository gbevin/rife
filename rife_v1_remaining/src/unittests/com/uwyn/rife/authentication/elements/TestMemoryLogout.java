/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestMemoryLogout.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.TestCaseServerside;

public class TestMemoryLogout extends TestCaseServerside
{
	public TestMemoryLogout(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testMemoryLogoutTemplateBasicInput()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm 	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		assertEquals(0, response.getForms().length);

		response = response.getLinkWith("reload").click();
		assertEquals(0, response.getForms().length);

		WebResponse	response_logout = response.getLinkWith("logout template").click();
		assertEquals("logged out memory", response_logout.getTitle());
		
		response = response.getLinkWith("reload").click();
		assertEquals(1, response.getForms().length);
	}
	
	public void testMemoryLogoutTemplateBasicCookie()
	throws Exception
	{
		setupSite("site/authentication_memory_cookie.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm 	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		assertEquals("authid", response.getNewCookieNames()[0]);
		assertFalse("".equals(conversation.getCookieValue("authid")));
		assertEquals(0, response.getForms().length);
		
		response = response.getLinkWith("reload").click();
		assertEquals(0, response.getForms().length);
		
		WebResponse	response_logout = response.getLinkWith("logout template").click();
		assertEquals("logged out memory", response_logout.getTitle());
		assertEquals("", conversation.getCookieValue("authid"));
		
		response = response.getLinkWith("reload").click();
		assertEquals(1, response.getForms().length);
	}
	
	public void testMemoryLogoutTemplateXhtmlInput()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm 	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/xhtml");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		assertEquals(0, response.getForms().length);
		
		response = response.getLinkWith("reload").click();
		assertEquals(0, response.getForms().length);
		
		WebResponse	response_logout = response.getLinkWith("logout template").click();
		assertEquals("logged out memory", response_logout.getTitle());
		assertTrue(response_logout.getText().indexOf("This is XHtml") != -1);
		
		response = response.getLinkWith("reload").click();
		assertEquals(1, response.getForms().length);
	}
	
	public void testMemoryLogoutTemplateXhtmlCookie()
	throws Exception
	{
		setupSite("site/authentication_memory_cookie.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm 	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/xhtml");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		assertEquals("authid", response.getNewCookieNames()[0]);
		assertFalse("".equals(conversation.getCookieValue("authid")));
		assertEquals(0, response.getForms().length);

		response = response.getLinkWith("reload").click();
		assertEquals(0, response.getForms().length);

		WebResponse	response_logout = response.getLinkWith("logout template").click();
		assertEquals("logged out memory", response_logout.getTitle());
		assertEquals("", conversation.getCookieValue("authid"));
		assertTrue(response_logout.getText().indexOf("This is XHtml") != -1);
		
		response = response.getLinkWith("reload").click();
		assertEquals(1, response.getForms().length);
	}

	public void testMemoryLogoutTemplateOtheridInput()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebForm		form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		WebResponse response_basic = conversation.getResponse(request);
		form = response_basic.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response_basic = form.submit();
		assertEquals(0, response_basic.getForms().length);
		
		response_basic = response_basic.getLinkWith("reload").click();
		assertEquals(0, response_basic.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/otherid");
		WebResponse response_otherid = conversation.getResponse(request);
		form = response_otherid.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response_otherid = form.submit();
		assertEquals(0, response_otherid.getForms().length);
		
		response_otherid = response_otherid.getLinkWith("reload").click();
		assertEquals(0, response_otherid.getForms().length);

		WebResponse	response_logout = null;
		response_logout = response_basic.getLinkWith("logout template").click();
		assertEquals("logged out memory", response_logout.getTitle());
		response_basic = response_basic.getLinkWith("reload").click();
		assertEquals(1, response_basic.getForms().length);
		response_otherid = response_otherid.getLinkWith("reload").click();
		assertEquals(0, response_otherid.getForms().length);

		response_logout = response_otherid.getLinkWith("logout template").click();
		assertEquals("logged out memory", response_logout.getTitle());
		response_otherid = response_otherid.getLinkWith("reload").click();
		assertEquals(1, response_otherid.getForms().length);
	}

	public void testMemoryLogoutTemplateOtheridCookie()
	throws Exception
	{
		setupSite("site/authentication_memory_cookie.xml");
		
		WebRequest	request = null;
		WebForm		form = null;
		
		WebConversation	conversation_basic = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		WebResponse response_basic = conversation_basic.getResponse(request);
		form = response_basic.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response_basic = form.submit();
		assertEquals("authid", response_basic.getNewCookieNames()[0]);
		assertFalse("".equals(conversation_basic.getCookieValue("authid")));
		assertEquals(0, response_basic.getForms().length);
		
		response_basic = response_basic.getLinkWith("reload").click();
		assertEquals(0, response_basic.getForms().length);
		
		WebConversation	conversation_otherid = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/otherid");
		WebResponse response_otherid = conversation_otherid.getResponse(request);
		form = response_otherid.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response_otherid = form.submit();

		response_basic = response_basic.getLinkWith("reload").click();
		assertEquals(0, response_otherid.getForms().length);
		
		response_otherid = response_otherid.getLinkWith("reload").click();
		assertEquals(0, response_otherid.getForms().length);
		
		WebResponse	response_logout = null;
		response_logout = response_basic.getLinkWith("logout template").click();
		assertEquals("logged out memory", response_logout.getTitle());
		assertEquals("", conversation_basic.getCookieValue("authid"));
		response_basic = response_basic.getLinkWith("reload").click();
		assertEquals(1, response_basic.getForms().length);
		response_otherid = response_otherid.getLinkWith("reload").click();
		assertEquals(0, response_otherid.getForms().length);
		
		response_logout = response_otherid.getLinkWith("logout template").click();
		assertEquals("logged out memory", response_logout.getTitle());
		assertEquals("", conversation_otherid.getCookieValue("authid"));
		response_otherid = response_otherid.getLinkWith("reload").click();
		assertEquals(1, response_otherid.getForms().length);
	}
	
	public void testMemoryLogoutPassthroughBasicInput()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm 	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		assertEquals(0, response.getForms().length);

		response = response.getLinkWith("reload").click();
		assertEquals(0, response.getForms().length);

		response = response.getLinkWith("logout passthrough").click();
		assertEquals(1, response.getForms().length);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		assertEquals(0, response.getForms().length);
	}

	public void testMemoryLogoutPassthroughBasicCookie()
	throws Exception
	{
		setupSite("site/authentication_memory_cookie.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm 	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		assertEquals("authid", response.getNewCookieNames()[0]);
		assertFalse("".equals(conversation.getCookieValue("authid")));
		assertEquals(0, response.getForms().length);
		
		response = response.getLinkWith("reload").click();
		assertEquals(0, response.getForms().length);
		
		response = response.getLinkWith("logout passthrough").click();
		assertEquals("", conversation.getCookieValue("authid"));
		assertEquals(1, response.getForms().length);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		assertEquals(0, response.getForms().length);
	}
	
	public void testMemoryLogoutPrecedenceInput()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm 	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/precedence");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		assertEquals(0, response.getForms().length);

		response = response.getLinkWith("reload").click();
		assertEquals(0, response.getForms().length);

		response = response.getLinkWith("logout").click();
		assertEquals(1, response.getForms().length);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		assertEquals(0, response.getForms().length);
	}
	
	public void testMemoryLogoutPrecedenceCookie()
	throws Exception
	{
		setupSite("site/authentication_memory_cookie.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm 	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/precedence");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		assertEquals("authid", response.getNewCookieNames()[0]);
		assertFalse("".equals(conversation.getCookieValue("authid")));
		assertEquals(0, response.getForms().length);
		
		response = response.getLinkWith("reload").click();
		assertEquals(0, response.getForms().length);
		
		response = response.getLinkWith("logout").click();
		assertEquals("", conversation.getCookieValue("authid"));
		assertEquals(1, response.getForms().length);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		assertEquals(0, response.getForms().length);
	}
}

