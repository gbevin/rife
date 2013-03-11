/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OutbeanGroupRequiresValidatedConstrainedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class OutbeanGroupRequiresValidatedConstrainedException extends EngineException
{
	private static final long serialVersionUID = 7067345443383952327L;

	private String	mDeclarationName = null;
	private String	mClassName = null;
	private String	mGroupName = null;
	
	public OutbeanGroupRequiresValidatedConstrainedException(String declarationName, String className, String groupName)
	{
		super("The element '"+declarationName+"' declared the output bean with the group '"+groupName+"', however its class '"+className+"' doesn't implement ValidatedConstrained.");
		
		mDeclarationName = declarationName;
		mClassName = className;
		mGroupName = groupName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
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
