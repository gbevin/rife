/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UpdateUserErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;

public class UpdateUserErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = 644224221069008281L;

	private String				mLogin = null;
	private RoleUserAttributes	mAttributes = null;
	
	public UpdateUserErrorException(String role, RoleUserAttributes attributes)
	{
		this(role, attributes, null);
	}
	
	public UpdateUserErrorException(String login, RoleUserAttributes attributes, Throwable cause)
	{
		super("Error while updating user with login '"+login+"'.", cause);
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
}
