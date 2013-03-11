/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DbQueryException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class DbQueryException extends RuntimeException
{
	private static final long serialVersionUID = -2143066136860048063L;

	public DbQueryException(String message)
	{
		super(message);
	}

	public DbQueryException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
