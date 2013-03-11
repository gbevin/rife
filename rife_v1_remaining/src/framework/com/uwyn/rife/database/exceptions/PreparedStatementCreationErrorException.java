/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PreparedStatementCreationErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.Datasource;

public class PreparedStatementCreationErrorException extends DatabaseException
{
	private static final long serialVersionUID = 527710892636948049L;

	private Datasource	mDatasource = null;

	public PreparedStatementCreationErrorException(Datasource datasource, Throwable cause)
	{
		super("Couldn't create a new prepared statement.", cause);
		mDatasource = datasource;
	}

	public Datasource getDatasource()
	{
		return mDatasource;
	}
}
