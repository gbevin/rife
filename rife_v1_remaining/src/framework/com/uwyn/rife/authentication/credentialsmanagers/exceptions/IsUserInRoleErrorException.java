/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: IsUserInRoleErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class IsUserInRoleErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = 8354257394797764661L;

	private long	mUserId = -1;
	private String	mRole = null;
	
	public IsUserInRoleErrorException(long userId, String role)
	{
		this(userId, role, null);
	}
	
	public IsUserInRoleErrorException(long userId, String role, DatabaseException cause)
	{
		super("Error while verifying if the user id '"+userId+"' has access to role '"+role+"'.", cause);
		mUserId = userId;
		mRole = role;
	}
	
	public long getUserId()
	{
		return mUserId;
	}
	
	public String getRole()
	{
		return mRole;
	}
}
