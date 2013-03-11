/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PurgeRememberIdsErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.remembermanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.RememberManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class PurgeRememberIdsErrorException extends RememberManagerException
{
	private static final long serialVersionUID = -309361946618471471L;

	public PurgeRememberIdsErrorException()
	{
		this(null);
	}
	
	public PurgeRememberIdsErrorException(DatabaseException cause)
	{
		super("Unable to purge the expired remember IDs.", cause);
	}
}
