/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TagBadlyTerminatedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.datastructures.DocumentPosition;

public class TagBadlyTerminatedException extends SyntaxErrorException
{
	private static final long serialVersionUID = 6462207336960276066L;
	
	private String mTagType = null;
	private String mTagId = null;

	public TagBadlyTerminatedException(String templateName, DocumentPosition errorLocation, String tagType, String tagId)
	{
		super(templateName, errorLocation, "the "+tagType+" tag '"+tagId+"' was badly terminated, only whitespace is allowed between the name delimiter and the tag termination", null);

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
