/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: IsUserValidErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class IsUserValidErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = 2097389998452272264L;

	private String	mLogin = null;
	private String	mRole = null;
	
	public IsUserValidErrorException(String login)
	{
		this(login, null);
	}
	
	public IsUserValidErrorException(String login, DatabaseException cause)
	{
		super("Error while verifying validity of the user with login '"+login+"' is present.", cause);
		mLogin = login;
	}
	
	public IsUserValidErrorException(String login, String role, DatabaseException cause)
	{
		super("Error while verifying validity of the user with login '"+login+"' is present in role '"+role+"'.", cause);
		mLogin = login;
		mRole = role;
	}
	
	public String getLogin()
	{
		return mLogin;
	}
	
	public String getRole()
	{
		return mRole;
	}
}
