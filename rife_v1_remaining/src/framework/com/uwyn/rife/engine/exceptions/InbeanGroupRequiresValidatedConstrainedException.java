/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InbeanGroupRequiresValidatedConstrainedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class InbeanGroupRequiresValidatedConstrainedException extends EngineException
{
	private static final long serialVersionUID = -2816314485367732244L;

	private String	mDeclarationName = null;
	private String	mClassName = null;
	private String	mGroupName = null;
	
	public InbeanGroupRequiresValidatedConstrainedException(String declarationName, String className, String groupName)
	{
		super("The element '"+declarationName+"' declared the input bean with the group '"+groupName+"', however its class '"+className+"' doesn't implement ValidatedConstrained.");
		
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
