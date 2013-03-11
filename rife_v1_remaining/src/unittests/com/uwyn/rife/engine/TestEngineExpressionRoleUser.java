/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineExpressionRoleUser.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.authentication.credentialsmanagers.DatabaseUsers;
import com.uwyn.rife.authentication.credentialsmanagers.DatabaseUsersFactory;
import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.authentication.sessionmanagers.DatabaseSessions;
import com.uwyn.rife.authentication.sessionmanagers.DatabaseSessionsFactory;
import com.uwyn.rife.authentication.sessionmanagers.SessionManagerFactoryFactory;
import com.uwyn.rife.config.Config;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.StringEncryptor;
import com.uwyn.rife.tools.StringUtils;

public class TestEngineExpressionRoleUser extends TestCaseServerside
{
	private Datasource				mDatasource = null;
	private HierarchicalProperties	mProperties = null;
	
	public TestEngineExpressionRoleUser(int siteType, String name)
	{
		super(siteType, name);
	}
	
	
	public void setUp()
	throws Exception
	{
		super.setUp();
		
		Config.getRepInstance().setParameter("unittestsdatasource", "unittestsderby");

		mDatasource = Datasources.getRepInstance().getDatasource("unittestsderby");
		mProperties = new HierarchicalProperties();
		mProperties.put("datasource", mDatasource);
		mProperties.put(SessionManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, DatabaseSessionsFactory.class.getName());

		DatabaseUsers	users = DatabaseUsersFactory.getInstance(mDatasource);
		try
		{
			users.install();
			
			users.addRole("admin");
			users.addRole("maint");
			 
			users.addUser("guest", new RoleUserAttributes(43, "guestpass"));
			users.addUser("gbevin", new RoleUserAttributes(1, "yeolpass", new String[] {"admin", "maint"}));
			users.addUser("johndoe", new RoleUserAttributes(174, "thepassofbass", new String[] {"maint"}));
			users.setPasswordEncryptor(StringEncryptor.SHA);
			users.addUser("guestencrypted", new RoleUserAttributes(44, "guestpass"));
			users.addUser("gbevinencrypted", new RoleUserAttributes(3, "yeolpass", new String[] {"admin", "maint"}));
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

		DatabaseSessions	sessions = (DatabaseSessions) SessionManagerFactoryFactory.getManager(mProperties);
		
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

		DatabaseSessions	sessions = (DatabaseSessions)SessionManagerFactoryFactory.getManager(mProperties);
		
		try
		{
			sessions.remove();
		}
		catch (SessionManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		Config.getRepInstance().removeParameter("unittestsdatasource");

		super.tearDown();
	}

	public void testMemoryRoleAdminOgnl()
	throws Exception
	{
		setupSite("site/roleuser_ognl.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testMemoryRoleMaintOgnl()
	throws Exception
	{
		setupSite("site/roleuser_ognl.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_maint?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_maint?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_maint?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testIdentifiedOgnl()
	throws Exception
	{
		setupSite("site/roleuser_ognl.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;
		String authid = null;

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		authid = lines[8];
		request = new GetMethodWebRequest("http://localhost:8181/identified?authid="+authid);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		authid = lines[8];
		request = new GetMethodWebRequest("http://localhost:8181/identified?authid="+authid);
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/identified?authid=blargh");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);
	}

	public void testExplicitEvaluationOgnl()
	throws Exception
	{
		setupSite("site/roleuser_ognl.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=login");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=password1");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=password2");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=role1&roleadmin=anotheradmin");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=role1&roleadmin=admin");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);
	}

	public void testDatabaseRoleAdminOgnl()
	throws Exception
	{
		setupSite("site/roleuser_ognl.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/encrypted?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testDatabaseRoleMaintOgnl()
	throws Exception
	{
		setupSite("site/roleuser_ognl.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/database/role_maint?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/encrypted?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_maint?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testMemoryRoleAdminMvel()
	throws Exception
	{
		setupSite("site/roleuser_mvel.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testMemoryRoleMaintMvel()
	throws Exception
	{
		setupSite("site/roleuser_mvel.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_maint?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_maint?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_maint?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testIdentifiedMvel()
	throws Exception
	{
		setupSite("site/roleuser_mvel.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;
		String authid = null;

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		authid = lines[8];
		request = new GetMethodWebRequest("http://localhost:8181/identified?authid="+authid);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		authid = lines[8];
		request = new GetMethodWebRequest("http://localhost:8181/identified?authid="+authid);
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/identified?authid=blargh");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);
	}

	public void testExplicitEvaluationMvel()
	throws Exception
	{
		setupSite("site/roleuser_mvel.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=login");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=password1");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=password2");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=role1&roleadmin=anotheradmin");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=role1&roleadmin=admin");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);
	}

	public void testDatabaseRoleAdminMvel()
	throws Exception
	{
		setupSite("site/roleuser_mvel.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/encrypted?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testDatabaseRoleMaintMvel()
	throws Exception
	{
		setupSite("site/roleuser_mvel.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/database/role_maint?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/encrypted?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_maint?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testMemoryRoleAdminGroovy()
	throws Exception
	{
		setupSite("site/roleuser_groovy.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testMemoryRoleMaintGroovy()
	throws Exception
	{
		setupSite("site/roleuser_groovy.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_maint?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_maint?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_maint?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testIdentifiedGroovy()
	throws Exception
	{
		setupSite("site/roleuser_groovy.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;
		String authid = null;

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		authid = lines[8];
		request = new GetMethodWebRequest("http://localhost:8181/identified?authid="+authid);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		authid = lines[8];
		request = new GetMethodWebRequest("http://localhost:8181/identified?authid="+authid);
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/identified?authid=blargh");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);
	}

	public void testExplicitEvaluationGroovy()
	throws Exception
	{
		setupSite("site/roleuser_groovy.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=login");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=password1");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=password2");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=role1&roleadmin=anotheradmin");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=role1&roleadmin=admin");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);
	}

	public void testDatabaseRoleAdminGroovy()
	throws Exception
	{
		setupSite("site/roleuser_groovy.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/encrypted?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testDatabaseRoleMaintGroovy()
	throws Exception
	{
		setupSite("site/roleuser_groovy.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/database/role_maint?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/encrypted?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_maint?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testMemoryRoleAdminJanino()
	throws Exception
	{
		setupSite("site/roleuser_janino.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testMemoryRoleMaintJanino()
	throws Exception
	{
		setupSite("site/roleuser_janino.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_maint?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_maint?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_maint?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testIdentifiedJanino()
	throws Exception
	{
		setupSite("site/roleuser_janino.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;
		String authid = null;

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		authid = lines[8];
		request = new GetMethodWebRequest("http://localhost:8181/identified?authid="+authid);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/memory/role_admin?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		authid = lines[8];
		request = new GetMethodWebRequest("http://localhost:8181/identified?authid="+authid);
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/identified?authid=blargh");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);
	}

	public void testExplicitEvaluationJanino()
	throws Exception
	{
		setupSite("site/roleuser_janino.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=login");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=password1");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=password2");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=role1&roleadmin=anotheradmin");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_evaluation?submission=credentials&login=gbevin&password=yeolpass&evaluate=role1&roleadmin=admin");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("not matching role2", lines[4]);
		assertEquals("not matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);
	}

	public void testDatabaseRoleAdminJanino()
	throws Exception
	{
		setupSite("site/roleuser_janino.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/encrypted?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}

	public void testDatabaseRoleMaintJanino()
	throws Exception
	{
		setupSite("site/roleuser_janino.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/database/role_maint?submission=credentials&login=gbevin&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("matching login", lines[0]);
		assertEquals("matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/encrypted?submission=credentials&login=gbevinencrypted&password=yeolpass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("matching password2", lines[2]);
		assertEquals("matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_maint?submission=credentials&login=johndoe&password=thepassofbass");
		response = conversation.getResponse(request);
		assertEquals(0, response.getForms().length);
		lines = StringUtils.splitToArray(response.getText(), "\n");
		assertEquals("not matching login", lines[0]);
		assertEquals("not matching password1", lines[1]);
		assertEquals("not matching password2", lines[2]);
		assertEquals("not matching role1", lines[3]);
		assertEquals("matching role2", lines[4]);
		assertEquals("matching role3", lines[5]);
		assertEquals("not matching userid1", lines[6]);
		assertEquals("not matching userid2", lines[7]);

		request = new GetMethodWebRequest("http://localhost:8181/database/role_admin?submission=credentials&login=guest&password=guestpass");
		response = conversation.getResponse(request);
		assertEquals(1, response.getForms().length);
	}
}

