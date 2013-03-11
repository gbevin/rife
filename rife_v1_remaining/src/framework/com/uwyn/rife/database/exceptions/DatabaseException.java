/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class DatabaseException extends RuntimeException
{
	private static final long serialVersionUID = -8915821806051354310L;

	public DatabaseException(String message)
	{
		super(message);
	}

	public DatabaseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DatabaseException(Throwable cause)
	{
		super(cause);
	}
}
