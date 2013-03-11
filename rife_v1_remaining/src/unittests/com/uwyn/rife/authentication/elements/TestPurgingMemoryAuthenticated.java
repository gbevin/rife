/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestPurgingMemoryAuthenticated.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.authentication.SessionManager;
import com.uwyn.rife.engine.Gate;
import com.uwyn.rife.tools.ExceptionUtils;

public class TestPurgingMemoryAuthenticated extends TestCaseServerside
{
	public TestPurgingMemoryAuthenticated(int siteType, String name)
	{
		super(siteType, name);
	}

	private SessionManager getSessionManagerInstance(Gate gate, String authElementId)
	{
		return ((AuthenticatedDeployer)gate.getSite().resolveId(authElementId).getDeployer()).getSessionValidator().getSessionManager();
	}

	public void testPurgingMemoryAuthenticatedBasicInput()
	throws Exception
	{
		Gate gate = setupSite("site/authentication_memory_input.xml");
		
		SessionManager session_manager = getSessionManagerInstance(gate, ".INPUT.PURGING_MEMORY_AUTHENTICATED_BASIC");
		assertEquals(0, session_manager.countSessions());
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		String auth_id;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();
		
		assertEquals(1, session_manager.countSessions());
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();

		assertEquals(2, session_manager.countSessions());
		
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/memory/basic");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(1, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/memory/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);

		assertEquals(1, session_manager.countSessions());
		session_manager.eraseAllSessions();
	}
	
	public void testPurgingMemoryAuthenticatedBasicCookie()
	throws Exception
	{
		Gate gate = setupSite("site/authentication_memory_cookie.xml");
		
		SessionManager session_manager = getSessionManagerInstance(gate, ".COOKIE.PURGING_MEMORY_AUTHENTICATED_BASIC");
		assertEquals(0, session_manager.countSessions());
		
		WebConversation	conversation1 = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/memory/basic");
		response = conversation1.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		
		assertEquals("authid", response.getNewCookieNames()[0]);
		assertEquals(0, response.getForms().length);
		
		assertEquals(1, session_manager.countSessions());
		
		WebConversation conversation2 = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/memory/basic");
		response = conversation2.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		
		assertEquals("authid", response.getNewCookieNames()[0]);
		assertEquals(0, response.getForms().length);
		
		assertEquals(2, session_manager.countSessions());
		
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/memory/basic");
		response = conversation1.getResponse(request);
		
		assertEquals(0, response.getNewCookieNames().length);
		assertEquals(1, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/memory/basic");
		response = conversation1.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		
		assertEquals("authid", response.getNewCookieNames()[0]);
		assertEquals(0, response.getForms().length);
		
		assertEquals(1, session_manager.countSessions());
		session_manager.eraseAllSessions();
	}
}

