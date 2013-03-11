/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RoleCheckErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionvalidators.exceptions;

import com.uwyn.rife.authentication.exceptions.SessionValidatorException;

public class RoleCheckErrorException extends SessionValidatorException
{
	private static final long serialVersionUID = -849240411412778497L;

	private String	mAuthId = null;
	private String	mHostIp = null;
	private String	mRole = null;

	public RoleCheckErrorException(String authId, String hostIp, String role)
	{
		this(authId, hostIp, role, null);
	}

	public RoleCheckErrorException(String authId, String hostIp, String role, Throwable cause)
	{
		super("Unable to check the role '"+role+"' for the session with authid '"+authId+"' and hostip '"+hostIp+"'.", cause);
		mAuthId = authId;
		mHostIp = hostIp;
		mRole = role;
	}

	public String getAuthId()
	{
		return mAuthId;
	}

	public String getHostIp()
	{
		return mHostIp;
	}
	
	public String getRole()
	{
		return mRole;
	}
}
