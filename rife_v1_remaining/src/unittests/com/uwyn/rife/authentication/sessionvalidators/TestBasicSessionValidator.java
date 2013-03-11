/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSimpleSessionValidator.java 3308 2006-06-15 18:54:14Z gbevin $
 */
package com.uwyn.rife.authentication.sessionvalidators;

import com.uwyn.rife.authentication.SessionAttributes;
import com.uwyn.rife.authentication.credentialsmanagers.MemoryUsers;
import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.authentication.exceptions.SessionValidatorException;
import com.uwyn.rife.authentication.sessionmanagers.MemorySessions;
import com.uwyn.rife.authentication.sessionmanagers.SimpleSessionManagerFactory;
import com.uwyn.rife.authentication.sessionvalidators.BasicSessionValidator;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.tools.ExceptionUtils;
import junit.framework.TestCase;

public class TestBasicSessionValidator extends TestCase
{
	private HierarchicalProperties	mProperties = null;
	
	public TestBasicSessionValidator(String name)
	{
		super(name);
		mProperties = new HierarchicalProperties();
		mProperties.put(SimpleSessionManagerFactory.PROPERTYNAME_MANAGER_CLASS, MemorySessions.class.getName());
	}

	public void testInstantiation()
	{
		AbstractSessionValidator	validator = null;
		
		validator = new BasicSessionValidator();
		
		assertNotNull(validator);
	}

	public void testValidSessionId()
	{
		BasicSessionValidator	validator = new BasicSessionValidator();
		
		assertTrue(validator.isAccessAuthorized(1));
	}
	
	public void testSessionValidity()
	{
		BasicSessionValidator	validator = new BasicSessionValidator();
		MemorySessions			sessions = (MemorySessions) new SimpleSessionManagerFactory().getManager(mProperties);
		sessions.setSessionDuration(120000);
		validator.setSessionManager(sessions);

		int		user_id = 9478;
		String	host_ip = "98.232.12.456";
		
		String	auth_id = null;
		try
		{
			auth_id = sessions.startSession(user_id, host_ip, false);
			assertTrue(validator.isAccessAuthorized(validator.validateSession(auth_id, host_ip, new DummyAttributes())));
			sessions.setRestrictHostIp(true);
			assertEquals(AbstractSessionValidator.SESSION_INVALID, validator.validateSession(auth_id, "1.1.1.1", new DummyAttributes()));
			sessions.setRestrictHostIp(false);
			assertEquals(AbstractSessionValidator.SESSION_VALID, validator.validateSession(auth_id, "1.1.1.1", new DummyAttributes()));
			assertEquals(AbstractSessionValidator.SESSION_INVALID, validator.validateSession("not_valid", host_ip, new DummyAttributes()));
			
			sessions.setSessionDuration(0);
			
			Thread.sleep(2);
			assertEquals(AbstractSessionValidator.SESSION_INVALID, validator.validateSession(auth_id, host_ip, new DummyAttributes()));
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
	}
	
	public void testSessionValidityRole()
	{
		BasicSessionValidator	validator = new BasicSessionValidator();
		MemoryUsers				users = new MemoryUsers();
		MemorySessions			sessions = (MemorySessions) new SimpleSessionManagerFactory().getManager(mProperties);
		sessions.setSessionDuration(120000);
		validator.setSessionManager(sessions);
		validator.setCredentialsManager(users);

		String	host_ip = "98.232.12.456";
		
		String	auth_id1 = null;
		String	auth_id2 = null;
		String	auth_id3 = null;
		try
		{
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
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (SessionValidatorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
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
