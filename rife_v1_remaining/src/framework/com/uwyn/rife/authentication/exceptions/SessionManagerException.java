/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SessionManagerException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.exceptions;

public class SessionManagerException extends Exception
{
	private static final long serialVersionUID = 4691297582200595999L;

	public SessionManagerException(String message)
	{
		super(message);
	}

	public SessionManagerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SessionManagerException(Throwable cause)
	{
		super(cause);
	}
}
