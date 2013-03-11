/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnknownRoleErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;

public class UnknownRoleErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = -1534822390523586792L;

	private String				mRole = null;
	private String				mLogin = null;
	private RoleUserAttributes	mAttributes = null;
	
	public UnknownRoleErrorException(String role, String login, RoleUserAttributes attributes)
	{
		this(role, login, attributes, null);
	}
	
	public UnknownRoleErrorException(String role, String login, RoleUserAttributes attributes, Throwable cause)
	{
		super("The role '"+role+"' couldn't be found while adding the adding user with login '"+login+"' and attributes '"+attributes+"'.", cause);
		mRole = role;
		mLogin = login;
		mAttributes = attributes;
	}
	
	public String getLogin()
	{
		return mLogin;
	}
	
	public String getRole()
	{
		return mRole;
	}
	
	public RoleUserAttributes getAttributes()
	{
		return mAttributes;
	}
}
