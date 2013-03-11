/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestRoleUsersManagerRetriever.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.AuthenticatedElementNotFoundException;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.NotAuthenticatedElementException;

public class TestRoleUsersManagerRetriever extends TestCaseServerside
{
	public TestRoleUsersManagerRetriever(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testValidAuthElement()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/roleusersmanagerretriever/valid_auth_element");
		response = conversation.getResponse(request);
		assertEquals("gbevin", response.getText());
	}

	public void testAuthElementNotFound()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		
		try
		{
			request = new GetMethodWebRequest("http://localhost:8181/roleusersmanagerretriever/auth_element_not_found");
			conversation.getResponse(request);
			fail();
		}
		catch (Throwable e)
		{
			assertTrue(getLogSink().getInternalException() instanceof AuthenticatedElementNotFoundException);
			assertEquals(".UNAVAILABLE_ELEMENT", ((AuthenticatedElementNotFoundException)getLogSink().getInternalException()).getElementId());
		}
	}

	public void testElementNotAuthenticated()
	throws Exception
	{
		setupSite("site/authentication_memory_input.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		
		try
		{
			request = new GetMethodWebRequest("http://localhost:8181/roleusersmanagerretriever/element_not_authenticated");
			conversation.getResponse(request);
			fail();
		}
		catch (Throwable e)
		{
			assertTrue(getLogSink().getInternalException() instanceof NotAuthenticatedElementException);
			assertEquals(".INPUT.MEMORY_AUTHENTICATED_BASIC_TARGET", ((NotAuthenticatedElementException)getLogSink().getInternalException()).getElementId());
		}
	}
}

