/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StatementCloseErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.Datasource;

public class StatementCloseErrorException extends DatabaseException
{
	private static final long serialVersionUID = -4874100206556310884L;

	private Datasource	mDatasource = null;

	public StatementCloseErrorException(Datasource datasource, Throwable cause)
	{
		super("Couldn't close the statement.", cause);
		mDatasource = datasource;
	}

	public Datasource getDatasource()
	{
		return mDatasource;
	}
}
