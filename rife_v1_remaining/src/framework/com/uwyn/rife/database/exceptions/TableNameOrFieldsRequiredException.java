/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TableNameOrFieldsRequiredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class TableNameOrFieldsRequiredException extends DbQueryException
{
	static final long serialVersionUID = -1252775241150915434L;

	private String mQueryName;

	public TableNameOrFieldsRequiredException(String queryName)
	{
		super(queryName+" queries require a table name or fields.");

		mQueryName = queryName;
	}

	public String getQueryName()
	{
		return mQueryName;
	}
}
