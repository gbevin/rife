/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TableNameRequiredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class TableNameRequiredException extends DbQueryException
{
	static final long serialVersionUID = 5815362326172483731L;

	private String mQueryName;
	
	public TableNameRequiredException(String queryName)
	{
		super(queryName+" queries require a table name.");
		
		mQueryName = queryName;
	}

	public String getQueryName()
	{
		return mQueryName;
	}
}
