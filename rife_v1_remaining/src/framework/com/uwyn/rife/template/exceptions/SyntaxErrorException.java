/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SyntaxErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.datastructures.DocumentPosition;

public class SyntaxErrorException extends TemplateException
{
	private static final long serialVersionUID = 7687067640946628293L;

	private String				mTemplateName = null;
	private DocumentPosition	mErrorLocation = null;

	public SyntaxErrorException(String templateName, DocumentPosition errorLocation, String message, Throwable cause)
	{
		super(formatError(templateName, errorLocation, message), cause);

		mTemplateName = templateName;
		mErrorLocation = errorLocation;
	}
	
	public String getTemplateName()
	{
		return mTemplateName;
	}
	
	public DocumentPosition getErrorLocation()
	{
		return mErrorLocation;
	}
}
