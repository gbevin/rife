/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseSessionValidator.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionvalidators;

import com.uwyn.rife.authentication.SessionAttributes;
import com.uwyn.rife.authentication.SessionValidator;
import com.uwyn.rife.authentication.credentialsmanagers.DatabaseUsers;
import com.uwyn.rife.authentication.credentialsmanagers.DatabaseUsersFactory;
import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.authentication.exceptions.SessionValidatorException;
import com.uwyn.rife.authentication.sessionmanagers.DatabaseSessions;
import com.uwyn.rife.authentication.sessionmanagers.DatabaseSessionsFactory;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.tools.ExceptionUtils;
import junit.framework.TestCase;

public class TestDatabaseSessionValidator extends TestCase
{
	private Datasource				mDatasource = null;
	private HierarchicalProperties	mProperties = null;
    
	public TestDatabaseSessionValidator(Datasource datasource, String datasourceName, String name)
	{
		super(name);
		mDatasource = datasource;
		mProperties = new HierarchicalProperties();
		mProperties.put("datasource", mDatasource);
	}

	public void testInstantiation()
	{
		SessionValidator validator = new DatabaseSessionValidatorFactory().getValidator(mProperties);
		assertNotNull(validator);
		assertTrue(validator instanceof DatabaseSessionValidator);
	}

	public void testValidSessionId()
	{
		SessionValidator validator = new DatabaseSessionValidatorFactory().getValidator(mProperties);

		assertTrue(validator.isAccessAuthorized(1));
	}

	public void testSessionValidity()
	{
		SessionValidator	validator = new DatabaseSessionValidatorFactory().getValidator(mProperties);
		DatabaseSessions	sessions = new DatabaseSessionsFactory().getManager(mProperties);
		sessions.setSessionDuration(120000);
		validator.setSessionManager(sessions);

		int		user_id = 9478;
		String	host_ip = "98.232.12.456";

		String	auth_id = null;
		try
		{
			sessions.install();

			auth_id = sessions.startSession(user_id, host_ip, false);
			assertTrue(validator.isAccessAuthorized(validator.validateSession(auth_id, host_ip, new DummyAttributes())));
			sessions.setRestrictHostIp(true);
			assertEquals(DatabaseSessionValidator.SESSION_INVALID, validator.validateSession(auth_id, "1.1.1.1", new DummyAttributes()));
			sessions.setRestrictHostIp(false);
			assertEquals(DatabaseSessionValidator.SESSION_VALID, validator.validateSession(auth_id, "1.1.1.1", new DummyAttributes()));
			assertEquals(DatabaseSessionValidator.SESSION_INVALID, validator.validateSession("not_valid", host_ip, new DummyAttributes()));

			sessions.setSessionDuration(0);

			Thread.sleep(2);
			assertEquals(DatabaseSessionValidator.SESSION_INVALID, validator.validateSession(auth_id, host_ip, new DummyAttributes()));
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (SessionManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (SessionValidatorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			try
			{
				sessions.remove();
			}
			catch (SessionManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}

	public void testSessionValidityRole()
	{
		SessionValidator	validator = new DatabaseSessionValidatorFactory().getValidator(mProperties);
		DatabaseUsers		users = DatabaseUsersFactory.getInstance(mDatasource);
		DatabaseSessions	sessions = new DatabaseSessionsFactory().getManager(mProperties);
		sessions.setSessionDuration(120000);
		validator.setSessionManager(sessions);

		String	host_ip = "98.232.12.456";
		
		String	auth_id1 = null;
		String	auth_id2 = null;
		String	auth_id3 = null;
		try
		{
			users.install();
			sessions.install();
			
			users.addRole("admin");
			users.addRole("maint");

			users.addUser("login1", new RoleUserAttributes(1, "thepassword", new String[] {"admin", "maint"}));
			users.addUser("login2", new RoleUserAttributes(2, "thepassword", new String[] {"maint"}));
			users.addUser("login3", new RoleUserAttributes(3, "thepassword"));
			
			auth_id1 = sessions.startSession(1, host_ip, false);
			auth_id2 = sessions.startSession(2, host_ip, false);
			auth_id3 = sessions.startSession(3, host_ip, false);
			
			assertTrue(validator.isAccessAuthorized(validator.validateSession(auth_id1, host_ip, new DummyAttributes())));
			assertTrue(validator.isAccessAuthorized(validator.validateSession(auth_id1, host_ip, new RoleAdminAttributes())));
			assertTrue(validator.isAccessAuthorized(validator.validateSession(auth_id1, host_ip, new RoleMaintAttributes())));
			
			assertTrue(validator.isAccessAuthorized(validator.validateSession(auth_id2, host_ip, new DummyAttributes())));
			assertEquals(AbstractSessionValidator.SESSION_INVALID, validator.validateSession(auth_id2, host_ip, new RoleAdminAttributes()));
			assertTrue(validator.isAccessAuthorized(validator.validateSession(auth_id2, host_ip, new RoleMaintAttributes())));
			
			assertTrue(validator.isAccessAuthorized(validator.validateSession(auth_id3, host_ip, new DummyAttributes())));
			assertEquals(AbstractSessionValidator.SESSION_INVALID, validator.validateSession(auth_id3, host_ip, new RoleAdminAttributes()));
			assertEquals(AbstractSessionValidator.SESSION_INVALID, validator.validateSession(auth_id3, host_ip, new RoleMaintAttributes()));
		}
		catch (SessionManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (CredentialsManagerException e)
		{
			e.printStackTrace();
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (SessionValidatorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			try
			{
				sessions.remove();
			}
			catch (SessionManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
			try
			{
				users.remove();
			}
			catch (CredentialsManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}
	
	class DummyAttributes implements SessionAttributes
	{
		public boolean hasAttribute(String key)
		{
			return false;
		}
		
		public String getAttribute(String key)
		{
			return null;
		}
	}
	
	class RoleMaintAttributes implements SessionAttributes
	{
		public boolean hasAttribute(String key)
		{
			if (key.equals("role"))
			{
				return true;
			}
			
			return false;
		}
		
		public String getAttribute(String key)
		{
			if (key.equals("role"))
			{
				return "maint";
			}
			
			return null;
		}
	}
	
	class RoleAdminAttributes implements SessionAttributes
	{
		public boolean hasAttribute(String key)
		{
			if (key.equals("role"))
			{
				return true;
			}
			
			return false;
		}
		
		public String getAttribute(String key)
		{
			if (key.equals("role"))
			{
				return "admin";
			}
			
			return null;
		}
	}
}
