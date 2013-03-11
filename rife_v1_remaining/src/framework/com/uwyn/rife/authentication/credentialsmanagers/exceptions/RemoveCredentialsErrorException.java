/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RemoveCredentialsErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class RemoveCredentialsErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = 8496417459907476229L;

	public RemoveCredentialsErrorException()
	{
		this(null);
	}

	public RemoveCredentialsErrorException(DatabaseException cause)
	{
		super("Can't remove the credentials database structure.", cause);
	}
}
