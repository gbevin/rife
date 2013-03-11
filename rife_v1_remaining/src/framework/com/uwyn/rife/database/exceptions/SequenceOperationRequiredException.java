/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SequenceOperationRequiredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class SequenceOperationRequiredException extends DbQueryException
{
	static final long serialVersionUID = -4800820909278366194L;

	private String mQueryName;

	public SequenceOperationRequiredException(String queryName)
	{
		super(queryName+" queries require a sequence operation to be provided.");

		mQueryName = queryName;
	}

	public String getQueryName()
	{
		return mQueryName;
	}
}
