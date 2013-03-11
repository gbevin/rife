/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TransformedResultConversionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

public class TransformedResultConversionException extends ProcessingException
{
	private static final long serialVersionUID = -4796743001867603162L;
	
	private String mPath = null;

	public TransformedResultConversionException(String path, Throwable cause)
	{
		super("Error while converting the transformed result of the template '"+path+"'.", cause);
		mPath = path;
	}

	public String getPath()
	{
		return mPath;
	}
}
