/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GetUserIdErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class GetUserIdErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = -7151563685570831238L;

	private String 	mLogin = null;
	
	public GetUserIdErrorException(DatabaseException cause, String login)
	{
		super("Error while obtaining the user id of user with login '"+login+"'.", cause);
		mLogin = login;
	}
	
	public String getLogin()
	{
		return mLogin;
	}
}
