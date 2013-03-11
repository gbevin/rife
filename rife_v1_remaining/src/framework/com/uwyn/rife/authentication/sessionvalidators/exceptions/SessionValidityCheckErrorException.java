/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SessionValidityCheckErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionvalidators.exceptions;

import com.uwyn.rife.authentication.exceptions.SessionValidatorException;

public class SessionValidityCheckErrorException extends SessionValidatorException
{
	private static final long serialVersionUID = 4277837804430634653L;

	private String	mAuthId = null;
	private String	mHostIp = null;

	public SessionValidityCheckErrorException(String authId, String hostIp)
	{
		this(authId, hostIp, null);
	}

	public SessionValidityCheckErrorException(String authId, String hostIp, Throwable cause)
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
