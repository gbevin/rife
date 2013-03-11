/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GetAttributesErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class GetAttributesErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = 8779077667203772941L;

	private String	mLogin = null;
	
	public GetAttributesErrorException(String login)
	{
		this(login, null);
	}
	
	public GetAttributesErrorException(String login, DatabaseException cause)
	{
		super("Error while obtaining the attributes of the user with login '"+login+"'.", cause);
		mLogin = login;
	}
	
	public String getLogin()
	{
		return mLogin;
	}
}
