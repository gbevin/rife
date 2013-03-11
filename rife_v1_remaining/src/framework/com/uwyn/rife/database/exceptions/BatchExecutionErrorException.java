/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BatchExecutionErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.Datasource;

public class BatchExecutionErrorException extends DatabaseException
{
	private static final long serialVersionUID = 7946011449481688333L;

	private Datasource	mDatasource = null;

	public BatchExecutionErrorException(Datasource datasource, Throwable cause)
	{
		super("Error while executing the batch sql commands.", cause);
		mDatasource = datasource;
	}

	public Datasource getDatasource()
	{
		return mDatasource;
	}
}
