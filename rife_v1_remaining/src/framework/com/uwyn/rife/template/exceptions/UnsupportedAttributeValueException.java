/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedAttributeValueException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.datastructures.DocumentPosition;

public class UnsupportedAttributeValueException extends SyntaxErrorException
{
	private static final long serialVersionUID = -6852586489817324601L;
	
	String mTagType = null;
	String mTagId = null;
	String mAttributeName = null;
	String mAttributeValue = null;

	public UnsupportedAttributeValueException(String templateName, DocumentPosition errorLocation, String tagType, String tagId, String attributeName, String attributeValue)
	{
		super(templateName, errorLocation, "the "+attributeName+" attribute value '"+attributeValue+"' of the "+tagType+" tag '"+tagId+"' is not supported", null);

		mTagType = tagType;
		mTagId = tagId;
		mAttributeName = attributeName;
		mAttributeValue = attributeValue;
	}

	public String getTagType()
	{
		return mTagType;
	}

	public String getTagId()
	{
		return mTagId;
	}

	public String getAttributeName()
	{
		return mAttributeName;
	}

	public String getAttributeValue()
	{
		return mAttributeValue;
	}
}
