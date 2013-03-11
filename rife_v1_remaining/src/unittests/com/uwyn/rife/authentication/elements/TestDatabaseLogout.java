/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseLogout.java 3918 2008-04-14 17:35:35Z gbevin $
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
import com.uwyn.rife.authentication.remembermanagers.DatabaseRememberFactory;
import com.uwyn.rife.authentication.remembermanagers.RememberManagerFactoryFactory;
import com.uwyn.rife.authentication.sessionmanagers.DatabaseSessions;
import com.uwyn.rife.authentication.sessionmanagers.DatabaseSessionsFactory;
import com.uwyn.rife.authentication.sessionmanagers.SessionManagerFactoryFactory;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.tools.ExceptionUtils;

public class TestDatabaseLogout extends TestsuiteDatabaseAuthenticated
{
	private Datasource	mDatasource = null;
	
	public TestDatabaseLogout(String datasourceName, int siteType, String name)
	{
		super(datasourceName, siteType, name);
		
		mDatasource = Datasources.getRepInstance().getDatasource(datasourceName);
		mProperties.put("datasource", mDatasource);
		mProperties.put(SessionManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, DatabaseSessionsFactory.class.getName());
		mProperties.put(RememberManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, DatabaseRememberFactory.class.getName());
	}
	
	public void setUp()
	throws Exception
	{
		super.setUp();
		
		DatabaseUsers		users = DatabaseUsersFactory.getInstance(mDatasource);
		try
		{
			users.install();
			
			users.addRole("admin");
			users.addRole("maint");
			 
			users.addUser("guest", new RoleUserAttributes(43, "guestpass"));
			users.addUser("gbevin", new RoleUserAttributes(432, "yeolpass", new String[] {"admin", "maint"}));
			users.addUser("johndoe", new RoleUserAttributes(174, "thepassofbass", new String[] {"maint"}));
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

		DatabaseRemember	remember = (DatabaseRemember) RememberManagerFactoryFactory.getManager(mProperties);
		
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

	public void testDatabaseLogoutTemplateBasic()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm 	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();
		assertEquals(0, response.getForms().length);

		response = response.getLinkWith("reload").click();
		assertEquals(0, response.getForms().length);

		WebResponse	response_logout = response.getLinkWith("logout template").click();
		assertEquals("logged out database", response_logout.getTitle());
		
		response = response.getLinkWith("reload").click();
		assertEquals(1, response.getForms().length);
	}

	public void testDatabaseLogoutPassthroughBasic()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm 	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/basic");
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

	public void testDatabaseLogoutPrecedence()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm 	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/precedence");
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

	public void testDatabaseLogoutTemplateRemember()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm 	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		form.setCheckbox("remember", true);
		assertNull(conversation.getCookieValue("rememberid"));
		response = form.submit();
		assertEquals(0, response.getForms().length);
		assertNotNull(conversation.getCookieValue("rememberid"));
		String rememberid = conversation.getCookieValue("rememberid");

		response = response.getLinkWith("reload").click();
		assertEquals(0, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);

		WebResponse	response_logout = response.getLinkWith("logout template").click();
		assertEquals("logged out database", response_logout.getTitle());
		assertEquals("", conversation.getCookieValue("rememberid"));
		
		response = response.getLinkWith("reload").click();
		assertEquals(1, response.getForms().length);

		// ensure that the rememberid has also been erased from the backend
		conversation.addCookie("rememberid", rememberid);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testDatabaseLogoutPassthroughRemember()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm 	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		form.setCheckbox("remember", true);
		assertNull(conversation.getCookieValue("rememberid"));
		response = form.submit();
		assertEquals(0, response.getForms().length);
		assertNotNull(conversation.getCookieValue("rememberid"));
		String rememberid = conversation.getCookieValue("rememberid");

		response = response.getLinkWith("reload").click();
		assertEquals(0, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);

		response = response.getLinkWith("logout passthrough").click();
		assertEquals(1, response.getForms().length);
		assertEquals("", conversation.getCookieValue("rememberid"));

		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);

		// ensure that the rememberid has also been erased from the backend
		conversation.addCookie("rememberid", rememberid);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/remember");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}
}

