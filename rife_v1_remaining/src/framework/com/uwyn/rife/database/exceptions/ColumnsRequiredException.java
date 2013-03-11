/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ColumnsRequiredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class ColumnsRequiredException extends DbQueryException
{
	static final long serialVersionUID = 6643369478401322040L;

	private String mQueryName;

	public ColumnsRequiredException(String queryName)
	{
		super(queryName+" queries require columns.");

		mQueryName = queryName;
	}

	public String getQueryName()
	{
		return mQueryName;
	}
}
