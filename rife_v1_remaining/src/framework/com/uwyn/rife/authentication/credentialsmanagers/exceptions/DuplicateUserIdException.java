/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DuplicateUserIdException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;

public class DuplicateUserIdException extends CredentialsManagerException
{
	private static final long serialVersionUID = 1939667170190873742L;

	private long	mUserId = -1;
	
	public DuplicateUserIdException(long userId)
	{
		super("The user id '"+userId+"' is already present.");
		
		mUserId = userId;
	}
	
	public long getUserId()
	{
		return mUserId;
	}
}
