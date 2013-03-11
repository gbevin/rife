/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MismatchedTerminationTagException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.datastructures.DocumentPosition;

public class MismatchedTerminationTagException extends SyntaxErrorException
{
	private static final long serialVersionUID = -5032782429826522838L;
	
	private String mTagId = null;
	private String mExpected = null;
	private String mActual = null;

	public MismatchedTerminationTagException(String templateName, DocumentPosition errorLocation, String tagId, String expected, String actual)
	{
		super(templateName, errorLocation, "the "+expected+" tag "+(tagId == null ? "" : "'"+tagId+"'")+" was wrongly terminated with a "+actual+" termination tag", null);

		mTagId = tagId;
		mExpected = expected;
		mActual = actual;
	}

	public String getTagId()
	{
		return mTagId;
	}

	public String getExpected()
	{
		return mExpected;
	}

	public String getActual()
	{
		return mActual;
	}
}
