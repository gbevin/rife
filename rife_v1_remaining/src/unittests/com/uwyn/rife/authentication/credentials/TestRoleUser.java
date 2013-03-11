/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestRoleUser.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentials;

import com.uwyn.rife.site.ValidationError;
import java.util.Iterator;
import junit.framework.TestCase;

public class TestRoleUser extends TestCase
{
	public TestRoleUser(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		RoleUser user = null;
		
		user = new RoleUser();
		
		assertNotNull(user);
	}

	public void testInitialEmptyLogin()
	{
		RoleUser user = new RoleUser();
		
		assertNull(user.getLogin());
	}

	public void testInitialEmptyPassword()
	{
		RoleUser user = new RoleUser();
		
		assertNull(user.getPassword());
	}

	public void testInitialEmptyRole()
	{
		RoleUser user = new RoleUser();
		
		assertNull(user.getRole());
	}

	public void testPopulation()
	{
		RoleUser user = new RoleUser();
		
		user.setLogin("the login");
		user.setPassword("the password");
		user.setRole("the role");
		
		assertEquals("the login", user.getLogin());
		assertEquals("the password", user.getPassword());
		assertEquals("the role", user.getRole());
	}

	public void testValidation()
	{
		RoleUser user = new RoleUser();

		Iterator<ValidationError>	validationerrors_it = null;
		ValidationError				validationerror = null;
		
		assertTrue(false == user.validate());
		
		validationerrors_it = user.getValidationErrors().iterator();
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("MANDATORY", validationerror.getIdentifier());
		assertEquals("login", validationerror.getSubject());
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("MANDATORY", validationerror.getIdentifier());
		assertEquals("password", validationerror.getSubject());
		assertTrue(false == validationerrors_it.hasNext());
		
		user.resetValidation();
		
		user.setLogin("e");
		user.setPassword("f");

		assertTrue(false == user.validate());
		
		validationerrors_it = user.getValidationErrors().iterator();
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("WRONGLENGTH", validationerror.getIdentifier());
		assertEquals("login", validationerror.getSubject());
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("WRONGLENGTH", validationerror.getIdentifier());
		assertEquals("password", validationerror.getSubject());
		assertTrue(false == validationerrors_it.hasNext());
		
		user.resetValidation();
		
		user.setLogin("alogin");
		user.setPassword("apassword");

		assertTrue(user.validate());
		
		validationerrors_it = user.getValidationErrors().iterator();
		assertTrue(false == validationerrors_it.hasNext());
	}
}
