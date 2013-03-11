/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NoParametrizedQueryException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.DbPreparedStatement;

public class NoParametrizedQueryException extends DatabaseException
{
	private static final long serialVersionUID = -1606716036753773612L;

	private DbPreparedStatement	mPreparedStatement = null;

	public NoParametrizedQueryException(DbPreparedStatement statement)
	{
		super("The statement with sql '"+statement.getSql()+"' doesn't contain a parametrized query.");
		mPreparedStatement = statement;
	}

	public DbPreparedStatement getPreparedStatement()
	{
		return mPreparedStatement;
	}
}
