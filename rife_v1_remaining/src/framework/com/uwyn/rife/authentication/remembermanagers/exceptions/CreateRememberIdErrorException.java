/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CreateRememberIdErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.remembermanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.RememberManagerException;

public class CreateRememberIdErrorException extends RememberManagerException
{
	private static final long serialVersionUID = 5174821054624717542L;

	private long	mUserId = -1;

	public CreateRememberIdErrorException(long userId)
	{
		this(userId, null);
	}

	public CreateRememberIdErrorException(long userId, Throwable cause)
	{
		super("Unable to create a remember id for userid '"+userId+"'.", cause);
		
		mUserId = userId;
	}

	public long getUserId()
	{
		return mUserId;
	}
}
