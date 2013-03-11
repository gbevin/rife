/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterInjectionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ParameterInjectionException extends EngineException
{
	private static final long serialVersionUID = -7879149913448690863L;

	private String	mDeclarationName = null;
	private Class	mElementClass = null;
	private String	mSubmissionName = null;
	private String	mParameterName = null;
	
	public ParameterInjectionException(String declarationName, Class elementClass, String submissionName, String parameterName, Throwable e)
	{
		super("An error occurred while injecting the values for the parameter '"+parameterName+"' of submission '"+submissionName+"' of element '"+declarationName+"' into class '"+elementClass.getName()+"'.", e);
		
		mDeclarationName = declarationName;
		mElementClass = elementClass;
		mSubmissionName = submissionName;
		mParameterName = parameterName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public Class getElementClass()
	{
		return mElementClass;
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
