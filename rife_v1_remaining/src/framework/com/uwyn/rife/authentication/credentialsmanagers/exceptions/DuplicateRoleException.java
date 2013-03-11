/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DuplicateRoleException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;

public class DuplicateRoleException extends CredentialsManagerException
{
	private static final long serialVersionUID = 3859722464041667678L;

	private String	mRole = null;
	
	public DuplicateRoleException(String role)
	{
		super("The role '"+role+"' is already present.");
		
		mRole = role;
	}
	
	public String getRole()
	{
		return mRole;
	}
}
