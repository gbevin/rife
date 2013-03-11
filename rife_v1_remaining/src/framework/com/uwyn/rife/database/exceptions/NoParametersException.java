/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NoParametersException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.DbPreparedStatement;

public class NoParametersException extends DatabaseException
{
	private static final long serialVersionUID = -2087220322509692913L;

	private DbPreparedStatement	mPreparedStatement = null;

	public NoParametersException(DbPreparedStatement statement)
	{
		super("The statement with sql '"+statement.getSql()+"' doesn't contain any parameters.");
		mPreparedStatement = statement;
	}

	public DbPreparedStatement getPreparedStatement()
	{
		return mPreparedStatement;
	}
}
