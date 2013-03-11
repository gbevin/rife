/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MultipartFileTooBigException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class MultipartFileTooBigException extends MultipartRequestException
{
	private static final long serialVersionUID = -649024432766475910L;

	private String	mFileName = null;
	private long	mSizeLimit = 0;

	public MultipartFileTooBigException(String fileName, long sizeLimit)
	{
		super("The size of the uploaded file '"+fileName+"' exceeds "+sizeLimit+" which is the maximum.");
		
		mFileName = fileName;
		mSizeLimit = sizeLimit;
	}
	
	public String getFileName()
	{
		return mFileName;
	}
	
	public long getSizeLimit()
	{
		return mSizeLimit;
	}
}
