/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RoleUserAttributes.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class RoleUserAttributes implements Cloneable
{
	private long				mUserId = -1;
	private boolean				mAutomaticUserId = false;
	private String				mPassword = null;
	private HashSet<String>		mRoles = null;
	
	public RoleUserAttributes()
	{
	}
	
	public RoleUserAttributes(long userId, String password)
	{
		setUserId(userId);
		setPassword(password);
	}
	
	public RoleUserAttributes(long userId, String password, String[] roles)
	{
		setUserId(userId);
		setPassword(password);
		setRoles(roles);
	}
	
	public RoleUserAttributes(long userId, String password, Collection<String> roles)
	{
		setUserId(userId);
		setPassword(password);
		setRoles(roles);
	}
	
	public RoleUserAttributes(String password)
	{
		setPassword(password);
	}
	
	public RoleUserAttributes(String password, String[] roles)
	{
		setPassword(password);
		setRoles(roles);
	}
	
	public RoleUserAttributes(String password, Collection<String> roles)
	{
		setPassword(password);
		setRoles(roles);
	}
	
	public RoleUserAttributes(long userId)
	{
		setUserId(userId);
	}
	
	public RoleUserAttributes(long userId, String[] roles)
	{
		setUserId(userId);
		setRoles(roles);
	}
	
	public RoleUserAttributes(long userId, Collection<String> roles)
	{
		setUserId(userId);
		setRoles(roles);
	}
	
	public RoleUserAttributes(String[] roles)
	{
		setRoles(roles);
	}
	
	public RoleUserAttributes(Collection<String> roles)
	{
		setRoles(roles);
	}
	
	public void setUserId(long userId)
	{
		if (userId < 0)		throw new IllegalArgumentException("userId can't be negative.");
		
		mUserId = userId;
	}
	
	public long getUserId()
	{
		return mUserId;
	}
	
	void setAutomaticUserId(boolean automatic)
	{
		mAutomaticUserId = automatic;
	}
	
	boolean isAutomaticUserId()
	{
		return mAutomaticUserId;
	}
	
	public void setPassword(String password)
	{
		if (password != null && 0 == password.length())	throw new IllegalArgumentException("password can't be empty.");
		
		mPassword = password;
	}
	
	public String getPassword()
	{
		return mPassword;
	}
	
	public void setRoles(Collection<String> roles)
	{
		if (null == roles)
		{
			mRoles = null;
			return;
		}
		
		mRoles = new HashSet<String>(roles);
	}
	
	public void setRoles(String[] roles)
	{
		if (roles != null &&
			roles.length > 0)
		{
			setRoles(new HashSet<String>(Arrays.asList(roles)));
		}
	}
	
	public void addRole(String role)
	{
		if (null == mRoles)
		{
			mRoles = new HashSet<String>();
		}
		mRoles.add(role);
	}
	
	public void removeRole(String role)
	{
		if (null == mRoles)
		{
			return;
		}
		mRoles.remove(role);
	}
	
	public Collection<String> getRoles()
	{
		if (null == mRoles)
		{
			mRoles = new HashSet<String>();
		}

		return mRoles;
	}
	
	public boolean isInRole(String role)
	{
		if (null == role)		throw new IllegalArgumentException("role can't be null.");
		if (0 == role.length())	throw new IllegalArgumentException("role can't be empty.");
		
		if (null == mRoles)
		{
			return false;
		}
		
		return mRoles.contains(role);
	}
	
	public boolean isValid(String password)
	{
		if (null == password)		throw new IllegalArgumentException("password can't be null.");
		if (0 == password.length())	throw new IllegalArgumentException("password can't be empty.");

		return mPassword != null &&
			   password.equals(mPassword);
	}
	
	public boolean isValid(String password, String role)
	{
		if (isValid(password) &&
			isInRole(role))
		{
			
			return true;
		}
		
		return false;
	}

	public synchronized RoleUserAttributes clone()
	{
        RoleUserAttributes new_attributes = null;
		try
		{
			new_attributes = (RoleUserAttributes)super.clone();

			if (mRoles != null)
			{
				new_attributes.mRoles = new HashSet<String>(mRoles);
			}
		}
		catch (CloneNotSupportedException e)
		{
			new_attributes = null;
		}

		return new_attributes;
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
		
		if (!(other instanceof RoleUserAttributes))
		{
			return false;
		}

		RoleUserAttributes other_attributes = (RoleUserAttributes)other;
		if (getUserId() != other_attributes.getUserId())
		{
			return false;
		}
		if (!getPassword().equals(other_attributes.getPassword()))
		{
			return false;
		}
		Collection<String> roles = getRoles();
		Collection<String> other_roles = other_attributes.getRoles();
		if ((roles != null || other_roles != null))
		{
			if (null == roles || null == other_roles)
			{
				return false;
			}
			if (roles.size() != other_roles.size())
			{
				return false;
			}
			
			for (String role : roles)
			{
 				if (!other_roles.contains(role))
				{
					return false;
				}
			}
		}
		
		return true;
	}
}

