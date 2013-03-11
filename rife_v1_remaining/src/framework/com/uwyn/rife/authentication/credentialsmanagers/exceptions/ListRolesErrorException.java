/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ListRolesErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class ListRolesErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = 5960821338157810627L;

	public ListRolesErrorException(DatabaseException cause)
	{
		super("Error while listing all the roles.", cause);
	}
}
