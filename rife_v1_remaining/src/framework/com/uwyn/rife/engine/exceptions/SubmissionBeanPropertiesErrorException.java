/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionBeanPropertiesErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class SubmissionBeanPropertiesErrorException extends EngineException
{
	private static final long serialVersionUID = 9120437724779621435L;
	
	private String	mDeclarationName = null;
	private String	mSubmissionName = null;
	private String	mClassName = null;

	public SubmissionBeanPropertiesErrorException(String declarationName, String submissionName, String className, Throwable cause)
	{
		super("Unexpected error while obtaining the properties of the bean with class '"+className+"' of submission '"+submissionName+"' in element '"+declarationName+"'.", cause);
		
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
