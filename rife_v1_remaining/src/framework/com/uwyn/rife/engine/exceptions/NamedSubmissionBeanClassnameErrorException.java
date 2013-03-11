/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedSubmissionBeanClassnameErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NamedSubmissionBeanClassnameErrorException extends EngineException
{
	private static final long serialVersionUID = 2323771246791646043L;

	private String	mDeclarationName = null;
	private String	mSubmissionName = null;
	private String	mBeanName = null;
	private String	mClassName = null;

	public NamedSubmissionBeanClassnameErrorException(String declarationName, String submissionName, String beanName, String className, Throwable cause)
	{
		super("The class '"+className+"' of the named bean '"+beanName+"' of submission '"+submissionName+"' in element '"+declarationName+"' couldn't be found.", cause);
		
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
