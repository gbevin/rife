/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeginTagNotEndedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.datastructures.DocumentPosition;

public class BeginTagNotEndedException extends SyntaxErrorException
{
	private static final long serialVersionUID = -2326812741726875736L;
	
	private String mTagType = null;
	private String mTagId = null;

	public BeginTagNotEndedException(String templateName, DocumentPosition errorLocation, String tagType, String tagId)
	{
		super(templateName, errorLocation, "the begin tag of "+tagType+" '"+tagId+"' wasn't correctly ended", null);

		mTagType = tagType;
		mTagId = tagId;
	}

	public String getTagType()
	{
		return mTagType;
	}

	public String getTagId()
	{
		return mTagId;
	}
}
