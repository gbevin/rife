/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExecutionErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.Datasource;

public class ExecutionErrorException extends DatabaseException
{
	private static final long serialVersionUID = 4317171502649179520L;

	private String		mSql = null;
	private Datasource	mDatasource = null;

	public ExecutionErrorException(String sql, Datasource datasource, Throwable cause)
	{
		super("Error while executing the SQL '"+sql+"'.", cause);
		mSql = sql;
		mDatasource = datasource;
	}

	public Datasource getDatasource()
	{
		return mDatasource;
	}

	public String getSql()
	{
		return mSql;
	}
}
