/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DriverNameRetrievalErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class DriverNameRetrievalErrorException extends DatabaseException
{
	static final long serialVersionUID = -2809651374321112986L;

	public DriverNameRetrievalErrorException(Throwable cause)
	{
		super("Unexpected error while retrieving the driver name of a JDBC connection.", cause);
	}
}
