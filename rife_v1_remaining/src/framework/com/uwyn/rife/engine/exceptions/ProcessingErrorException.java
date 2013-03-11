/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ProcessingErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ProcessingErrorException extends EngineException
{
	private static final long serialVersionUID = -4205944769417229563L;

	private String	mType = null;
	private String	mResourcePath = null;
	
	public ProcessingErrorException(String type, String resourcePath, Throwable cause)
	{
		this(type, resourcePath, null, cause);
	}
	
	public ProcessingErrorException(String type, String resourcePath, String message, Throwable cause)
	{
		super("Error while processing the "+type+" '"+resourcePath+"'"+(null == message ? "." : " : "+message), cause);
		
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
