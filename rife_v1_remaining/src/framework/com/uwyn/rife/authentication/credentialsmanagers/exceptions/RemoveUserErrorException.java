/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RemoveUserErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class RemoveUserErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = -272534511065525807L;

	private String	mLogin = null;
	private Long    mUserid = null;
	
	public RemoveUserErrorException(String role)
	{
		this(role, null);
	}
	
	public RemoveUserErrorException(String login, DatabaseException cause)
	{
		super("Error while removing user with login '"+login+"'.", cause);
		mLogin = login;
	}
	
	public RemoveUserErrorException(Long userId, DatabaseException cause)
	{
		super("Error while removing user with login '"+userId+"'.", cause);
		mUserid = userId;
	}
	
	public String getLogin()
	{
		return mLogin;
	}
	
	public Long getUserId()
	{
		return mUserid;
	}
}
