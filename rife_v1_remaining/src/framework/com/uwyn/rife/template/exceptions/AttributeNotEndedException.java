/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AttributeNotEndedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.datastructures.DocumentPosition;

public class AttributeNotEndedException extends SyntaxErrorException
{
	private static final long serialVersionUID = 8866294323664003598L;
	
	private String mTagType = null;
	private String mAttributeName = null;
	
	public AttributeNotEndedException(String templateName, DocumentPosition errorLocation, String tagType, String attributeName)
	{
		super(templateName, errorLocation, "the "+attributeName+" attribute of a "+tagType+" tag was not ended", null);
		
		mTagType = tagType;
		mAttributeName = attributeName;
	}

	public String getTagType()
	{
		return mTagType;
	}

	public String getAttributeName()
	{
		return mAttributeName;
	}
}
