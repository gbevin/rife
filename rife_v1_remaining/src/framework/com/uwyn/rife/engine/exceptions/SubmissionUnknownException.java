/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionUnknownException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class SubmissionUnknownException extends EngineException
{
	private static final long serialVersionUID = -5915416958509548974L;
	
	private String	mDeclarationName = null;
	private String	mSubmissionName = null;

	public SubmissionUnknownException(String declarationName, String submissionName)
	{
		super("The element '"+declarationName+"' doesn't contain submission '"+submissionName+"'.");
		
		mDeclarationName = declarationName;
		mSubmissionName = submissionName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
}
