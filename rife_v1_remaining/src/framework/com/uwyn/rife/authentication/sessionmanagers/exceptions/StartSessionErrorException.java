/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StartSessionErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class StartSessionErrorException extends SessionManagerException
{
	private static final long serialVersionUID = 1599170266402021852L;

	private long	mUserId = -1;
	private String	mHostIp = null;

	public StartSessionErrorException(long userId, String hostIp)
	{
		this(userId, hostIp, null);
	}

	public StartSessionErrorException(long userId, String hostIp, DatabaseException cause)
	{
		super("Unable to start session for userid '"+userId+"' and hostip '"+hostIp+"'.", cause);
		mUserId = userId;
		mHostIp = hostIp;
	}

	public long getUserId()
	{
		return mUserId;
	}

	public String getHostIp()
	{
		return mHostIp;
	}
}
