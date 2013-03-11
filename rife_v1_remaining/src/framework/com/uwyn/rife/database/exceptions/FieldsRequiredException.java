/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FieldsRequiredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class FieldsRequiredException extends DbQueryException
{
	static final long serialVersionUID = 5937549014842696343L;

	private String mQueryName;

	public FieldsRequiredException(String queryName)
	{
		super(queryName+" queries require fields.");

		mQueryName = queryName;
	}

	public String getQueryName()
	{
		return mQueryName;
	}
}
