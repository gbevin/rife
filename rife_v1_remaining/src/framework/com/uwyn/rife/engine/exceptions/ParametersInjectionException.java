/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParametersInjectionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ParametersInjectionException extends EngineException
{
	private static final long serialVersionUID = -7452442554877557000L;

	private String	mDeclarationName = null;
	private Class	mElementClass = null;
	private String	mSubmissionName = null;
	
	public ParametersInjectionException(String declarationName, Class elementClass, String submissionName, Throwable e)
	{
		super("An error occurred while injecting the values for the parameters of submission '"+submissionName+"' of element '"+declarationName+"' into class '"+elementClass.getName()+"'.", e);
		
		mDeclarationName = declarationName;
		mElementClass = elementClass;
		mSubmissionName = submissionName;
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
}
