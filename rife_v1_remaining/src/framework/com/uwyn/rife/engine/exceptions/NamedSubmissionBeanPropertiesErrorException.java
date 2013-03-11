/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedSubmissionBeanPropertiesErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NamedSubmissionBeanPropertiesErrorException extends EngineException
{
	private static final long serialVersionUID = 2827160429495848251L;

	private String	mDeclarationName = null;
	private String	mSubmissionName = null;
	private String	mBeanName = null;
	private String	mClassName = null;

	public NamedSubmissionBeanPropertiesErrorException(String declarationName, String submissionName, String beanName, String className, Throwable cause)
	{
		super("Unexpected error while obtaining the properties of the named bean '"+beanName+"' with class '"+className+"' of submission '"+submissionName+"' in element '"+declarationName+"'.", cause);
		
		mDeclarationName = declarationName;
		mSubmissionName = submissionName;
		mBeanName = beanName;
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
	
	public String getBeanName()
	{
		return mBeanName;
	}
	
	public String getClassName()
	{
		return mClassName;
	}
}
