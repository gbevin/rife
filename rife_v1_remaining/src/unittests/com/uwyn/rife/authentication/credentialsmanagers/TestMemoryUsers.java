/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestMemoryUsers.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import com.uwyn.rife.authentication.credentials.RoleUser;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.DuplicateLoginException;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.DuplicateRoleException;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.DuplicateUserIdException;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.StringEncryptor;
import java.io.File;
import java.util.ArrayList;
import junit.framework.TestCase;

public class TestMemoryUsers extends TestCase
{
	public TestMemoryUsers(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		MemoryUsers users = null;
		
		users = new MemoryUsers();
		
		assertNotNull(users);
	}

	public void testNoInitialUsers()
	{
		MemoryUsers users = new MemoryUsers();
		
		assertEquals(0, users.countUsers());
	}

	public void testAddRoles()
	{
		MemoryUsers users = new MemoryUsers();

		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3");
			try
			{
				users.addRole("role2");
				fail();
			}
			catch (DuplicateRoleException e)
			{
				assertEquals("role2", e.getRole());
			}
	
			assertEquals(3, users.countRoles());
	
			assertTrue(users.containsRole("role1"));
			assertTrue(users.containsRole("role2"));
			assertTrue(users.containsRole("role3"));
			assertTrue(!users.containsRole("role4"));
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testAddUsers()
	{
		MemoryUsers users = new MemoryUsers();
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3");

			RoleUserAttributes user1_attributes = new RoleUserAttributes("thepassword");
			users.addUser("login1", user1_attributes);
			assertEquals(0, user1_attributes.getUserId());
			RoleUserAttributes user2_attributes = new RoleUserAttributes("thepassword2", new String[] {"role1", "role2"});
			users.addUser("login2", user2_attributes);
			assertEquals(1, user2_attributes.getUserId());
			RoleUserAttributes user3_attributes = new RoleUserAttributes(174, "thepassword3", new String[] {"role1", "role2", "role3"});
			users.addUser("login3", user3_attributes);
			assertEquals(174, user3_attributes.getUserId());
			RoleUserAttributes user4_attributes = new RoleUserAttributes("thepassword4", new String[] {"role2", "role3"});
			users.addUser("login4", user4_attributes);
			assertEquals(2, user4_attributes.getUserId());
			try
			{
				RoleUserAttributes user5_attributes = new RoleUserAttributes("thepassword5", new String[] {"role1"});
				users.addUser("login1", user5_attributes);
				fail();
			}
			catch (DuplicateLoginException e)
			{
				assertEquals(e.getLogin(), "login1");
			}
			
			assertEquals(4, users.countUsers());
			
			assertTrue(users.containsUser("login1"));
			assertTrue(users.containsUser("login2"));
			assertTrue(users.containsUser("login3"));
			assertTrue(users.containsUser("login4"));
			assertTrue(!users.containsUser("login5"));

			assertEquals(3, users.countRoles());

			assertTrue(users.containsRole("role1"));
			assertTrue(users.containsRole("role2"));
			assertTrue(users.containsRole("role3"));
			assertTrue(!users.containsRole("role4"));

			assertEquals(users.getUserId("login1"), user1_attributes.getUserId());
			assertEquals(users.getUserId("login2"), user2_attributes.getUserId());
			assertEquals(users.getUserId("login3"), user3_attributes.getUserId());
			assertEquals(users.getUserId("login4"), user4_attributes.getUserId());

			assertEquals(users.getLogin(user1_attributes.getUserId()), "login1");
			assertEquals(users.getLogin(user2_attributes.getUserId()), "login2");
			assertEquals(users.getLogin(user3_attributes.getUserId()), "login3");
			assertEquals(users.getLogin(user4_attributes.getUserId()), "login4");
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testUpdateUsers()
	{
		MemoryUsers users = new MemoryUsers();
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3");

			RoleUserAttributes user1_attributes = new RoleUserAttributes("thepassword");
			RoleUserAttributes user2_attributes = new RoleUserAttributes("thepassword2", new String[] {"role1", "role2"});
			RoleUserAttributes user3_attributes = new RoleUserAttributes(174, "thepassword3", new String[] {"role1", "role2", "role3"});
			RoleUserAttributes user4_attributes = new RoleUserAttributes("thepassword4", new String[] {"role2", "role3"});
			users
				.addUser("login1", user1_attributes)
				.addUser("login2", user2_attributes)
				.addUser("login3", user3_attributes)
				.addUser("login4", user4_attributes);

			RoleUserAttributes user1_attributes_new = new RoleUserAttributes(4, "thepassword_new", new String[] {"role1", "role2"});
			RoleUserAttributes user2_attributes_new = new RoleUserAttributes(3, new String[] {"role2"});
			RoleUserAttributes user3_attributes_new = new RoleUserAttributes(2, new String[] {"role1"});
			RoleUserAttributes user4_attributes_new = new RoleUserAttributes(1, "thepassword_new4");
			assertTrue(users.updateUser("login1", user1_attributes_new));
			assertTrue(users.updateUser("login2", user2_attributes_new));
			assertTrue(users.updateUser("login3", user3_attributes_new));
			assertTrue(users.updateUser("login4", user4_attributes_new));

			assertEquals(4, users.countUsers());
			
			assertTrue(users.containsUser("login1"));
			assertTrue(users.containsUser("login2"));
			assertTrue(users.containsUser("login3"));
			assertTrue(users.containsUser("login4"));
			assertTrue(!users.containsUser("login5"));
			
			assertEquals(users.getAttributes("login1").getPassword(), user1_attributes_new.getPassword());
			assertEquals(users.getAttributes("login1").getUserId(), user1_attributes.getUserId());
			assertEquals(users.getAttributes("login1").getRoles().size(), user1_attributes_new.getRoles().size());
			assertTrue(users.getAttributes("login1").getRoles().contains("role1"));
			assertTrue(users.getAttributes("login1").getRoles().contains("role2"));
			assertEquals(users.getAttributes("login2").getPassword(), user2_attributes.getPassword());
			assertEquals(users.getAttributes("login2").getUserId(), user2_attributes.getUserId());
			assertEquals(users.getAttributes("login2").getRoles().size(), user2_attributes_new.getRoles().size());
			assertTrue(users.getAttributes("login2").getRoles().contains("role2"));
			assertEquals(users.getAttributes("login3").getPassword(), user3_attributes.getPassword());
			assertEquals(users.getAttributes("login3").getUserId(), user3_attributes.getUserId());
			assertEquals(users.getAttributes("login3").getRoles().size(), user3_attributes_new.getRoles().size());
			assertTrue(users.getAttributes("login3").getRoles().contains("role1"));
			assertEquals(users.getAttributes("login4").getPassword(), user4_attributes_new.getPassword());
			assertEquals(users.getAttributes("login4").getUserId(), user4_attributes.getUserId());
			assertEquals(0, users.getAttributes("login4").getRoles().size());

			assertEquals(2, users.countRoles());

			assertTrue(users.containsRole("role1"));
			assertTrue(users.containsRole("role2"));
			assertTrue(!users.containsRole("role3"));
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testListRoles()
	{
		MemoryUsers users = new MemoryUsers();
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3");

			RoleUserAttributes user1_attributes = new RoleUserAttributes("thepassword");
			RoleUserAttributes user2_attributes = new RoleUserAttributes("thepassword2", new String[] {"role1", "role2"});
			RoleUserAttributes user3_attributes = new RoleUserAttributes(174, "thepassword3", new String[] {"role1", "role2", "role3"});
			RoleUserAttributes user4_attributes = new RoleUserAttributes("thepassword4", new String[] {"role2", "role3"});
			users
				.addUser("login1", user1_attributes)
				.addUser("login2", user2_attributes)
				.addUser("login3", user3_attributes)
				.addUser("login4", user4_attributes);
			
			ListMemoryRoles listroles = null;
			
			listroles = new ListMemoryRoles();
			assertTrue(users.listRoles(listroles));
			assertEquals(3, listroles.getRoles().size());
			assertTrue(listroles.getRoles().contains("role1"));
			assertTrue(listroles.getRoles().contains("role2"));
			assertTrue(listroles.getRoles().contains("role3"));
			
			users.removeUser("login4");
			
			listroles = new ListMemoryRoles();
			assertTrue(users.listRoles(listroles));
			assertEquals(3, listroles.getRoles().size());
			assertTrue(listroles.getRoles().contains("role1"));
			assertTrue(listroles.getRoles().contains("role2"));
			assertTrue(listroles.getRoles().contains("role3"));
			
			users.removeUser("login3");
			
			listroles = new ListMemoryRoles();
			assertTrue(users.listRoles(listroles));
			assertEquals(2, listroles.getRoles().size());
			assertTrue(listroles.getRoles().contains("role1"));
			assertTrue(listroles.getRoles().contains("role2"));
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testListUsers()
	{
		MemoryUsers users = new MemoryUsers();
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3");

			RoleUserAttributes user1_attributes = new RoleUserAttributes("thepassword");
			RoleUserAttributes user2_attributes = new RoleUserAttributes("thepassword2", new String[] {"role1", "role2"});
			RoleUserAttributes user3_attributes = new RoleUserAttributes(174, "thepassword3", new String[] {"role1", "role2", "role3"});
			RoleUserAttributes user4_attributes = new RoleUserAttributes("thepassword4", new String[] {"role2", "role3"});
			users
				.addUser("login1", user1_attributes)
				.addUser("login2", user2_attributes)
				.addUser("login3", user3_attributes)
				.addUser("login4", user4_attributes);
			
			ListMemoryUsers listusers = new ListMemoryUsers();
			assertTrue(users.listUsers(listusers));
			assertEquals(4, listusers.getUsers().size());
			assertTrue(listusers.getUsers().contains("0,login1,thepassword"));
			assertTrue(listusers.getUsers().contains("1,login2,thepassword2"));
			assertTrue(listusers.getUsers().contains("174,login3,thepassword3"));
			assertTrue(listusers.getUsers().contains("2,login4,thepassword4"));
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testListUsersRanged()
	{
		MemoryUsers users = new MemoryUsers();
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3");

			RoleUserAttributes user1_attributes = new RoleUserAttributes("thepassword");
			RoleUserAttributes user2_attributes = new RoleUserAttributes("thepassword2", new String[] {"role1", "role2"});
			RoleUserAttributes user3_attributes = new RoleUserAttributes(174, "thepassword3", new String[] {"role1", "role2", "role3"});
			RoleUserAttributes user4_attributes = new RoleUserAttributes("thepassword4", new String[] {"role2", "role3"});
			users
				.addUser("login1", user1_attributes)
				.addUser("login2", user2_attributes)
				.addUser("login3", user3_attributes)
				.addUser("login4", user4_attributes);
			
			ListMemoryUsers listusers = null;
			
			listusers = new ListMemoryUsers();
			assertTrue(users.listUsers(listusers, 2, 1));
			assertEquals(2, listusers.getUsers().size());
			assertTrue(listusers.getUsers().contains("1,login2,thepassword2"));
			assertTrue(listusers.getUsers().contains("174,login3,thepassword3"));
			
			listusers = new ListMemoryUsers();
			assertTrue(users.listUsers(listusers, 3, 0));
			assertEquals(3, listusers.getUsers().size());
			assertTrue(listusers.getUsers().contains("0,login1,thepassword"));
			assertTrue(listusers.getUsers().contains("1,login2,thepassword2"));
			assertTrue(listusers.getUsers().contains("174,login3,thepassword3"));
			
			listusers = new ListMemoryUsers();
			assertFalse(users.listUsers(listusers, 0, 3));
			assertEquals(0, listusers.getUsers().size());
			
			listusers = new ListMemoryUsers();
			assertTrue(users.listUsers(listusers, 2, 3));
			assertEquals(1, listusers.getUsers().size());
			assertTrue(listusers.getUsers().contains("2,login4,thepassword4"));
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	class ListMemoryRoles implements ListRoles
	{
		private ArrayList<String>	mRoles = new ArrayList<String>();
		
		public ArrayList<String> getRoles()
		{
			return mRoles;
		}
		
		public boolean foundRole(String name)
		{
			mRoles.add(name);
			
			return true;
		}
	}
	
	class ListMemoryUsers implements ListUsers
	{
		private ArrayList<String>	mUsers = new ArrayList<String>();
		
		public ArrayList<String> getUsers()
		{
			return mUsers;
		}
		
		public boolean foundUser(long userId, String login, String password)
		{
			mUsers.add(userId+","+login+","+password);
			
			return true;
		}
	}

	public void testGetUserAttributes()
	{
		MemoryUsers users = new MemoryUsers();
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3");

			RoleUserAttributes user1_attributes = new RoleUserAttributes("thepassword");
			RoleUserAttributes user2_attributes = new RoleUserAttributes("thepassword2", new String[] {"role1", "role2"});
			RoleUserAttributes user3_attributes = new RoleUserAttributes(174, "thepassword3", new String[] {"role1", "role2", "role3"});
			RoleUserAttributes user4_attributes = new RoleUserAttributes("thepassword4", new String[] {"role2", "role3"});
			users
				.addUser("login1", user1_attributes)
				.addUser("login2", user2_attributes)
				.addUser("login3", user3_attributes)
				.addUser("login4", user4_attributes);

			RoleUserAttributes	attributes1 = users.getAttributes("login1");
			RoleUserAttributes	attributes2 = users.getAttributes("login2");
			RoleUserAttributes	attributes3 = users.getAttributes("login3");
			RoleUserAttributes	attributes4 = users.getAttributes("login4");
			assertEquals(attributes1.getUserId(), 0);
			assertEquals(0, attributes1.getRoles().size());
			assertEquals(attributes1.getPassword(), "thepassword");
			
			assertEquals(attributes2.getUserId(), 1);
			assertEquals(attributes2.getRoles().size(), 2);
			assertTrue(attributes2.getRoles().contains("role1"));
			assertTrue(attributes2.getRoles().contains("role2"));
			assertEquals(attributes2.getPassword(), "thepassword2");
			
			assertEquals(attributes3.getUserId(), 174);
			assertEquals(attributes3.getRoles().size(), 3);
			assertTrue(attributes3.getRoles().contains("role1"));
			assertTrue(attributes3.getRoles().contains("role2"));
			assertTrue(attributes3.getRoles().contains("role3"));
			assertEquals(attributes3.getPassword(), "thepassword3");
			
			assertEquals(attributes4.getUserId(), 2);
			assertEquals(attributes4.getRoles().size(), 2);
			assertTrue(attributes4.getRoles().contains("role2"));
			assertTrue(attributes4.getRoles().contains("role3"));
			assertEquals(attributes4.getPassword(), "thepassword4");
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testUserIdSpecification()
	{
		MemoryUsers users = new MemoryUsers();
		
		try
		{
			users
				.addUser("login1", new RoleUserAttributes(0, "thepassword"))
				.addUser("login2", new RoleUserAttributes("thepassword"));
			try
			{
				users.addUser("login3", new RoleUserAttributes(1, "thepassword"));
				fail();
			}
			catch (DuplicateUserIdException e)
			{
				assertTrue(true);
			}
			users
				.addUser("login4", new RoleUserAttributes(2, "thepassword"))
				.addUser("login5", new RoleUserAttributes("thepassword"))
				.addUser("login6", new RoleUserAttributes(847, "thepassword"));
			
			assertTrue(users.containsUser("login1"));
			assertTrue(users.containsUser("login2"));
			assertTrue(!users.containsUser("login3"));
			assertTrue(users.containsUser("login4"));
			assertTrue(users.containsUser("login5"));
			assertTrue(users.containsUser("login6"));
			
			assertEquals("login1", users.getLogin(0));
			assertEquals("login2", users.getLogin(1));
			assertEquals("login4", users.getLogin(2));
			assertEquals("login5", users.getLogin(3));
			assertEquals("login6", users.getLogin(847));
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testValidUsers()
	{
		MemoryUsers users = new MemoryUsers();
		users.setPasswordEncryptor(StringEncryptor.SHA);
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3");

			RoleUserAttributes user1_attributes = new RoleUserAttributes("thepassword");
			users.addUser("login1", user1_attributes);
			RoleUserAttributes user2_attributes = new RoleUserAttributes("SHA:iTeooS7tJ7m1mdRrbUacq/pr1uM=", new String[] {"role1", "role2"});
			users.addUser("login2", user2_attributes);
			RoleUserAttributes user3_attributes = new RoleUserAttributes("thepassword3", new String[] {"role1", "role2", "role3"});
			users.addUser("login3", user3_attributes);
			RoleUserAttributes user4_attributes = new RoleUserAttributes(174, "thepassword4", new String[] {"role2", "role3"});
			users.addUser("login4", user4_attributes);
			
			assertTrue(-1 == users.verifyCredentials(new RoleUser("login", "thepassword")));
			
			assertTrue(0 == users.verifyCredentials(new RoleUser("login1", "thepassword")));
			assertTrue(-1 == users.verifyCredentials(new RoleUser("login1", "thepassword2")));
			assertTrue(-1 == users.verifyCredentials(new RoleUser("login1", "thepassword", "role1")));
			
			assertTrue(1 == users.verifyCredentials(new RoleUser("login2", "thepassword2")));
			assertTrue(-1 == users.verifyCredentials(new RoleUser("login2", "thepassword3")));
			assertTrue(1 == users.verifyCredentials(new RoleUser("login2", "thepassword2", "role1")));
			assertTrue(1 == users.verifyCredentials(new RoleUser("login2", "thepassword2", "role2")));
			assertTrue(-1 == users.verifyCredentials(new RoleUser("login2", "thepassword2", "role3")));
			
			assertTrue(2 == users.verifyCredentials(new RoleUser("login3", "thepassword3")));
			assertTrue(-1 == users.verifyCredentials(new RoleUser("login3", "thepassword4")));
			assertTrue(2 == users.verifyCredentials(new RoleUser("login3", "thepassword3", "role1")));
			assertTrue(2 == users.verifyCredentials(new RoleUser("login3", "thepassword3", "role2")));
			assertTrue(2 == users.verifyCredentials(new RoleUser("login3", "thepassword3", "role3")));
			assertTrue(-1 == users.verifyCredentials(new RoleUser("login3", "thepassword3", "role4")));
			
			assertTrue(174 == users.verifyCredentials(new RoleUser("login4", "thepassword4")));
			assertTrue(-1 == users.verifyCredentials(new RoleUser("login4", "thepassword")));
			assertTrue(-1 == users.verifyCredentials(new RoleUser("login4", "thepassword4", "role1")));
			assertTrue(174 == users.verifyCredentials(new RoleUser("login4", "thepassword4", "role2")));
			assertTrue(174 == users.verifyCredentials(new RoleUser("login4", "thepassword4", "role3")));
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testUsersInRole()
	{
		MemoryUsers users = new MemoryUsers();
		users.setPasswordEncryptor(StringEncryptor.MD5);
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3");

			RoleUserAttributes user1_attributes = new RoleUserAttributes("thepassword");
			users.addUser("login1", user1_attributes);
			RoleUserAttributes user2_attributes = new RoleUserAttributes("thepassword2", new String[] {"role1", "role2"});
			users.addUser("login2", user2_attributes);
			RoleUserAttributes user3_attributes = new RoleUserAttributes("thepassword3", new String[] {"role1", "role2", "role3"});
			users.addUser("login3", user3_attributes);
			RoleUserAttributes user4_attributes = new RoleUserAttributes(174, "thepassword4", new String[] {"role2", "role3"});
			users.addUser("login4", user4_attributes);
			
			assertTrue(!users.isUserInRole(user1_attributes.getUserId(), "role1"));
			assertTrue(users.isUserInRole(user2_attributes.getUserId(), "role1"));
			assertTrue(users.isUserInRole(user3_attributes.getUserId(), "role1"));
			assertTrue(!users.isUserInRole(user4_attributes.getUserId(), "role1"));
			
			assertTrue(!users.isUserInRole(user1_attributes.getUserId(), "role2"));
			assertTrue(users.isUserInRole(user2_attributes.getUserId(), "role2"));
			assertTrue(users.isUserInRole(user3_attributes.getUserId(), "role2"));
			assertTrue(users.isUserInRole(user4_attributes.getUserId(), "role2"));
			
			assertTrue(!users.isUserInRole(user1_attributes.getUserId(), "role3"));
			assertTrue(!users.isUserInRole(user2_attributes.getUserId(), "role3"));
			assertTrue(users.isUserInRole(user3_attributes.getUserId(), "role3"));
			assertTrue(users.isUserInRole(user4_attributes.getUserId(), "role3"));
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testListUsersInRole()
	{
		MemoryUsers users = new MemoryUsers();
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3");

			ListMemoryUsers listusers = new ListMemoryUsers();
			
			RoleUserAttributes user1_attributes = new RoleUserAttributes("thepassword");
			RoleUserAttributes user2_attributes = new RoleUserAttributes("thepassword2", new String[] {"role1", "role2"});
			RoleUserAttributes user3_attributes = new RoleUserAttributes(174, "thepassword3", new String[] {"role1", "role2", "role3"});
			RoleUserAttributes user4_attributes = new RoleUserAttributes("thepassword4", new String[] {"role2", "role3"});
			users
				.addUser("login1", user1_attributes)
				.addUser("login2", user2_attributes)
				.addUser("login3", user3_attributes)
				.addUser("login4", user4_attributes);
			
			assertFalse(users.listUsersInRole(null, "role1"));
			assertFalse(users.listUsersInRole(listusers, null));
			assertFalse(users.listUsersInRole(listusers, ""));
			
			assertTrue(users.listUsersInRole(listusers, "role1"));
			assertEquals(2, listusers.getUsers().size());
			assertFalse(listusers.getUsers().contains("0,login1,thepassword"));
			assertTrue(listusers.getUsers().contains("1,login2,thepassword2"));
			assertTrue(listusers.getUsers().contains("174,login3,thepassword3"));
			assertFalse(listusers.getUsers().contains("2,login4,thepassword4"));
			
			listusers = new ListMemoryUsers();
			assertTrue(users.listUsersInRole(listusers, "role2"));
			assertEquals(3, listusers.getUsers().size());
			assertFalse(listusers.getUsers().contains("0,login1,thepassword"));
			assertTrue(listusers.getUsers().contains("1,login2,thepassword2"));
			assertTrue(listusers.getUsers().contains("174,login3,thepassword3"));
			assertTrue(listusers.getUsers().contains("2,login4,thepassword4"));
			
			listusers = new ListMemoryUsers();
			assertTrue(users.listUsersInRole(listusers, "role3"));
			assertEquals(2, listusers.getUsers().size());
			assertFalse(listusers.getUsers().contains("0,login1,thepassword"));
			assertFalse(listusers.getUsers().contains("1,login2,thepassword2"));
			assertTrue(listusers.getUsers().contains("174,login3,thepassword3"));
			assertTrue(listusers.getUsers().contains("2,login4,thepassword4"));
			
			listusers = new ListMemoryUsers();
			assertFalse(users.listUsersInRole(listusers, "role4"));
			assertEquals(0, listusers.getUsers().size());
			assertFalse(listusers.getUsers().contains("0,login1,thepassword"));
			assertFalse(listusers.getUsers().contains("1,login2,thepassword2"));
			assertFalse(listusers.getUsers().contains("174,login3,thepassword3"));
			assertFalse(listusers.getUsers().contains("2,login4,thepassword4"));
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveUsersByLogin()
	{
		MemoryUsers users = new MemoryUsers();
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3")
				.addUser("login1", new RoleUserAttributes("thepassword"))
				.addUser("login2", new RoleUserAttributes("thepassword2", new String[] {"role1", "role2"}))
				.addUser("login3", new RoleUserAttributes("thepassword3", new String[] {"role1", "role2", "role3"}))
				.addUser("login4", new RoleUserAttributes(174, "thepassword4", new String[] {"role2", "role3"}));
			
			assertEquals(4, users.countUsers());
			assertTrue(!users.removeUser("login5"));
			assertEquals(4, users.countUsers());
			assertTrue(users.removeUser("login4"));
			assertEquals(3, users.countUsers());
			assertTrue(users.removeUser("login1"));
			assertEquals(2, users.countUsers());
			assertTrue(users.removeUser("login2"));
			assertEquals(1, users.countUsers());
			assertTrue(users.removeUser("login3"));
			assertEquals(0, users.countUsers());
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testRemoveUsersByUserId()
	{
		MemoryUsers users = new MemoryUsers();
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3")
				.addUser("login1", new RoleUserAttributes(1, "thepassword"))
				.addUser("login2", new RoleUserAttributes(23, "thepassword2", new String[] {"role1", "role2"}))
				.addUser("login3", new RoleUserAttributes(14, "thepassword3", new String[] {"role1", "role2", "role3"}))
				.addUser("login4", new RoleUserAttributes(174, "thepassword4", new String[] {"role2", "role3"}));
			
			assertEquals(4, users.countUsers());
			assertTrue(!users.removeUser(5));
			assertEquals(4, users.countUsers());
			assertTrue(users.removeUser(174));
			assertEquals(3, users.countUsers());
			assertTrue(users.removeUser(1));
			assertEquals(2, users.countUsers());
			assertTrue(users.removeUser(23));
			assertEquals(1, users.countUsers());
			assertTrue(users.removeUser(14));
			assertEquals(0, users.countUsers());
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testRemoveRole()
	{
		MemoryUsers users = new MemoryUsers();

		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3")
				.addUser("login1", new RoleUserAttributes(1, "thepassword"))
				.addUser("login2", new RoleUserAttributes(23, "thepassword2", new String[] {"role1", "role2"}))
				.addUser("login3", new RoleUserAttributes(14, "thepassword3", new String[] {"role1", "role2", "role3"}))
				.addUser("login4", new RoleUserAttributes(174, "thepassword4", new String[] {"role2", "role3"}));
			
			assertEquals(3, users.countRoles());
			assertTrue(!users.removeRole("role4"));
			assertEquals(3, users.countRoles());
			assertTrue(users.removeRole("role3"));
			assertEquals(2, users.countRoles());
			
			RoleUserAttributes attributes = users.getAttributes("login3");
			assertTrue(attributes.getRoles().contains("role1"));
			assertTrue(attributes.getRoles().contains("role2"));
			assertTrue(!attributes.getRoles().contains("role3"));
			
			assertTrue(users.removeRole("role2"));
			attributes = users.getAttributes("login4");
			assertEquals(attributes.getRoles().size(), 0);
			
			attributes = users.getAttributes("login2");
			assertTrue(attributes.getRoles().contains("role1"));
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	public void testClearUsers()
	{
		MemoryUsers users = new MemoryUsers();
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3")
				.addUser("login1", new RoleUserAttributes("thepassword"))
				.addUser("login2", new RoleUserAttributes("thepassword2", new String[] {"role1", "role2"}))
				.addUser("login3", new RoleUserAttributes("thepassword3", new String[] {"role1", "role2", "role3"}))
				.addUser("login4", new RoleUserAttributes(174, "thepassword4", new String[] {"role2", "role3"}));
			
			assertEquals(4, users.countUsers());
			users.clearUsers();
			assertEquals(0, users.countUsers());
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testVerifyCredentials()
	{
		MemoryUsers users = new MemoryUsers();
		users.setPasswordEncryptor(StringEncryptor.OBF);

		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3");

			RoleUserAttributes user1_attributes = new RoleUserAttributes("thepassword");
			users.addUser("login1", user1_attributes);
			RoleUserAttributes user2_attributes = new RoleUserAttributes("thepassword2", new String[] {"role1", "role2"});
			users.addUser("login2", user2_attributes);
			RoleUserAttributes user3_attributes = new RoleUserAttributes("thepassword3", new String[] {"role1", "role2", "role3"});
			users.addUser("login3", user3_attributes);
			
			RoleUser user = new RoleUser();
			user.setLogin("login2");
			user.setPassword("thepassword2");
			user.setRole("role2");
			
			assertEquals(user2_attributes.getUserId(), users.verifyCredentials(user));
			
			user.setRole(null);
			
			assertEquals(user2_attributes.getUserId(), users.verifyCredentials(user));
			
			user.setRole("role3");
			
			assertEquals(-1, users.verifyCredentials(user));
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testToXml()
	{
		MemoryUsers users = new MemoryUsers();
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3")
				.addUser("login1", new RoleUserAttributes("thepassword"))
				.addUser("login2", new RoleUserAttributes("thepassword2", new String[] {"role1", "role2"}))
				.addUser("login3", new RoleUserAttributes("thepassword3", new String[] {"role1", "role2", "role3"}))
				.addUser("login4", new RoleUserAttributes(64, "thepassword4"))
				.addUser("login5", new RoleUserAttributes(9, "thepassword5", new String[] {"role1", "role2"}));
			
			assertEquals("<credentials>\n"+
				"\t<user login=\"login1\">\n"+
				"\t\t<password>thepassword</password>\n"+
				"\t</user>\n"+
				"\t<user login=\"login2\">\n"+
				"\t\t<password>thepassword2</password>\n"+
				"\t\t<role>role1</role>\n"+
				"\t\t<role>role2</role>\n"+
				"\t</user>\n"+
				"\t<user login=\"login3\">\n"+
				"\t\t<password>thepassword3</password>\n"+
				"\t\t<role>role1</role>\n"+
				"\t\t<role>role2</role>\n"+
				"\t\t<role>role3</role>\n"+
				"\t</user>\n"+
				"\t<user login=\"login4\" userid=\"64\">\n"+
				"\t\t<password>thepassword4</password>\n"+
				"\t</user>\n"+
				"\t<user login=\"login5\" userid=\"9\">\n"+
				"\t\t<password>thepassword5</password>\n"+
				"\t\t<role>role1</role>\n"+
				"\t\t<role>role2</role>\n"+
				"\t</user>\n"+
				"</credentials>\n", users.toXml());
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testEncryptedToXml()
	{
		MemoryUsers users = new MemoryUsers();
		users.setPasswordEncryptor(StringEncryptor.SHA);
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3")
				.addUser("login1", new RoleUserAttributes("thepassword"))
				.addUser("login2", new RoleUserAttributes("thepassword2", new String[] {"role1", "role2"}))
				.addUser("login3", new RoleUserAttributes("thepassword3", new String[] {"role1", "role2", "role3"}))
				.addUser("login4", new RoleUserAttributes(64, "thepassword4"))
				.addUser("login5", new RoleUserAttributes(9, "thepassword5", new String[] {"role1", "role2"}));
			
			assertEquals("<credentials>\n"+
				"\t<user login=\"login1\">\n"+
				"\t\t<password>SHA:gquHbROHv6/kbMHIou8HTq5Qyx0=</password>\n"+
				"\t</user>\n"+
				"\t<user login=\"login2\">\n"+
				"\t\t<password>SHA:iTeooS7tJ7m1mdRrbUacq/pr1uM=</password>\n"+
				"\t\t<role>role1</role>\n"+
				"\t\t<role>role2</role>\n"+
				"\t</user>\n"+
				"\t<user login=\"login3\">\n"+
				"\t\t<password>SHA:nyGLrpBEibAyX2s8mZSR97HwhpQ=</password>\n"+
				"\t\t<role>role1</role>\n"+
				"\t\t<role>role2</role>\n"+
				"\t\t<role>role3</role>\n"+
				"\t</user>\n"+
				"\t<user login=\"login4\" userid=\"64\">\n"+
				"\t\t<password>SHA:md3fSecksLrBRiH+oNDUs1Rik88=</password>\n"+
				"\t</user>\n"+
				"\t<user login=\"login5\" userid=\"9\">\n"+
				"\t\t<password>SHA:z6BF3VuavxQPTCgIKhiAwdiz5Z0=</password>\n"+
				"\t\t<role>role1</role>\n"+
				"\t\t<role>role2</role>\n"+
				"\t</user>\n"+
				"</credentials>\n", users.toXml());
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testStoreXml()
	{
		MemoryUsers users = new MemoryUsers();
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3");

			RoleUserAttributes	user1_attributes = new RoleUserAttributes("thepassword");
			RoleUserAttributes	user2_attributes = new RoleUserAttributes("thepassword2", new String[] {"role1", "role2"});
			RoleUserAttributes	user3_attributes = new RoleUserAttributes("thepassword3", new String[] {"role1", "role2", "role3"});
			RoleUserAttributes	user4_attributes = new RoleUserAttributes(64, "thepassword4");
			RoleUserAttributes	user5_attributes = new RoleUserAttributes(9, "thepassword5", new String[] {"role1", "role2"});
			users
				.addUser("login1", user1_attributes)
				.addUser("login2", user2_attributes)
				.addUser("login3", user3_attributes)
				.addUser("login4", user4_attributes)
				.addUser("login5", user5_attributes);
			
			String	xml_filename = "memoryusers_storexml_test.xml";
			String	xml_path = RifeConfig.Global.getTempPath()+File.separator+xml_filename;
			File	xml_file = new File(xml_path);
			users.storeToXml(xml_file);

			MemoryUsers users_stored = new MemoryUsers(xml_filename, ResourceFinderClasspath.getInstance());

			assertEquals(5, users_stored.countUsers());
			
			assertTrue(users_stored.containsUser("login1"));
			assertTrue(users_stored.containsUser("login2"));
			assertTrue(users_stored.containsUser("login3"));
			assertTrue(users_stored.containsUser("login4"));
			assertTrue(users_stored.containsUser("login5"));
			assertTrue(!users_stored.containsUser("login6"));
			
			assertEquals(3, users_stored.countRoles());

			assertTrue(users_stored.containsRole("role1"));
			assertTrue(users_stored.containsRole("role2"));
			assertTrue(users_stored.containsRole("role3"));
			assertTrue(!users_stored.containsRole("role4"));

			assertEquals(users_stored.getUserId("login1"), user1_attributes.getUserId());
			assertEquals(users_stored.getUserId("login2"), user2_attributes.getUserId());
			assertEquals(users_stored.getUserId("login3"), user3_attributes.getUserId());
			assertEquals(users_stored.getUserId("login4"), user4_attributes.getUserId());
			assertEquals(users_stored.getUserId("login5"), user5_attributes.getUserId());

			assertEquals(users_stored.getLogin(user1_attributes.getUserId()), "login1");
			assertEquals(users_stored.getLogin(user2_attributes.getUserId()), "login2");
			assertEquals(users_stored.getLogin(user3_attributes.getUserId()), "login3");
			assertEquals(users_stored.getLogin(user4_attributes.getUserId()), "login4");
			assertEquals(users_stored.getLogin(user5_attributes.getUserId()), "login5");

			RoleUserAttributes	user1_attributes_new = users_stored.getAttributes("login1");
			RoleUserAttributes	user2_attributes_new = users_stored.getAttributes("login2");
			RoleUserAttributes	user3_attributes_new = users_stored.getAttributes("login3");
			RoleUserAttributes	user4_attributes_new = users_stored.getAttributes("login4");
			RoleUserAttributes	user5_attributes_new = users_stored.getAttributes("login5");
			assertFalse(user1_attributes == user1_attributes_new);
			assertFalse(user2_attributes == user2_attributes_new);
			assertFalse(user3_attributes == user3_attributes_new);
			assertFalse(user4_attributes == user4_attributes_new);
			assertFalse(user5_attributes == user5_attributes_new);
			assertEquals(user1_attributes, user1_attributes_new);
			assertEquals(user2_attributes, user2_attributes_new);
			assertEquals(user3_attributes, user3_attributes_new);
			assertEquals(user4_attributes, user4_attributes_new);
			assertEquals(user5_attributes, user5_attributes_new);
			
			RoleUserAttributes	user6_attributes = new RoleUserAttributes(90, "thepassword6", new String[] {"role2"});
			users_stored.addUser("login6", user6_attributes);
			users_stored.storeToXml();

			MemoryUsers users_stored2 = new MemoryUsers(xml_filename, ResourceFinderClasspath.getInstance());
			assertEquals(6, users_stored2.countUsers());
			assertTrue(users_stored2.containsUser("login6"));
			assertEquals(3, users_stored2.countRoles());
			assertEquals(users_stored2.getUserId("login6"), user6_attributes.getUserId());
			assertEquals(users_stored2.getLogin(user6_attributes.getUserId()), "login6");
			RoleUserAttributes	user6_attributes_new = users_stored2.getAttributes("login6");
			assertFalse(user6_attributes == user6_attributes_new);
			assertEquals(user6_attributes, user6_attributes_new);
			
			xml_file.delete();
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testListUserRoles()
	{
		MemoryUsers users = new MemoryUsers();
		
		try
		{
			users
				.addRole("role1")
				.addRole("role2")
				.addRole("role3");
			
			RoleUserAttributes user1_attributes = new RoleUserAttributes(49, "thepassword");
			users.addUser("login1", user1_attributes);
			RoleUserAttributes user2_attributes = new RoleUserAttributes(322, "thepassword2", new String[] {"role1", "role2"});
			users.addUser("login2", user2_attributes);
			RoleUserAttributes user3_attributes = new RoleUserAttributes(2, "thepassword3", new String[] {"role1", "role2", "role3"});
			users.addUser("login3", user3_attributes);
			
			ListMemoryRoles listroles = new ListMemoryRoles();
			
			assertFalse(users.listUserRoles("login1", listroles));
			
			assertTrue(users.listUserRoles("login2", listroles));
			assertEquals(listroles.getRoles().size(), 2);
			assertTrue(listroles.getRoles().contains("role1"));
			assertTrue(listroles.getRoles().contains("role2"));
			
			listroles = new ListMemoryRoles();
			
			assertTrue(users.listUserRoles("login3", listroles));
			assertEquals(listroles.getRoles().size(), 3);
			assertTrue(listroles.getRoles().contains("role1"));
			assertTrue(listroles.getRoles().contains("role2"));
			assertTrue(listroles.getRoles().contains("role3"));
		}
		catch (CredentialsManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
}


