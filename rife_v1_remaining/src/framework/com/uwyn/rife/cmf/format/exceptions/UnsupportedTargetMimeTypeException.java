/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedTargetMimeTypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.format.exceptions;

import com.uwyn.rife.cmf.MimeType;

public class UnsupportedTargetMimeTypeException extends FormatException
{
	private static final long serialVersionUID = -8908623401198633615L;

	private MimeType	mMimeType = null;
	
	public UnsupportedTargetMimeTypeException(MimeType mimeType)
	{
		super("Unsupported target mime type '"+mimeType+"'", null);
		
		mMimeType = mimeType;
	}
	
	public MimeType getMimeType()
	{
		return mMimeType;
	}
}
