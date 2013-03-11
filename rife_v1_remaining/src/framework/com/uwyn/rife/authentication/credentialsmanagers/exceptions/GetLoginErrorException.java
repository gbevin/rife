/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GetLoginErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class GetLoginErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = -3880034096005166200L;

	private long 	mUserId = -1;
	
	public GetLoginErrorException(DatabaseException cause, long userId)
	{
		super("Error while obtaining the login of user with id '"+userId+"'.", cause);
		mUserId = userId;
	}
	
	public long getUserId()
	{
		return mUserId;
	}
}
