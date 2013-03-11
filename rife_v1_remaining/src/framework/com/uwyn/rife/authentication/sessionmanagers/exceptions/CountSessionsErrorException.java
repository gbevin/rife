/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CountSessionsErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class CountSessionsErrorException extends SessionManagerException
{
	private static final long serialVersionUID = -1711545850000533498L;

	public CountSessionsErrorException()
	{
		this(null);
	}

	public CountSessionsErrorException(DatabaseException cause)
	{
		super("Unable to count the sessions.", cause);
	}
}
