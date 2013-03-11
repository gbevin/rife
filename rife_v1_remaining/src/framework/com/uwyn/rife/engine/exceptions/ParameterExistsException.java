/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ParameterExistsException extends EngineException
{
	private static final long serialVersionUID = 7696605273657616990L;

	private String	mDeclarationName = null;
	private String	mSubmissionName = null;
	private String	mParameterName = null;

	public ParameterExistsException(String declarationName, String parameterName, String submissionName)
	{
		super("The submission '"+submissionName+"' of element '"+declarationName+"' already contains parameter '"+parameterName+"'.");
		
		mDeclarationName = declarationName;
		mSubmissionName = submissionName;
		mParameterName = parameterName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
	
	public String getParameterName()
	{
		return mParameterName;
	}
}
