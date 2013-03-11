/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedCredentialsTypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.Credentials;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;

public class UnsupportedCredentialsTypeException extends CredentialsManagerException
{
	private static final long serialVersionUID = 6083937124194075522L;

	private Credentials	mCredentials = null;
	
	public UnsupportedCredentialsTypeException(Credentials credentials)
	{
		super("The credentials with type '"+credentials.getClass().getName()+"' aren't supported.");
		mCredentials = credentials;
	}
	
	public Credentials getCredentials()
	{
		return mCredentials;
	}
}
