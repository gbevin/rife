/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestRoleUserAttributes.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import java.util.ArrayList;
import junit.framework.TestCase;

public class TestRoleUserAttributes extends TestCase
{
	public TestRoleUserAttributes(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		RoleUserAttributes user_attributes = null;
		
		user_attributes = new RoleUserAttributes("thepassword");
		assertNotNull(user_attributes);
		assertEquals("thepassword", user_attributes.getPassword());
		
		ArrayList<String> roles = new ArrayList<String>();
		roles.add("firstrole");
		roles.add("secondrole");
		user_attributes = new RoleUserAttributes("thepassword", roles);
		assertNotNull(user_attributes);
		assertEquals("thepassword", user_attributes.getPassword());
		
		assertEquals(2, user_attributes.getRoles().size());
		boolean	firstrole = false;
		boolean	secondrole = false;
		for (String role : user_attributes.getRoles())
		{
			if (role.equals("firstrole"))
			{
				firstrole = true;
			}
			else if (role.equals("secondrole"))
			{
				secondrole = true;
			}
		}
		assertTrue(firstrole && secondrole);

		user_attributes = new RoleUserAttributes("thepassword", new String[] {"firstrole", "secondrole"});
		assertNotNull(user_attributes);
		assertEquals("thepassword", user_attributes.getPassword());
		
		assertEquals(2, user_attributes.getRoles().size());
		firstrole = false;
		secondrole = false;
		for (String role : user_attributes.getRoles())
		{
			if (role.equals("firstrole"))
			{
				firstrole = true;
			}
			else if (role.equals("secondrole"))
			{
				secondrole = true;
			}
		}
		assertTrue(firstrole && secondrole);
	}

	public void testEquals()
	{
		RoleUserAttributes user_attributes1 = new RoleUserAttributes("thepassword");
		RoleUserAttributes user_attributes2 = new RoleUserAttributes("thepassword");
		RoleUserAttributes user_attributes3 = new RoleUserAttributes("thepassword2");
		RoleUserAttributes user_attributes4 = new RoleUserAttributes(12, "thepassword2");
		RoleUserAttributes user_attributes5 = new RoleUserAttributes("thepassword", new String[] {"firstrole", "secondrole"});
		RoleUserAttributes user_attributes6 = new RoleUserAttributes("thepassword", new String[] {"firstrole", "secondrole"});
		RoleUserAttributes user_attributes7 = new RoleUserAttributes("thepassword", new String[] {"firstrole"});
		RoleUserAttributes user_attributes8 = new RoleUserAttributes("thepassword", new String[] {"firstrole", "thirdrole"});
		RoleUserAttributes user_attributes9 = new RoleUserAttributes(13, "thepassword", new String[] {"firstrole", "secondrole"});
		RoleUserAttributes user_attributes10 = new RoleUserAttributes(13, "thepassword", new String[] {"firstrole", "secondrole"});
		
		assertTrue(user_attributes1.equals(user_attributes1));
		assertTrue(user_attributes1.equals(user_attributes2));
		assertFalse(user_attributes1.equals(user_attributes3));
		assertFalse(user_attributes1.equals(user_attributes4));
		assertFalse(user_attributes1.equals(user_attributes5));
		assertFalse(user_attributes1.equals(user_attributes6));
		assertFalse(user_attributes1.equals(user_attributes7));
		assertFalse(user_attributes1.equals(user_attributes8));
		assertFalse(user_attributes1.equals(user_attributes9));
		assertFalse(user_attributes1.equals(user_attributes10));
		
		assertTrue(user_attributes2.equals(user_attributes2));
		assertFalse(user_attributes2.equals(user_attributes3));
		assertFalse(user_attributes2.equals(user_attributes4));
		assertFalse(user_attributes2.equals(user_attributes5));
		assertFalse(user_attributes2.equals(user_attributes6));
		assertFalse(user_attributes2.equals(user_attributes7));
		assertFalse(user_attributes2.equals(user_attributes8));
		assertFalse(user_attributes2.equals(user_attributes9));
		assertFalse(user_attributes2.equals(user_attributes10));
		
		assertTrue(user_attributes3.equals(user_attributes3));
		assertFalse(user_attributes3.equals(user_attributes4));
		assertFalse(user_attributes3.equals(user_attributes5));
		assertFalse(user_attributes3.equals(user_attributes6));
		assertFalse(user_attributes3.equals(user_attributes7));
		assertFalse(user_attributes3.equals(user_attributes8));
		assertFalse(user_attributes3.equals(user_attributes9));
		assertFalse(user_attributes3.equals(user_attributes10));
		
		assertTrue(user_attributes4.equals(user_attributes4));
		assertFalse(user_attributes4.equals(user_attributes5));
		assertFalse(user_attributes4.equals(user_attributes6));
		assertFalse(user_attributes4.equals(user_attributes7));
		assertFalse(user_attributes4.equals(user_attributes8));
		assertFalse(user_attributes4.equals(user_attributes9));
		assertFalse(user_attributes4.equals(user_attributes10));
		
		assertTrue(user_attributes5.equals(user_attributes5));
		assertTrue(user_attributes5.equals(user_attributes6));
		assertFalse(user_attributes5.equals(user_attributes7));
		assertFalse(user_attributes5.equals(user_attributes8));
		assertFalse(user_attributes5.equals(user_attributes9));
		assertFalse(user_attributes5.equals(user_attributes10));
		
		assertTrue(user_attributes6.equals(user_attributes6));
		assertFalse(user_attributes6.equals(user_attributes7));
		assertFalse(user_attributes6.equals(user_attributes8));
		assertFalse(user_attributes6.equals(user_attributes9));
		assertFalse(user_attributes6.equals(user_attributes10));
		
		assertTrue(user_attributes7.equals(user_attributes7));
		assertFalse(user_attributes7.equals(user_attributes8));
		assertFalse(user_attributes7.equals(user_attributes9));
		assertFalse(user_attributes7.equals(user_attributes10));
		
		assertTrue(user_attributes8.equals(user_attributes8));
		assertFalse(user_attributes8.equals(user_attributes9));
		assertFalse(user_attributes8.equals(user_attributes10));
		
		assertTrue(user_attributes9.equals(user_attributes9));
		assertTrue(user_attributes9.equals(user_attributes10));
		
		assertTrue(user_attributes10.equals(user_attributes10));
	}
	
