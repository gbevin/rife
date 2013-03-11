/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CredentialsManagerException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.exceptions;

public class CredentialsManagerException extends RuntimeException
{
	private static final long serialVersionUID = 1084302907754518421L;

	public CredentialsManagerException(String message)
	{
		super(message);
	}

	public CredentialsManagerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CredentialsManagerException(Throwable cause)
	{
		super(cause);
	}
}
