/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RemoveRoleErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class RemoveRoleErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = 1481690228170243838L;

	private String	mName = null;
	
	public RemoveRoleErrorException(String role)
	{
		this(role, null);
	}
	
	public RemoveRoleErrorException(String name, DatabaseException cause)
	{
		super("Error while removing role with name '"+name+"'.", cause);
		mName = name;
	}
	
	public String getName()
	{
		return mName;
	}
}
