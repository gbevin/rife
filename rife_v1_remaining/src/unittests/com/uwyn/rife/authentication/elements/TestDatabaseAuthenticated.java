/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseAuthenticated.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.authentication.credentialsmanagers.DatabaseUsers;
import com.uwyn.rife.authentication.credentialsmanagers.DatabaseUsersFactory;
import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.authentication.exceptions.RememberManagerException;
import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.authentication.remembermanagers.DatabaseRemember;
import com.uwyn.rife.authentication.remembermanagers.RememberManagerFactoryFactory;
import com.uwyn.rife.authentication.sessionmanagers.DatabaseSessions;
import com.uwyn.rife.authentication.sessionmanagers.SessionManagerFactoryFactory;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.engine.SiteBuilder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.test.MockConversation;
import com.uwyn.rife.test.MockForm;
import com.uwyn.rife.test.MockRequest;
import com.uwyn.rife.test.MockResponse;
import com.uwyn.rife.test.ParsedHtml;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.StringEncryptor;

public class TestDatabaseAuthenticated extends TestsuiteDatabaseAuthenticated
{
	private Datasource	mDatasource = null;
	
	public TestDatabaseAuthenticated(String datasourceName, int siteType, String name)
	{
		super(datasourceName, siteType, name);
		
		mDatasource = Datasources.getRepInstance().getDatasource(datasourceName);
		mProperties.put("datasource", mDatasource);
		mProperties.put(SessionManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, "DatabaseSessionsFactory");
		mProperties.put(RememberManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, "DatabaseRememberFactory");
	}
	
