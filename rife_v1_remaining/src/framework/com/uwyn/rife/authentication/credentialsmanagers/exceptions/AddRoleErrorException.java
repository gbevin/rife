/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AddRoleErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class AddRoleErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = 5644080575833305836L;
	
	private String	mRole = null;
	
	public AddRoleErrorException(String role)
	{
		this(role, null);
	}
	
	public AddRoleErrorException(String role, DatabaseException cause)
	{
		super("Error while adding role '"+role+"'.", cause);
		mRole = role;
	}
	
	public String getRole()
	{
		return mRole;
	}
}
