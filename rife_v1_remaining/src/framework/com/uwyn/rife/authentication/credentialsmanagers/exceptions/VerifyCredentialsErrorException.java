/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: VerifyCredentialsErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.Credentials;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;

public class VerifyCredentialsErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = -8816867018874678942L;

	private Credentials	mCredentials = null;
	
	public VerifyCredentialsErrorException(Credentials credentials)
	{
		this(credentials, null);
	}
	
	public VerifyCredentialsErrorException(Credentials credentials, Throwable cause)
	{
		super("Error while verifying the credentials.", cause);
		mCredentials = credentials;
	}
	
	public Credentials getCredentials()
	{
		return mCredentials;
	}
}
