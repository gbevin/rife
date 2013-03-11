/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContainsUserErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class ContainsUserErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = -8188124830768131352L;

	private String	mLogin = null;
	
	public ContainsUserErrorException(String login)
	{
		this(login, null);
	}
	
	public ContainsUserErrorException(String login, DatabaseException cause)
	{
		super("Error while checking if the user with login '"+login+"' is present.", cause);
		mLogin = login;
	}
	
	public String getLogin()
	{
		return mLogin;
	}
}
