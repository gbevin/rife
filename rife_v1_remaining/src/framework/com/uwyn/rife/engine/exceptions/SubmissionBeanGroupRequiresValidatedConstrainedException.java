/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionBeanGroupRequiresValidatedConstrainedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class SubmissionBeanGroupRequiresValidatedConstrainedException extends EngineException
{
	private static final long serialVersionUID = -5077797557350922842L;

	private String	mDeclarationName = null;
	private String	mSubmissionName = null;
	private String	mClassName = null;
	private String	mGroupName = null;
	
	public SubmissionBeanGroupRequiresValidatedConstrainedException(String declarationName, String submissionName, String className, String groupName)
	{
		super("The element '"+declarationName+"' declared the bean in submission '"+submissionName+"' with the group '"+groupName+"', however its class '"+className+"' doesn't implement ValidatedConstrained.");
		
		mDeclarationName = declarationName;
		mSubmissionName = submissionName;
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
	
	public String getClassName()
	{
		return mClassName;
	}
	
	public String getGroupName()
	{
		return mGroupName;
	}
}
