/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CountRolesErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class CountRolesErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = -8457306602256031431L;

	public CountRolesErrorException()
	{
		this(null);
	}

	public CountRolesErrorException(DatabaseException cause)
	{
		super("Error while counting the roles.", cause);
	}
}
