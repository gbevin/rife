/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RoleUserIdentity.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers;

public class RoleUserIdentity implements Cloneable
{
	private String				mLogin = null;
	private RoleUserAttributes	mAttributes = null;
	
	public RoleUserIdentity(String login, RoleUserAttributes attributes)
	{
		if (null == login)			throw new IllegalArgumentException("login can't be null.");
		if (0 == login.length())	throw new IllegalArgumentException("login can't be empty.");
		if (null == attributes)		throw new IllegalArgumentException("attributes can't be null.");

		mLogin = login;
		mAttributes = attributes;
	}
	
	public String getLogin()
	{
		return mLogin;
	}
	
	public RoleUserAttributes getAttributes()
	{
		return mAttributes;
	}

	public RoleUserIdentity clone()
	{
        RoleUserIdentity new_identity = null;
		try
		{
			new_identity = (RoleUserIdentity)super.clone();

			new_identity.mAttributes = mAttributes.clone();
		}
		catch (CloneNotSupportedException e)
		{
			new_identity = null;
		}

		return new_identity;
	}
	
	public boolean equals(Object other)
	{
		if (null == other)
		{
			return false;
		}
		
		if (this == other)
		{
			return true;
		}
		
		if (!(other instanceof RoleUserIdentity))
		{
			return false;
		}

		RoleUserIdentity other_identity = (RoleUserIdentity)other;
		if (!getLogin().equals(other_identity.getLogin()))
		{
			return false;
		}
		if (!getAttributes().equals(other_identity.getAttributes()))
		{
			return false;
		}
		
		return true;
	}
}

