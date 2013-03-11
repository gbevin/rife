/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestMemoryAuthenticated.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.engine.SiteBuilder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.test.MockConversation;
import com.uwyn.rife.test.MockForm;
import com.uwyn.rife.test.MockRequest;
import com.uwyn.rife.test.MockResponse;
import com.uwyn.rife.test.ParsedHtml;
import com.uwyn.rife.tools.ExceptionUtils;

public class TestMemoryAuthenticated extends TestCaseServerside
{
	public TestMemoryAuthenticated(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testMemoryAuthenticatedBasicInput()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();

		assertEquals(1, response.getForms().length);
	}
	
	public void testMemoryAuthenticatedBasicCookie()
	throws Exception
	{
		setupSite("site/authentication_memory_cookie.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		
		assertEquals("authid", response.getNewCookieNames()[0]);
		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		assertEquals(0, response.getForms().length);
		
		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();
		
		assertEquals("authid", response.getNewCookieNames()[0]);
		assertEquals(0, response.getForms().length);
		
		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();
		
		assertEquals(0, response.getNewCookieNames().length);
		assertEquals(1, response.getForms().length);
	}
	
	public void testMemoryAuthenticatedXhtmlInput()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/xhtml");
		response = conversation.getResponse(request);
		assertTrue(response.getText().indexOf("This is XHtml :") != -1);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/xhtml");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/xhtml");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();

