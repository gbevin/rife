/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MultipartCorruptContentTypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class MultipartCorruptContentTypeException extends MultipartRequestException
{
	private static final long serialVersionUID = 4867933809123299599L;

	private String	mContentType = null;

	public MultipartCorruptContentTypeException(String contentType)
	{
		super("The content type line '"+contentType+"' is corrupt.");
		
		mContentType = contentType;
	}
	
	public String getContentType()
	{
		return mContentType;
	}
}
