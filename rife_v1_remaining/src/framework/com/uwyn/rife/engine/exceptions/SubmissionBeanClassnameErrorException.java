/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionBeanClassnameErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class SubmissionBeanClassnameErrorException extends EngineException
{
	private static final long serialVersionUID = 335092809557396834L;

	private String	mDeclarationName = null;
	private String	mSubmissionName = null;
	private String	mClassName = null;

	public SubmissionBeanClassnameErrorException(String declarationName, String submissionName, String className, Throwable cause)
	{
		super("The class '"+className+"' of a bean of submission '"+submissionName+"' in element '"+declarationName+"' couldn't be found.", cause);
		
		mDeclarationName = declarationName;
		mSubmissionName = submissionName;
		mClassName = className;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
	
	public String getClassName()
	{
		return mClassName;
	}
}
