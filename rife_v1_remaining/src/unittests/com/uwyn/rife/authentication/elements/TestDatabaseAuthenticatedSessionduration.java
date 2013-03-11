/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseAuthenticatedSessionduration.java 3918 2008-04-14 17:35:35Z gbevin $
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
import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.authentication.sessionmanagers.DatabaseSessions;
import com.uwyn.rife.authentication.sessionmanagers.DatabaseSessionsFactory;
import com.uwyn.rife.authentication.sessionmanagers.SessionManagerFactoryFactory;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.tools.ExceptionUtils;

public class TestDatabaseAuthenticatedSessionduration extends TestsuiteDatabaseAuthenticated
{
	private Datasource	mDatasource = null;
	
	public TestDatabaseAuthenticatedSessionduration(String datasourceName, int siteType, String name)
	{
		super(datasourceName, siteType, name);
		
		mDatasource = Datasources.getRepInstance().getDatasource("sessionduration"+datasourceName);
		mProperties.put("datasource", mDatasource);
		mProperties.put(SessionManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, DatabaseSessionsFactory.class.getName());
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

		super.tearDown();
	}

	public void testDatabaseAuthenticatedSessionduration()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		String auth_id;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/sessionduration");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/sessionduration");
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
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/database/sessionduration");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(1, response.getForms().length);
	 }
}