	public void setUp()
	throws Exception
	{
		super.setUp();
		
		DatabaseUsers	users = DatabaseUsersFactory.getInstance(mDatasource);
		try
		{
			users.install();
			
			users.addRole("admin");
			users.addRole("maint");
			 
			users.addUser("guest", new RoleUserAttributes(43, "guestpass"));
			users.addUser("gbevin", new RoleUserAttributes(432, "yeolpass", new String[] {"admin", "maint"}));
			users.addUser("johndoe", new RoleUserAttributes(174, "thepassofbass", new String[] {"maint"}));
			users.setPasswordEncryptor(StringEncryptor.SHA);
			users.addUser("guestencrypted", new RoleUserAttributes(44, "guestpass"));
			users.addUser("gbevinencrypted", new RoleUserAttributes(433, "yeolpass", new String[] {"admin", "maint"}));
			users.setPasswordEncryptor(null);
		}
		catch (CredentialsManagerException e)
		{
			try
			{
				users.remove();
			}
			catch (CredentialsManagerException e2)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e2), false);
			}
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		DatabaseSessions sessions = (DatabaseSessions) SessionManagerFactoryFactory.getManager(mProperties);
		
		try
		{
			sessions.install();
		}
		catch (SessionManagerException e)
		{
			try
			{
				sessions.remove();
			}
			catch (SessionManagerException e2)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e2), false);
			}
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		DatabaseRemember	remember = (DatabaseRemember) RememberManagerFactoryFactory.getManager(mProperties);
		
		try
		{
			remember.install();
		}
		catch (RememberManagerException e)
		{
			try
			{
				remember.remove();
			}
			catch (RememberManagerException e2)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e2), false);
			}
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void tearDown()
	throws Exception
	{
		DatabaseUsers users = DatabaseUsersFactory.getInstance(mDatasource);
		
		try
		{
			users.remove();
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		DatabaseSessions sessions = (DatabaseSessions) SessionManagerFactoryFactory.getManager(mProperties);
		
		try
		{
			sessions.remove();
		}
		catch (SessionManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		DatabaseRemember remember = (DatabaseRemember) RememberManagerFactoryFactory.getManager(mProperties);
		
		try
		{
			remember.remove();
		}
		catch (RememberManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		super.tearDown();
	}

	public void testDatabaseAuthenticatedBasic()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();

		assertEquals(1, response.getForms().length);
	}

	public void testDatabaseAuthenticatedEncrypted()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guestencrypted");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevinencrypted");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(1, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(1, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();

		assertEquals(1, response.getForms().length);
	}

	public void testDatabaseAuthenticatedRole()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		
		assertEquals(1, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/role2");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "johndoe");
		form.setParameter("password", "thepassofbass");
		response = form.submit();

		assertEquals(1, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/role2");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "johndoe");
		form.setParameter("password", "thepassofbass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();

		assertEquals(1, response.getForms().length);
	}

	public void testDatabaseAuthenticatedRoleIsolation()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		String auth_id = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/role");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);
		
		assertEquals(auth_id, response.getTitle());
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/role2");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);
		
		assertEquals(auth_id, response.getTitle());
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/role2");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "johndoe");
		form.setParameter("password", "thepassofbass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/role2");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);
		
		assertEquals(auth_id, response.getTitle());
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/role");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);
		
		assertEquals(1, response.getForms().length);
	}

	public void testDatabaseAuthenticatedRemember()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		// verify if regular authentication still works, without flagging the remember checkbox
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		assertNull(conversation.getCookieValue("authid"));
		assertNull(conversation.getCookieValue("rememberid"));

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();
		assertNull(conversation.getCookieValue("authid"));
		assertNull(conversation.getCookieValue("rememberid"));

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();
		assertNull(conversation.getCookieValue("authid"));
		assertNull(conversation.getCookieValue("rememberid"));
		
		assertEquals(1, response.getForms().length);

		// try the remember feature
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);

		assertEquals(1, response.getForms().length);

		// indicate that the authentication should be remembered
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		form.setCheckbox("remember", true);
		response = form.submit();
		assertNull(conversation.getCookieValue("authid"));
		assertNotNull(conversation.getCookieValue("rememberid"));
		String authid1 = response.getTitle();
		String rememberid1 = conversation.getCookieValue("rememberid");
		assertEquals(0, response.getForms().length);

		// check that the remember cookie works
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		assertNotNull(conversation.getCookieValue("rememberid"));
		String authid2 = response.getTitle();
		String rememberid2 = conversation.getCookieValue("rememberid");
		assertEquals(0, response.getForms().length);
		
		// ensure that a new one is assigned everytime one is used
		assertFalse(authid1.equals(authid2));
		assertFalse(rememberid1.equals(rememberid2));
		
		// check that the remember cookie isn't replaced when the user is already authenticated
		response = response.getLinkWith("reload").click();
		assertNotNull(conversation.getCookieValue("rememberid"));
		String authid3 = response.getTitle();
		String rememberid3 = conversation.getCookieValue("rememberid");
		assertEquals(0, response.getForms().length);
		
		assertTrue(authid2.equals(authid3));
		assertTrue(rememberid2.equals(rememberid3));
		
		// check if the new remember id still works
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		String rememberid4 = conversation.getCookieValue("rememberid");

		// start a new web conversation and check that a rememberid is invalidated after each use
		conversation = new WebConversation();
		conversation.addCookie("rememberid", rememberid1);
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
		
		conversation = new WebConversation();
		conversation.addCookie("rememberid", rememberid2);
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
		
		conversation = new WebConversation();
		conversation.addCookie("rememberid", rememberid3);
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
		
		conversation = new WebConversation();
		conversation.addCookie("rememberid", rememberid4);
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
	}

	public void testDatabaseAuthenticatedProhibitRemember()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		// indicate that the authentication should be remembered
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		form.setCheckbox("remember", true);
		response = form.submit();
		assertNull(conversation.getCookieValue("authid"));
		assertNotNull(conversation.getCookieValue("rememberid"));
		assertEquals(0, response.getForms().length);

		// check that the remember cookie works
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		assertNotNull(conversation.getCookieValue("rememberid"));
		String authid2 = response.getTitle();
		assertEquals(0, response.getForms().length);
		
		// check that the authid works when remembered authentication is allowed
		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember?authid="+authid2);
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		
		// check that the remember cookie is prohibited
		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/prohibitremember?authid="+authid2);
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}
	
	/**
	 * Tests that we can get through an authenticated element that has its
	 * "enforce_authenticated" flag set to false.
	 */
	public void testDatabaseAuthenticatedNotEnforced()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/authentication_database.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse	response;
		MockRequest		request;
		MockForm		form;
		ParsedHtml		parsed;
		String			auth_id;
		
		/* Can we get through the page as an anonymous user? */
		response = conversation.doRequest("/authentication/database/enforce");
		parsed = response.getParsedHtml();
		
		assertEquals("forms", 0, parsed.getForms().size());
		assertEquals("login name", "(none)", parsed.getLinkWithId("userLogin").getText());
		
		/* Now hit a protected page so we can get an authid value. */
		response = conversation.doRequest("/authentication/database/enforce2");
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
		response = conversation.doRequest("/authentication/database/enforce", request);
		parsed = response.getParsedHtml();
		
		assertEquals("forms (unenforced after login)", 0, parsed.getForms().size());
		assertEquals("login name (unenforced after login)", "guest", parsed.getLinkWithId("userLogin").getText());
	}
}

