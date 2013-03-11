/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnreadableDataFormatException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.format.exceptions;

import com.uwyn.rife.cmf.MimeType;
import java.util.Collection;

public class UnreadableDataFormatException extends FormatException
{
	private static final long serialVersionUID = -8866969888137085698L;

	private MimeType			mMimeType = null;
	private Collection<String>	mErrors = null;
	
	public UnreadableDataFormatException(MimeType mimeType, Collection<String> errors)
	{
		super("Impossible to read the data that has to be stored with the mime type '"+mimeType+"'", null);
		
		mMimeType = mimeType;
		mErrors = errors;
	}
	
	public MimeType getMimeType()
	{
		return mMimeType;
	}
	
	public Collection<String> getErrors()
	{
		return mErrors;
	}
}
