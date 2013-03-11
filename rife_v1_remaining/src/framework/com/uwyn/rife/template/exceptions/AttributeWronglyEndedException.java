/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AttributeWronglyEndedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.datastructures.DocumentPosition;

public class AttributeWronglyEndedException extends SyntaxErrorException
{
	private static final long serialVersionUID = -7697178038103069291L;
	
	private String mTagType = null;
	private String mAttributeName = null;
	
	public AttributeWronglyEndedException(String templateName, DocumentPosition errorLocation, String tagType, String attributeName)
	{
		super(templateName, errorLocation, "the "+attributeName+" attribute of a "+tagType+" tag was ended with a delimiter while none was used to start it", null);
		
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
