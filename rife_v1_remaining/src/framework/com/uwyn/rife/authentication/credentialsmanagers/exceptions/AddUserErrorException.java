/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AddUserErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;

public class AddUserErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = -2931184377699186549L;
	
	private String				mLogin = null;
	private RoleUserAttributes	mAttributes = null;
	
	public AddUserErrorException(String login, RoleUserAttributes attributes)
	{
		this(login, attributes, null);
	}
	
	public AddUserErrorException(String login, RoleUserAttributes attributes, Throwable cause)
	{
		super("Error while adding user with login '"+login+"' and attributes '"+attributes+"'.", cause);
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
