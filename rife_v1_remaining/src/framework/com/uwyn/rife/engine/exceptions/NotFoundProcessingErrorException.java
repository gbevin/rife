/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NotFoundProcessingErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NotFoundProcessingErrorException extends EngineException
{
	private static final long serialVersionUID = 4016869783059413526L;

	private String	mType = null;
	private String	mResourcePath = null;
	
	public NotFoundProcessingErrorException(String type, String resourcePath, Throwable cause)
	{
		super("Error while processing the "+type+" '"+resourcePath+"', it couldn't be found.", cause);
		
		mType = type;
		mResourcePath = resourcePath;
	}

	public String getType()
	{
		return mType;
	}
	
	public String getResourcePath()
	{
		return mResourcePath;
	}
}
