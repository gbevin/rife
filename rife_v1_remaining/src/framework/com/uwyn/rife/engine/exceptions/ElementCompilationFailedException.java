/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementCompilationFailedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementCompilationFailedException extends EngineException
{
	private static final long serialVersionUID = -8488706811970241938L;
	
	private String	mSourceFilename = null;
	private String	mExitName = null;

	public ElementCompilationFailedException(String sourceFilename, String errors, Throwable cause)
	{
		super(null == errors ? sourceFilename : errors, cause);
		
		mSourceFilename = sourceFilename;
		mExitName = errors;
	}
	
	public String getSourceFilename()
	{
		return mSourceFilename;
	}
	
	public String getErrors()
	{
		return mExitName;
	}
}
