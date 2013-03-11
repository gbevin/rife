/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SessionRememberedCheckErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class SessionRememberedCheckErrorException extends SessionManagerException
{
	private static final long serialVersionUID = -9064926639559046361L;

	private String	mAuthId = null;

	public SessionRememberedCheckErrorException(String authId)
	{
		this(authId, null);
	}
	
	public SessionRememberedCheckErrorException(String authId, DatabaseException cause)
	{
		super("Unable to check whether the session with authid '"+authId+"' was created from remembered data.", cause);
		mAuthId = authId;
	}

	public String getAuthId()
	{
		return mAuthId;
	}
}
