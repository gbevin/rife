/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedVirtualParameterTypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.DbPreparedStatement;

public class UnsupportedVirtualParameterTypeException extends DatabaseException
{
	private static final long serialVersionUID = -4366883446335774838L;

	private DbPreparedStatement	mPreparedStatement = null;
	private int					mParameterIndex = -1;
	private String				mValueType = null;

	public UnsupportedVirtualParameterTypeException(DbPreparedStatement statement, int parameterIndex, String valueType)
	{
		super("The statement with sql '"+statement.getSql()+"' doesn't support the value type '"+valueType+"' for the virtual parameter with index '"+parameterIndex+"'.");
		mPreparedStatement = statement;
		mParameterIndex = parameterIndex;
		mValueType = valueType;
	}

	public DbPreparedStatement getPreparedStatement()
	{
		return mPreparedStatement;
	}
	
	public int getParameterIndex()
	{
		return mParameterIndex;
	}
	
	public String getValueType()
	{
		return mValueType;
	}
}
