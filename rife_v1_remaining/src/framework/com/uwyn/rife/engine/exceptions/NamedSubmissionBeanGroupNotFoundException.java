/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedSubmissionBeanGroupNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NamedSubmissionBeanGroupNotFoundException extends EngineException
{
	private static final long serialVersionUID = 1594365480539237167L;

	private String	mDeclarationName = null;
	private String	mSubmissionName = null;
	private String	mBeanName = null;
	private String	mClassName = null;
	private String	mGroupName = null;
	
	public NamedSubmissionBeanGroupNotFoundException(String declarationName, String submissionName, String beanName, String className, String groupName)
	{
		super("The element '"+declarationName+"' declared the named bean '"+beanName+"' in submission '"+submissionName+"' with the class '"+className+"' and the group '"+groupName+"', however the group couldn't be found.");
		
		mDeclarationName = declarationName;
		mSubmissionName = submissionName;
		mBeanName = beanName;
		mClassName = className;
		mGroupName = groupName;
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
	
	public String getGroupName()
	{
		return mGroupName;
	}
}
