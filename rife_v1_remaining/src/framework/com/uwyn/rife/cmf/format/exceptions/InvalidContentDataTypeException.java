/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InvalidContentDataTypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.format.exceptions;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.format.Formatter;

public class InvalidContentDataTypeException extends FormatException
{
	private static final long serialVersionUID = -8080073439600024252L;

	private Formatter	mFormatter = null;
	private MimeType	mMimeType = null;
	private Class		mExpectedType = null;
	private Class		mReceivedType = null;
	
	public InvalidContentDataTypeException(Formatter formatter, MimeType mimeType, Class expectedType, Class receivedType)
	{
		super("The formatter '"+formatter.getClass().getName()+"' received content with mime type '"+mimeType+"' and the data should have been of type '"+expectedType.getName()+"', instead it was '"+receivedType.getName()+"'.", null);
		
		mFormatter = formatter;
		mMimeType = mimeType;
		mExpectedType = expectedType;
		mReceivedType = receivedType;
	}
	
	public Formatter getFormatter()
	{
		return mFormatter;
	}
	
	public MimeType getMimeType()
	{
		return mMimeType;
	}
	
	public Class getExpectedType()
	{
		return mExpectedType;
	}
	
	public Class getReceivedType()
	{
		return mReceivedType;
	}
}
