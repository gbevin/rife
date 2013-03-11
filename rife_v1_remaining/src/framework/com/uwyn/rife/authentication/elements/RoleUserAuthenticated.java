/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RoleUserAuthenticated.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.authentication.Credentials;
import com.uwyn.rife.authentication.credentials.RoleUserCredentials;

/**
 * Extends the generic {@link Authenticated} element with support for a {@code role}
 * property that will be used together with {@link RoleUserCredentials}
 * credentials to only allow people with that role to access the child page.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public abstract class RoleUserAuthenticated extends Authenticated
{
	public boolean hasAttribute(String key)
	{
		return key.equals("role") &&
			   hasProperty("role");
	}
	
	public String getAttribute(String key)
	{
		if (key.equals("role"))
		{
			return getPropertyString("role");
		}
		
		return null;
	}
	
	protected void validatedCredentials(Credentials credentials)
	{
		if (hasProperty("role"))
		{
			((RoleUserCredentials)credentials).setRole(getPropertyString("role"));
		}
	}
}
