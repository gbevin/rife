/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EraseUserSessionsErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class EraseUserSessionsErrorException extends SessionManagerException
{
	private static final long serialVersionUID = -322527253962142510L;

	public EraseUserSessionsErrorException(long userId)
	{
		this(userId, null);
	}

	public EraseUserSessionsErrorException(long userId, DatabaseException cause)
	{
		super("Unable to erase all the sessions for user id "+userId+".", cause);
	}
}
