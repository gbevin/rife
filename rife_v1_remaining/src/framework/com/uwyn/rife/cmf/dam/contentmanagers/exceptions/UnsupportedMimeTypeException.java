/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedMimeTypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentmanagers.exceptions;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;

public class UnsupportedMimeTypeException extends ContentManagerException
{
	private static final long serialVersionUID = 2504591167417035649L;

	private MimeType	mMimeType = null;
	
	public UnsupportedMimeTypeException(MimeType mimeType)
	{
		super("The mime type '"+mimeType+"' isn't supported by the content manager.");
		
		mMimeType = mimeType;
	}
	
	public MimeType getMimeType()
	{
		return mMimeType;
	}
}
