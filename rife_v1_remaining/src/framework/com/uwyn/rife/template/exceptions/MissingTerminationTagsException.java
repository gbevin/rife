/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingTerminationTagsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.datastructures.DocumentPosition;

public class MissingTerminationTagsException extends SyntaxErrorException
{
	private static final long serialVersionUID = -205869856601005663L;
	
	private String mTagType = null;

	public MissingTerminationTagsException(String templateName, DocumentPosition errorLocation, String tagType)
	{
		super(templateName, errorLocation, "the "+tagType+" tags were not all terminated", null);

		mTagType = tagType;
	}

	
	public String getTagType()
	{
		return mTagType;
	}
}
