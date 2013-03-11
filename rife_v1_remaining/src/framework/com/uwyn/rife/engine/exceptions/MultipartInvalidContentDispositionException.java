/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MultipartInvalidContentDispositionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class MultipartInvalidContentDispositionException extends MultipartRequestException
{
	private static final long serialVersionUID = 6757637387303238042L;

	private String	mContentDisposition = null;

	public MultipartInvalidContentDispositionException(String contentDisposition)
	{
		super("The content disposition '"+contentDisposition+"' isn't valid.");
		
		mContentDisposition = contentDisposition;
	}
	
	public String getContentType()
	{
		return mContentDisposition;
	}
}
