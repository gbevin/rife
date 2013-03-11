/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RememberManagerException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.exceptions;

public class RememberManagerException extends Exception
{
	private static final long serialVersionUID = 3486027286626106335L;

	public RememberManagerException(String message)
	{
		super(message);
	}

	public RememberManagerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public RememberManagerException(Throwable cause)
	{
		super(cause);
	}
}
