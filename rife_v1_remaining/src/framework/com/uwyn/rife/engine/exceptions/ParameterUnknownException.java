/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterUnknownException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ParameterUnknownException extends EngineException
{
	private static final long serialVersionUID = -8729881801021087377L;

	private String	mDeclarationName = null;
	private String	mParameterName = null;

	public ParameterUnknownException(String declarationName, String parameterName)
	{
		super("The element '"+declarationName+"' doesn't contain parameter '"+parameterName+"' in any submission.");
		
		mDeclarationName = declarationName;
		mParameterName = parameterName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getParameterName()
	{
		return mParameterName;
	}
}
