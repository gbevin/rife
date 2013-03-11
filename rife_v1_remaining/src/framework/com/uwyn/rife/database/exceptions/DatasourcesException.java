/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatasourcesException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class DatasourcesException extends DatabaseException
{
	private static final long serialVersionUID = 6060312635494918174L;

	public DatasourcesException(String message)
	{
		super(message);
	}
	public DatasourcesException(Throwable cause)
	{
		super(cause);
	}
	public DatasourcesException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
