/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterDoesntExistException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.DbPreparedStatement;

public class ParameterDoesntExistException extends DatabaseException
{
	private static final long serialVersionUID = -5547694215702755839L;

	private DbPreparedStatement	mPreparedStatement = null;
	private String				mParameterName = null;

	public ParameterDoesntExistException(DbPreparedStatement statement, String parameterName)
	{
		super("The statement with sql '"+statement.getSql()+"' doesn't contain the parameter '"+parameterName+"'.");
		mPreparedStatement = statement;
		mParameterName = parameterName;
	}

	public DbPreparedStatement getPreparedStatement()
	{
		return mPreparedStatement;
	}
	
	public String getParameterName()
	{
		return mParameterName;
	}
}
