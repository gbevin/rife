/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: IncludeNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.datastructures.DocumentPosition;

public class IncludeNotFoundException extends ProcessingException
{
	private static final long serialVersionUID = -8132650494489046526L;
	
	private String				mTemplateName = null;
	private DocumentPosition	mErrorLocation = null;
	private String 				mIncluded = null;

	public IncludeNotFoundException(String templateName, DocumentPosition errorLocation, String included)
	{
		super(formatError(templateName, errorLocation, "couldn't find the included template '"+included+"'"));

		mTemplateName = templateName;
		mErrorLocation = errorLocation;
		mIncluded = included;
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
}
