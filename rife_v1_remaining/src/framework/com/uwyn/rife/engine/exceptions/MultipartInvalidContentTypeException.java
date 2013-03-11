/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MultipartInvalidContentTypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class MultipartInvalidContentTypeException extends MultipartRequestException
{
	private static final long serialVersionUID = 780518501619092791L;

	private String	mContentType = null;

	public MultipartInvalidContentTypeException(String contentType)
	{
		super("The content type '"+contentType+"' isn't valid for a multipart request.");
		
		mContentType = contentType;
	}
	
	public String getContentType()
	{
		return mContentType;
	}
}
