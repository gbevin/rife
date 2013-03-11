/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SequenceNameRequiredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class SequenceNameRequiredException extends DbQueryException
{
	static final long serialVersionUID = -1117694732120142775L;

	private String mQueryName;
		
	public SequenceNameRequiredException(String queryName)
	{
		super(queryName+" queries require a sequence name.");
		
		mQueryName = queryName;
	}

	public String getQueryName()
	{
		return mQueryName;
	}
}
