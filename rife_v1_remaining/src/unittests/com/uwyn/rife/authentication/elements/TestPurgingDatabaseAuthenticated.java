/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestPurgingDatabaseAuthenticated.java 3918 2008-04-14 17:35:35Z gbevin $
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
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.StringEncryptor;

public class TestPurgingDatabaseAuthenticated extends TestsuiteDatabaseAuthenticated
{
	private Datasource	mDatasource = null;
	
	public TestPurgingDatabaseAuthenticated(String datasourceName, int siteType, String name)
	{
		super(datasourceName, siteType, name);
		
		mDatasource = Datasources.getRepInstance().getDatasource(datasourceName);
		mProperties = new HierarchicalProperties();
		mProperties.put("datasource", mDatasource);
		mProperties.put(SessionManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, DatabaseSessionsFactory.class.getName());
		mProperties.put(RememberManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, DatabaseRememberFactory.class.getName());
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

		DatabaseRemember remember = (DatabaseRemember) RememberManagerFactoryFactory.getManager(mProperties);
		
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

	public void testPurgingDatabaseAuthenticatedBasic()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		String auth_id;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();
		
		assertEquals(1, SessionManagerFactoryFactory.getManager(mProperties).countSessions());
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();

		assertEquals(2, SessionManagerFactoryFactory.getManager(mProperties).countSessions());
		
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/basic");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(1, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);

		assertEquals(1, SessionManagerFactoryFactory.getManager(mProperties).countSessions());
	}

	public void testPurgingDatabaseAuthenticatedEncrypted()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guestencrypted");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevinencrypted");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(1, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(1, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();

		assertEquals(1, response.getForms().length);
	}

	public void testPurgingDatabaseAuthenticatedRemember()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		// try the remember feature
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/remember");
		response = conversation.getResponse(request);

		assertEquals(1, response.getForms().length);

		// indicate that the authentication should be remembered
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/remember");
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
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/remember");
		response = conversation.getResponse(request);
		assertNotNull(conversation.getCookieValue("rememberid"));
		String rememberid1 = conversation.getCookieValue("rememberid");
		assertEquals(0, response.getForms().length);
		
		// wait a while
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// create a new remember id
		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/remember");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		form.setCheckbox("remember", true);
		response = form.submit();
		String rememberid3 = conversation.getCookieValue("rememberid");
		assertEquals(0, response.getForms().length);
		
		// check that the previous remember id has been purged
		conversation = new WebConversation();
		conversation.addCookie("rememberid", rememberid1);
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/remember");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
		
		// check that the new remember id has not been purged
		conversation = new WebConversation();
		conversation.addCookie("rememberid", rememberid3);
		request = new GetMethodWebRequest("http://localhost:8181/authentication/purging/database/remember");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
	}
}

