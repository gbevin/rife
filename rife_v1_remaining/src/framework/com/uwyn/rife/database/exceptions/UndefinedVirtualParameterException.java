/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UndefinedVirtualParameterException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.DbPreparedStatement;

public class UndefinedVirtualParameterException extends DatabaseException
{
	private static final long serialVersionUID = -7004752430133818652L;

	private DbPreparedStatement	mPreparedStatement = null;
	private String				mParameterName = null;
	private int					mParameterIndex = -1;

	public UndefinedVirtualParameterException(DbPreparedStatement statement, String parameterName)
	{
		super("The statement with sql '"+statement.getSql()+"' requires the definition of a value for the virtual parameter with name '"+parameterName+"'.");
		mPreparedStatement = statement;
		mParameterName = parameterName;
	}

	public UndefinedVirtualParameterException(DbPreparedStatement statement, int parameterIndex)
	{
		super("The statement with sql '"+statement.getSql()+"' requires the definition of a value for the virtual parameter with index '"+parameterIndex+"'.");
		mPreparedStatement = statement;
		mParameterIndex = parameterIndex;
	}

	public DbPreparedStatement getPreparedStatement()
	{
		return mPreparedStatement;
	}
	
	public String getParameterName()
	{
		return mParameterName;
	}
	
	public int getParameterIndex()
	{
		return mParameterIndex;
	}
}
