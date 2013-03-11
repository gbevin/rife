/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InstallCredentialsErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class InstallCredentialsErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = 5098527539817113840L;

	public InstallCredentialsErrorException()
	{
		this(null);
	}

	public InstallCredentialsErrorException(DatabaseException cause)
	{
		super("Can't install the credentials database structure.", cause);
	}
}
