/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestMixedAuthenticated.java 3918 2008-04-14 17:35:35Z gbevin $
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
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.StringEncryptor;

public class TestMixedAuthenticated extends TestsuiteDatabaseAuthenticated
{
	private Datasource	mDatasource = null;
	
	public TestMixedAuthenticated(String datasourceName, int siteType, String name)
	{
		super(datasourceName, siteType, name);
		
		mDatasource = Datasources.getRepInstance().getDatasource(datasourceName);
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

		super.tearDown();
	}

	public void testMixedAuthenticatedBasic()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();

		assertEquals(1, response.getForms().length);
	}

	public void testMixedAuthenticatedEncrypted()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guestencrypted");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevinencrypted");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(1, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(1, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/encrypted");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();

		assertEquals(1, response.getForms().length);
	}

	public void testMixedAuthenticatedOtherid()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		String auth_id;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/basic");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/basic");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(0, response.getForms().length);
		assertEquals(auth_id, response.getTitle());
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/otherid");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(1, response.getForms().length);
		assertTrue(!auth_id.equals(response.getTitle()));
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/otherid");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/otherid");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(0, response.getForms().length);
		assertEquals(auth_id, response.getTitle());
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/basic");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(1, response.getForms().length);
		assertTrue(!auth_id.equals(response.getTitle()));
	}

	public void testMixedAuthenticatedRole()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();
		
		assertEquals(1, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/role2");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "johndoe");
		form.setParameter("password", "thepassofbass");
		response = form.submit();

		assertEquals(1, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/role2");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "johndoe");
		form.setParameter("password", "thepassofbass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "invalid");
		form.setParameter("password", "invalid");
		response = form.submit();

		assertEquals(1, response.getForms().length);
	}

	public void testMixedAuthenticatedRoleIsolation()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		String auth_id = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/role");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "yeolpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/role");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);
		
		assertEquals(auth_id, response.getTitle());
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/role2");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);
		
		assertEquals(auth_id, response.getTitle());
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/role2");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "johndoe");
		form.setParameter("password", "thepassofbass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/role2");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);
		
		assertEquals(auth_id, response.getTitle());
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/role");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);
		
		assertEquals(1, response.getForms().length);
	 }

	public void testMixedAuthenticatedSessionduration()
	throws Exception
	{
		setupSite("site/authentication_database.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		String auth_id;
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/sessionduration");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "guest");
		form.setParameter("password", "guestpass");
		response = form.submit();

		assertEquals(0, response.getForms().length);
		auth_id = response.getTitle();
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/sessionduration");
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
		
		request = new GetMethodWebRequest("http://localhost:8181/authentication/mixed/sessionduration");
		request.setParameter("authid", auth_id);
		response = conversation.getResponse(request);

		assertEquals(1, response.getForms().length);
	 }
}

