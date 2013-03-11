/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestXml2MemoryUsers.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import com.uwyn.rife.authentication.credentials.RoleUser;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.ExceptionUtils;
import junit.framework.TestCase;

public class TestXml2MemoryUsers extends TestCase
{
	public TestXml2MemoryUsers(String name)
	{
		super(name);
	}
	
	public void testInstantiation()
	{
		Xml2MemoryUsers xml2users = new Xml2MemoryUsers();
		
		assertNotNull(xml2users);
	}
	
	public void testParse()
	{
		MemoryUsers users = null;
		
		try
		{
			users = new MemoryUsers("xml/test_xml2users.xml", ResourceFinderClasspath.getInstance());
	
			assertEquals(4, users.countUsers());
			
			assertTrue(!users.containsUser("login"));
			assertTrue(users.containsUser("the_first_login"));
			assertTrue(users.containsUser("the_second_login"));
			assertTrue(users.containsUser("the_third_login"));
			assertTrue(users.containsUser("the_fourth_login"));
	
			assertTrue(-1 == users.verifyCredentials(new RoleUser("login", "thepassword")));
			
			assertTrue(0 == users.verifyCredentials(new RoleUser("the_first_login", "a password")));
			
			assertTrue(17 == users.verifyCredentials(new RoleUser("the_second_login", "another password")));
			assertTrue(17 == users.verifyCredentials(new RoleUser("the_second_login", "another password", "role1")));
			assertTrue(17 == users.verifyCredentials(new RoleUser("the_second_login", "another password", "role2")));
			
			assertTrue(98 == users.verifyCredentials(new RoleUser("the_third_login", "a third password")));
			
			assertTrue(1 == users.verifyCredentials(new RoleUser("the_fourth_login", "the last password")));
			assertTrue(1 == users.verifyCredentials(new RoleUser("the_fourth_login", "the last password", "role3")));
			assertTrue(1 == users.verifyCredentials(new RoleUser("the_fourth_login", "the last password", "role2")));
			assertTrue(1 == users.verifyCredentials(new RoleUser("the_fourth_login", "the last password", "role4")));
			
			assertEquals("the_first_login", users.getLogin(0));
			assertEquals("the_second_login", users.getLogin(17));
			assertEquals("the_third_login", users.getLogin(98));
			assertEquals("the_fourth_login", users.getLogin(1));
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testUnavailableXmlFile()
	{
		MemoryUsers users = null;
		
		try
		{
			users = new MemoryUsers("xml/this_file_is_not_there.xml", ResourceFinderClasspath.getInstance());
			fail();
			assertNotNull(users);
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(true);
		}
	}
}