		assertEquals(1, response.getForms().length);
	}

	public void testMemoryAuthenticatedXhtmlCookie()
	throws Exception
	{
		setupSite("site/authentication_memory_cookie.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/xhtml");
		response = conversation.getResponse(request);
		assertTrue(response.getText().indexOf("This is XHtml :") != -1);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals("authid", response.getNewCookieNames()[0]);
		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/xhtml");
		assertEquals(0, response.getForms().length);

		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/xhtml");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals("authid", response.getNewCookieNames()[0]);
		assertEquals(0, response.getForms().length);
		
		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/xhtml");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();

		assertEquals(0, response.getNewCookieNames().length);
		assertEquals(1, response.getForms().length);
	}

	public void testMemoryAuthenticatedEncryptedInput()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevinencrypted");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guestencrypted");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();

		assertEquals(1, response.getForms().length);
	}

	public void testMemoryAuthenticatedEncryptedCookie()
	throws Exception
	{
		setupSite("site/authentication_memory_cookie.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		
		assertEquals("authid", response.getNewCookieNames()[0]);
		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		assertEquals(0, response.getForms().length);

		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevinencrypted");
		form.setParameter("password", "yeolpass");
		response = form.submit();
		
		assertEquals("authid", response.getNewCookieNames()[0]);
		assertEquals(0, response.getForms().length);

		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guestencrypted");
		form.setParameter("password", "guestpass");
		response = form.submit();
		
		assertEquals("authid", response.getNewCookieNames()[0]);
		assertEquals(0, response.getForms().length);

		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals("authid", response.getNewCookieNames()[0]);
		assertEquals(0, response.getForms().length);

		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();

		assertEquals(0, response.getNewCookieNames().length);
		assertEquals(1, response.getForms().length);
	}

	public void testMemoryAuthenticatedOtheridInput()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		String auth_id;

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(0, response.getForms().length);
		assertEquals(auth_id, response.getTitle());

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/otherid");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(1, response.getForms().length);
		assertTrue(!auth_id.equals(response.getTitle()));

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/otherid");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/otherid");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(0, response.getForms().length);
		assertEquals(auth_id, response.getTitle());

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/basic");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(1, response.getForms().length);
		assertTrue(!auth_id.equals(response.getTitle()));
	}

	public void testMemoryAuthenticatedOtheridCookie()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/authentication_memory_cookie.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse	response;
		MockForm		form;
		ParsedHtml		parsed;

		String auth_id;

		response = conversation.doRequest("/authentication/memory/basic");
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		parsed = response.getParsedHtml();
		
		assertEquals(0, parsed.getForms().size());
		assertEquals("authid", response.getNewCookieNames().get(0));
		auth_id = conversation.getCookieValue("authid");

		conversation = new MockConversation(builder.getSite());
		conversation.addCookie("authid", auth_id);
		response = conversation.doRequest("/authentication/memory/basic");
		parsed = response.getParsedHtml();
		
		assertEquals(0, parsed.getForms().size());
		auth_id = parsed.getTitle();
		
		conversation = new MockConversation(builder.getSite());
		conversation.addCookie("authid", auth_id);
		response = conversation.doRequest("/authentication/memory/otherid");
		parsed = response.getParsedHtml();
		
		assertEquals(0, response.getNewCookieNames().size());
		assertEquals(1, parsed.getForms().size());

		conversation = new MockConversation(builder.getSite());
		conversation.addCookie("authid", auth_id);
		response = conversation.doRequest("/authentication/memory/otherid");
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		parsed = response.getParsedHtml();
		
		assertEquals(0, parsed.getForms().size());
		assertEquals("authid", response.getNewCookieNames().get(0));
		auth_id = conversation.getCookieValue("authid");
		
		conversation = new MockConversation(builder.getSite());
		conversation.addCookie("authid", auth_id);
		response = conversation.doRequest("/authentication/memory/otherid");
		parsed = response.getParsedHtml();
		
		assertEquals(0, parsed.getForms().size());

		response = conversation.doRequest("/authentication/memory/basic");
		parsed = response.getParsedHtml();
		
		assertEquals(1, parsed.getForms().size());
	}

	public void testMemoryAuthenticatedRoleInput()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(1, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role2");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "johndoe");
		form.setParameter("password", "thepassofbass");
		response = form.submit();

		assertEquals(1, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role2");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "johndoe");
		form.setParameter("password", "thepassofbass");
		response = form.submit();

		assertEquals(0, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();

		assertEquals(1, response.getForms().length);
	}

	public void testMemoryAuthenticatedRoleCookie()
	throws Exception
	{
		setupSite("site/authentication_memory_cookie.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getNewCookieNames().length);
		assertEquals(1, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals("authid", response.getNewCookieNames()[0]);
		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role");
		assertEquals(0, response.getForms().length);

		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role2");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals("authid", response.getNewCookieNames()[0]);
		assertEquals(0, response.getForms().length);
		
		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "johndoe");
		form.setParameter("password", "thepassofbass");
		response = form.submit();

		assertEquals(0, response.getNewCookieNames().length);
		assertEquals(1, response.getForms().length);

		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role2");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "johndoe");
		form.setParameter("password", "thepassofbass");
		response = form.submit();

		assertEquals("authid", response.getNewCookieNames()[0]);
		assertEquals(0, response.getForms().length);

		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();

		assertEquals(0, response.getNewCookieNames().length);
		assertEquals(1, response.getForms().length);
	}

	public void testMemoryAuthenticatedRoleIsolationInput()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		String auth_id = null;

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(auth_id, response.getTitle());

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role2");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(auth_id, response.getTitle());

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role2");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "johndoe");
		form.setParameter("password", "thepassofbass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role2");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(auth_id, response.getTitle());

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/role");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(1, response.getForms().length);
	 }

	public void testMemoryAuthenticatedRoleIsolationCookie()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/authentication_memory_cookie.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse	response;
		MockForm		form;
		ParsedHtml		parsed;
		
		String auth_id;
		
		response = conversation.doRequest("/authentication/memory/role");
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();
		parsed = response.getParsedHtml();
		
		assertEquals(0, parsed.getForms().size());
		assertEquals("authid", response.getNewCookieNames().get(0));
		auth_id = conversation.getCookieValue("authid");
		
		conversation = new MockConversation(builder.getSite());
		conversation.addCookie("authid", auth_id);
		response = conversation.doRequest("/authentication/memory/role");
		parsed = response.getParsedHtml();
		
		assertEquals(0, parsed.getForms().size());

		conversation = new MockConversation(builder.getSite());
		conversation.addCookie("authid", auth_id);
		response = conversation.doRequest("/authentication/memory/role2");
		parsed = response.getParsedHtml();
		
		assertEquals(0, parsed.getForms().size());
		
		conversation = new MockConversation(builder.getSite());
		response = conversation.doRequest("/authentication/memory/role2");
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		form.setParameter("login", "johndoe");
		form.setParameter("password", "thepassofbass");
		response = form.submit();
		parsed = response.getParsedHtml();
		
		assertEquals(0, parsed.getForms().size());
		assertEquals("authid", response.getNewCookieNames().get(0));
		auth_id = conversation.getCookieValue("authid");
		
		conversation = new MockConversation(builder.getSite());
		conversation.addCookie("authid", auth_id);
		response = conversation.doRequest("/authentication/memory/role2");
		parsed = response.getParsedHtml();
		
		assertEquals(0, parsed.getForms().size());
		
		conversation = new MockConversation(builder.getSite());
		conversation.addCookie("authid", auth_id);
		response = conversation.doRequest("/authentication/memory/role");
		parsed = response.getParsedHtml();
		
		assertEquals(1, parsed.getForms().size());
	}

	public void testMemoryAuthenticatedSessiondurationInput()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		String auth_id;

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/sessionduration");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/sessionduration");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(0, response.getForms().length);
		assertEquals(auth_id, response.getTitle());

		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		request = new GetMethodWebRequest("http://localhost:8181/authentication/memory/sessionduration");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(1, response.getForms().length);
	 }

	public void testMemoryAuthenticatedSessiondurationCookie()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/authentication_memory_cookie.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse	response;
		MockForm		form;
		ParsedHtml		parsed;
		
		String auth_id;
		
		response = conversation.doRequest("/authentication/memory/sessionduration");
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		parsed = response.getParsedHtml();

		assertEquals(0, parsed.getForms().size());
		assertEquals("authid", response.getNewCookieNames().get(0));
		auth_id = conversation.getCookieValue("authid");
		
		conversation = new MockConversation(builder.getSite());
		conversation.addCookie("authid", auth_id);
		response = conversation.doRequest("/authentication/memory/sessionduration");
		parsed = response.getParsedHtml();
		
		assertEquals(0, parsed.getForms().size());
		
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		conversation = new MockConversation(builder.getSite());
		conversation.addCookie("authid", auth_id);
		response = conversation.doRequest("/authentication/memory/sessionduration");
		parsed = response.getParsedHtml();
		
		assertEquals(1, parsed.getForms().size());
	}

	/**
	 * Tests that we can get through an authenticated element that has its
	 * "enforce_authenticated" flag set to false.
	 */
	public void testMemoryAuthenticatedNotEnforcedInput()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/authentication_memory_input.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse	response;
		MockRequest		request;
		MockForm		form;
		ParsedHtml		parsed;
		String			auth_id;
		
		/* Can we get through the page as an anonymous user? */
		response = conversation.doRequest("/authentication/memory/enforce");
		parsed = response.getParsedHtml();
		
		assertEquals("forms", 0, parsed.getForms().size());
		assertEquals("login name", "(none)", parsed.getLinkWithId("userLogin").getText());
		
		/* Now hit a protected page so we can get an authid cookie. */
		response = conversation.doRequest("/authentication/memory/enforce2");
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		parsed = response.getParsedHtml();
		
		assertEquals("forms (after login)", 0, parsed.getForms().size());
		assertEquals("login name (after login)", "guest", parsed.getLinkWithId("userLogin").getText());

		auth_id = parsed.getTitle();

		/* And hit the unprotected page again to be sure it recognizes us. */
		conversation = new MockConversation(builder.getSite());
		request = new MockRequest();
		request.setParameter("authid", auth_id);
		response = conversation.doRequest("/authentication/memory/enforce", request);
		parsed = response.getParsedHtml();
		
		assertEquals("forms (unenforced after login)", 0, parsed.getForms().size());
		assertEquals("login name (unenforced after login)", "guest", parsed.getLinkWithId("userLogin").getText());
	}

	/**
	 * Tests that we can get through an authenticated element that has its
	 * "enforce_authenticated" flag set to false.
	 */
	public void testMemoryAuthenticatedNotEnforcedCookie()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/authentication_memory_cookie.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse	response;
		MockForm		form;
		ParsedHtml		parsed;
		String			auth_id;
		
		/* Can we get through the page as an anonymous user? */
		response = conversation.doRequest("/authentication/memory/enforce");
		parsed = response.getParsedHtml();
		
		assertEquals("forms", 0, parsed.getForms().size());
		assertEquals("login name", "(none)", parsed.getLinkWithId("userLogin").getText());
		
		/* Now hit a protected page so we can get an authid cookie. */
		response = conversation.doRequest("/authentication/memory/enforce2");
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		parsed = response.getParsedHtml();
		
		assertEquals("forms (after login)", 0, parsed.getForms().size());
		assertEquals("login name (after login)", "guest", parsed.getLinkWithId("userLogin").getText());

		auth_id = conversation.getCookieValue("authid");

		/* And hit the unprotected page again to be sure it recognizes us. */
		conversation = new MockConversation(builder.getSite());
		conversation.addCookie("authid", auth_id);
		response = conversation.doRequest("/authentication/memory/enforce");
		parsed = response.getParsedHtml();
		
		assertEquals("forms (unenforced after login)", 0, parsed.getForms().size());
		assertEquals("login name (unenforced after login)", "guest", parsed.getLinkWithId("userLogin").getText());
	}
}

