/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: IsSessionValidErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class IsSessionValidErrorException extends SessionManagerException
{
	private static final long serialVersionUID = -4255610298209171384L;

	private String	mAuthId = null;
	private String	mHostIp = null;

	public IsSessionValidErrorException(String authId, String hostIp)
	{
		this(authId, hostIp, null);
	}

	public IsSessionValidErrorException(String authId, String hostIp, DatabaseException cause)
	{
		super("Unable to check the validity of the session with authid '"+authId+"' for hostip '"+hostIp+"'.", cause);
		mAuthId = authId;
		mHostIp = hostIp;
	}

	public String getAuthId()
	{
		return mAuthId;
	}

	public String getHostIp()
	{
		return mHostIp;
	}
}
