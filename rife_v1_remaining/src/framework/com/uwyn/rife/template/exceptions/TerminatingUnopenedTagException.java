/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TerminatingUnopenedTagException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.datastructures.DocumentPosition;

public class TerminatingUnopenedTagException extends SyntaxErrorException
{
	private static final long serialVersionUID = 2455635260743666566L;
	
	private String mTagType = null;

	public TerminatingUnopenedTagException(String templateName, DocumentPosition errorLocation, String tagType)
	{
		super(templateName, errorLocation, "a '"+tagType+"' tag is being terminated while it was never opened", null);
		
		mTagType = tagType;
	}

	public String getTagType()
	{
		return mTagType;
	}
}
