/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SessionValidatorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.exceptions;

public class SessionValidatorException extends Exception
{
	private static final long serialVersionUID = -6033104555814346647L;

	public SessionValidatorException(String message)
	{
		super(message);
	}

	public SessionValidatorException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SessionValidatorException(Throwable cause)
	{
		super(cause);
	}
}
