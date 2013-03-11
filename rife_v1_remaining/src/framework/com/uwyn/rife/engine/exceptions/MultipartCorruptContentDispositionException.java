/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MultipartCorruptContentDispositionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class MultipartCorruptContentDispositionException extends MultipartRequestException
{
	private static final long serialVersionUID = 4589739604600239664L;

	private String	mContentDisposition = null;

	public MultipartCorruptContentDispositionException(String contentDisposition)
	{
		super("The content disposition line '"+contentDisposition+"' is corrupt.");
		
		mContentDisposition = contentDisposition;
	}
	
	public String getContentType()
	{
		return mContentDisposition;
	}
}
