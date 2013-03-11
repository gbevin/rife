/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestAuthenticationUtils.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication;

import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.engine.SiteBuilder;
import com.uwyn.rife.test.MockConversation;
import com.uwyn.rife.test.MockResponse;

public class TestAuthenticationUtils extends TestCaseServerside
{
	public TestAuthenticationUtils(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testStartAuthSession()
	throws Throwable
	{
		SiteBuilder builder = new SiteBuilder("site/authentication_memory_input.xml");
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse response = conversation.doRequest("/authenticationutils/startauthsession");
		String authid = response.getText();
		
		SessionValidator validator = SessionValidatorRetriever.getSessionValidator(builder.getSite(), ".INPUT.MEMORY_AUTHENTICATED_BASIC", null);
		assertTrue(validator.getSessionManager().isSessionValid(authid, "127.0.0.1"));
	}
	
	public void testStartAuthSession2()
	throws Throwable
	{
		SiteBuilder builder = new SiteBuilder("site/authentication_memory_input.xml");
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse response = conversation.doRequest("/authenticationutils/startauthsession2");
		String authid = response.getText();
		
		SessionValidator validator = SessionValidatorRetriever.getSessionValidator(builder.getSite(), ".INPUT.MEMORY_AUTHENTICATED_BASIC", null);
		assertTrue(validator.getSessionManager().isSessionValid(authid, "127.0.0.1"));
	}
}

