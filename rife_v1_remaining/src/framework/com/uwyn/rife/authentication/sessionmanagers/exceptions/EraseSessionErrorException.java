/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EraseSessionErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class EraseSessionErrorException extends SessionManagerException
{
	private static final long serialVersionUID = 7163686412279212060L;

	private String	mAuthId = null;

	public EraseSessionErrorException(String authId)
	{
		this(authId, null);
	}
	
	public EraseSessionErrorException(String authId, DatabaseException cause)
	{
		super("Unable to erase the session with authid '"+authId+"'.", cause);
		mAuthId = authId;
	}

	public String getAuthId()
	{
		return mAuthId;
	}
}
