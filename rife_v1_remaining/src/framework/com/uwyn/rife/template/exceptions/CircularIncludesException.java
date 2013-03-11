/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CircularIncludesException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.datastructures.DocumentPosition;
import com.uwyn.rife.tools.StringUtils;
import java.util.Stack;

public class CircularIncludesException extends ProcessingException
{
	private static final long serialVersionUID = 3472405981484449892L;
	
	private String				mTemplateName = null;
	private DocumentPosition	mErrorLocation = null;
	private String				mIncluded = null;
	private Stack<String>		mPreviousIncludes = null;

	public CircularIncludesException(String templateName, DocumentPosition errorLocation, String included, Stack<String> previousIncludes)
	{
		super(formatError(templateName, errorLocation, "the template '"+included+"' has already been included, the include stack was : '"+StringUtils.join(previousIncludes,", ")+"'"));
		
		mTemplateName = templateName;
		mErrorLocation = errorLocation;
		mIncluded = included;
		mPreviousIncludes = previousIncludes;
	}
	
	public String getTemplateName()
	{
		return mTemplateName;
	}
	
	public DocumentPosition getErrorLocation()
	{
		return mErrorLocation;
	}
	
	public String getIncluded()
	{
		return mIncluded;
	}

	public Stack<String> getPreviousIncludes()
	{
		return mPreviousIncludes;
	}
}
