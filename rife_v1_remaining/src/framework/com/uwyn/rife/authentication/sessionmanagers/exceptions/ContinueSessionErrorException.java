/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinueSessionErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class ContinueSessionErrorException extends SessionManagerException
{
	private static final long serialVersionUID = -3671791851196693420L;

	private String	mAuthId = null;

	public ContinueSessionErrorException(String authId)
	{
		this(authId, null);
	}

	public ContinueSessionErrorException(String authId, DatabaseException cause)
	{
		super("Unable to continue the session with authid '"+authId+"'.", cause);
		mAuthId = authId;
	}

	public String getAuthId()
	{
		return mAuthId;
	}
}