	public void testEmptyInitialRoles()
	{
		RoleUserAttributes user_attributes = new RoleUserAttributes("thepassword");
		
		assertEquals(0, user_attributes.getRoles().size());
	}
	
	public void testPopulate()
	{
		RoleUserAttributes user_attributes = new RoleUserAttributes("thepassword");
		
		ArrayList<String> roles = new ArrayList<String>();
		roles.add("firstrole");
		roles.add("secondrole");
		user_attributes.setRoles(roles);
		
		assertEquals("thepassword", user_attributes.getPassword());
		
		assertEquals(2, user_attributes.getRoles().size());
		boolean	firstrole = false;
		boolean	secondrole = false;
		for (String role : user_attributes.getRoles())
		{
			if (role.equals("firstrole"))
			{
				firstrole = true;
			}
			else if (role.equals("secondrole"))
			{
				secondrole = true;
			}
		}
		
		assertTrue(firstrole && secondrole);
	}
	
	public void testIsInRole()
	{
		RoleUserAttributes user_attributes = new RoleUserAttributes("thepassword");
		
		ArrayList<String> roles = new ArrayList<String>();
		roles.add("firstrole");
		roles.add("secondrole");
		user_attributes.setRoles(roles);
		
		assertTrue(user_attributes.isInRole("firstrole"));
		assertTrue(user_attributes.isInRole("secondrole"));
		assertTrue(!user_attributes.isInRole("thirdrole"));
	}
	
	public void testIsValid()
	{
		RoleUserAttributes user_attributes = new RoleUserAttributes("thepassword");
		
		ArrayList<String> roles = new ArrayList<String>();
		roles.add("firstrole");
		roles.add("secondrole");
		user_attributes.setRoles(roles);
		
		assertTrue(user_attributes.isValid("thepassword"));
		assertTrue(!user_attributes.isValid("anotherpassword"));
		assertTrue(user_attributes.isValid("thepassword", "firstrole"));
		assertTrue(user_attributes.isValid("thepassword", "secondrole"));
		assertTrue(!user_attributes.isValid("anotherpassword", "firstrole"));
		assertTrue(!user_attributes.isValid("anotherpassword", "secondrole"));
		assertTrue(!user_attributes.isValid("thepassword", "thirdrole"));
		assertTrue(!user_attributes.isValid("anotherpassword", "thirdrole"));
	}
}
