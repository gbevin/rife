/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RowProcessorErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class RowProcessorErrorException extends DatabaseException
{
	private static final long serialVersionUID = -5597696130038426852L;

	public RowProcessorErrorException(Throwable cause)
	{
		super("An error occurred while processing a resultset row.", cause);
	}
}
