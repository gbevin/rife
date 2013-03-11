/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DuplicateLoginException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;

public class DuplicateLoginException extends CredentialsManagerException
{
	private static final long serialVersionUID = -5868340001604117437L;

	private String	mLogin = null;
	
	public DuplicateLoginException(String login)
	{
		super("The login '"+login+"' is already present.");
		
		mLogin = login;
	}
	
	public String getLogin()
	{
		return mLogin;
	}
}
